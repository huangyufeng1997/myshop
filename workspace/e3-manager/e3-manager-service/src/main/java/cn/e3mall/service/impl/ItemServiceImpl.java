package cn.e3mall.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service
 * @author Carl
 *
 */
@Service
public class ItemServiceImpl implements ItemService{
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	 
	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		//查询缓存
		try {
			String string = jedisClient.get(REDIS_ITEM_PRE+":"+itemId+":BASE");
			if(StringUtils.isNotBlank(string)) {
				TbItem item = JsonUtils.jsonToPojo(string, TbItem.class);
				return item;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存没有则查询数据库
		//根据主键查询
		//TbItem tbItem=itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		
		if(list!=null&&list.size()>0) {
			//把结果添加进缓存
			try {
				jedisClient.set(REDIS_ITEM_PRE+":"+itemId+":BASE", JsonUtils.objectToJson(list.get(0)));
				//设置过期时间
				jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+":BASE", ITEM_CACHE_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list.get(0);
		}

		return null;
	}
	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		//设置分页
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		
		EasyUIDataGridResult result =new EasyUIDataGridResult();
		result.setRows(list);
		//分页
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		Integer total = (int) pageInfo.getTotal();
		result.setTotal(total);
		return result;
	}
	@Override
	public E3Result addItem(TbItem item, String desc) {
		//生成商品id
		final long itemId = IDUtils.genItemId();
		//补全TbItem属性
		item.setId(itemId);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		//向商品插入数据
		itemMapper.insert(item);
		//创建TbItemDesc对象
		TbItemDesc itemDesc = new TbItemDesc();
		//补全TbItemDesc属性
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(itemId);
		//向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//发送商品添加消息
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(itemId+"");
				return textMessage;
			}
		});
		return E3Result.ok();
	}
	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//查询缓存
		try {
			String string = jedisClient.get(REDIS_ITEM_PRE+":"+itemId+":DESC");
			if(StringUtils.isNotBlank(string)) {
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(string, TbItemDesc.class);
				return itemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//把结果添加进缓存
		try {
			jedisClient.set(REDIS_ITEM_PRE+":"+itemId+":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间
			jedisClient.expire(REDIS_ITEM_PRE+":"+itemId+":DESC", ITEM_CACHE_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
	
	
	@Override
	public E3Result deleteBatch(String ids) {
		//判断ids不为空
	    if(StringUtils.isNoneBlank(ids)){
	        //分割ids
	        String[] split = ids.split(",");
	        for ( String id : split ) {
	            itemMapper.deleteByPrimaryKey(Long.valueOf(id));
	            itemDescMapper.deleteByPrimaryKey(Long.valueOf(id));
	        }
	        return E3Result.ok();
	    }
		return null;
	}
	
	@Override
	public E3Result productShelves(String ids) {
		//判断ids不为空
		if(StringUtils.isNoneBlank(ids)) {
			String[] split = ids.split(",");
			for (String id : split) {
				//通过id查询商品
				TbItem item = itemMapper.selectByPrimaryKey(Long.valueOf(id));
				//更新时间
				Date date = new Date();
				item.setUpdated(date);
				//商品状态,1-正常 2-下架 3-删除
				item.setStatus((byte)2);
				//保存
				itemMapper.updateByPrimaryKey(item);
			}
			return E3Result.ok();
		}
		return null;
	}
	@Override
	public E3Result productReshelves(String ids) {
		//判断ids不为空
		if(StringUtils.isNoneBlank(ids)) {
			String[] split = ids.split(",");
			for (String id : split) {
				//通过id查询商品
				TbItem item = itemMapper.selectByPrimaryKey(Long.valueOf(id));
				//更新时间
				Date date = new Date();
				item.setUpdated(date);
				//商品状态,1-正常 2-下架 3-删除
				item.setStatus((byte)1);
				//保存
				itemMapper.updateByPrimaryKey(item);
			}
			return E3Result.ok();
		}
		return null;
	}

}

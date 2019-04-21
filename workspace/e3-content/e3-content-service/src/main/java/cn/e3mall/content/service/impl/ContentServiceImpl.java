package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
/**
 * 内容管理
 * @author Carl
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	TbContentMapper contentMapper;
	@Autowired
	JedisClient jedisClient;
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public E3Result addContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		//缓存同步,删除缓存中对应的数据。
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		return E3Result.ok();
	}

	/**
	 * 根据分类内容ID查询内容列表
	 */
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		//查询缓存
		try {
			//如果有缓存则直接响应结果
			String json = jedisClient.hget(CONTENT_LIST, cid+"");
			if(StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//没有缓存则查询数据库
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		
		//把结果添加进缓存
		try {
			jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public EasyUIDataGridResult getItemList(Long categoryId, Integer page, Integer rows) {
		 //设置分页信息
	    PageHelper.startPage(page,rows);
	    //创建查询条件
	    TbContentExample example=new TbContentExample();
	    TbContentExample.Criteria criteria = example.createCriteria();
	    //设置查询条件
	    criteria.andCategoryIdEqualTo(categoryId);
	    //执行查询
	    List<TbContent> contents = contentMapper.selectByExample(example);
	    //取分页信息
	    PageInfo<TbContent> pageInfo = new PageInfo(contents);
	    //创建返回结果对象
	    EasyUIDataGridResult result = new EasyUIDataGridResult();
	    result.setTotal((int) pageInfo.getTotal());
	    result.setRows(contents);
	    return result;
	
	}

	@Override
	public E3Result deleteBatchContent(String[] ids) {
		for (String id : ids) {
			contentMapper.deleteByPrimaryKey(Long.valueOf(id));
			
		}
		return E3Result.ok();
	}

}

package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemCatExample.Criteria;
import cn.e3mall.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Override
	public List<EasyUITreeNode> getItemCat(long parentId) {
		
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		criteria.andParentIdEqualTo(parentId);
		
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		
		List<EasyUITreeNode> resultNode = new ArrayList() ;
		//把列表转换成EasyUITreeNode列表
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			
			resultNode.add(node);
		}
		return resultNode;
	}

}

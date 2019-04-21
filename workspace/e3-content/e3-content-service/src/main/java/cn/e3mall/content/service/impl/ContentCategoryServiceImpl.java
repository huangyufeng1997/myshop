package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> resultList =new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node =new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			node.setText(tbContentCategory.getName());
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 1、接收两个参数：parentId、name
		// 2、向tb_content_category表中插入数据。
		// a)创建一个TbContentCategory对象
		TbContentCategory tbContentCategory =new TbContentCategory();
		// b)补全TbContentCategory对象的属性
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		tbContentCategory.setIsParent(false);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		
		// c)向tb_content_category表中插入数据
		contentCategoryMapper.insert(tbContentCategory);
		// 3、判断父节点的isparent是否为true，不是true需要改为true。
		TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parentNode.getIsParent()) {
			parentNode.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		// 4、需要主键返回。
		// 5、返回E3Result，其中包装TbContentCategory对象
		return E3Result.ok(tbContentCategory);
	}

	@Override
	public E3Result deleteContentCategory(long id) {
		 //删除节点时需要判断是否有子节点
		   //痛id查询内容节点并且获取到父节点
		   TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		   Long parentId = tbContentCategory.getParentId();
		   if (tbContentCategory.getIsParent()){
		      return E3Result.build(1,"失败");
		   }else {
		      contentCategoryMapper.deleteByPrimaryKey(id);
		      TbContentCategoryExample example=new TbContentCategoryExample();
		      TbContentCategoryExample.Criteria criteria = example.createCriteria();
		      criteria.andParentIdEqualTo(parentId);
		      List<TbContentCategory> childs = contentCategoryMapper.selectByExample(example);
			      if (childs.size()==0) {
			         //判断父节点的isParent属性是否为true如果不是就修改为true
			         TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
			         if (parent.getIsParent()) {
			            parent.setIsParent(false);
			            //更新到数据库
			            contentCategoryMapper.updateByPrimaryKey(parent);
			         }
			      }
			         //返回结果，返回E3Result，包含pojo
			      return E3Result.ok(tbContentCategory);
		   	}
	
	}
}

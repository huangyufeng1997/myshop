package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 内容管理
 * @author Carl
 *
 */
@Controller
public class ContentController {
	@Autowired
	ContentService contentService;
	
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result addContent(TbContent content) {
		E3Result result = contentService.addContent(content);
		return result;
	}
	
	//通过分类id显示内容的数据
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult list(Long categoryId,Integer page,Integer rows){
	    //调用内容服务查询指定内容
	    EasyUIDataGridResult result = contentService.getItemList(categoryId,page,rows);
	    return result;
	}
	
	//批量删除内容数据
	@RequestMapping("/content/delete")
	@ResponseBody
	public E3Result deleteContent(String [] ids){
	    E3Result result=contentService.deleteBatchContent(ids);
	    return result;
	}
}

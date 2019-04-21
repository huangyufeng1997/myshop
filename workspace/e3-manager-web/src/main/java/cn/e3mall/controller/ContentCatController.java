package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

@Controller
public class ContentCatController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(
			@RequestParam(name="id",defaultValue="0")Long parentId){
		List<EasyUITreeNode> list = contentCategoryService.getContentCategoryList(parentId);
		return list;
	}
	
	@RequestMapping(value="/content/category/create",method=RequestMethod.POST)
	@ResponseBody
	public E3Result createCategory(Long parentId, String name) {
		E3Result result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	/**
	 * 删除内容节点
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/content/category/delete", method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteContentCategory(Long id){
	    //调用服务删除节点
	    E3Result result=contentCategoryService.deleteContentCategory(id);
	    return result;
	}
}

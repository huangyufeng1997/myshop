package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Controller
 * @author Carl
 *
 */
@Controller
public class itemController {
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable long itemId) {
		TbItem item = itemService.getItemById(itemId);
		return item;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows) {
		EasyUIDataGridResult itemList = itemService.getItemList(page, rows);
		return itemList;
	}
	
	//商品添加
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result saveItem(TbItem item, String desc) {
		E3Result result = itemService.addItem(item, desc);
		return result;
	}
	
	//异步加载回显描述
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public TbItemDesc selectTbItemDesc(@PathVariable long id) {
		TbItemDesc itemDesc = itemService.getItemDescById(id);
		return itemDesc;
	}
	
	//异步加载商品信息
	@RequestMapping("/rest/item/param/item/query/{id}")
	@ResponseBody
	public TbItem queryById(@PathVariable long id){
	    TbItem item = itemService.getItemById(id);
	    return item;
	}

	
	//批量删除
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public E3Result delete(String ids) {
		E3Result result =itemService.deleteBatch(ids);
		return result;
	}
	
	//批量下架
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public E3Result productShelves(String ids) {
		E3Result result = itemService.productShelves(ids);
		return result;
	}
	
	//批量上架
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public E3Result productReshelves(String ids) {
		E3Result result = itemService.productReshelves(ids);
		return result;
	}
	
		
	
}

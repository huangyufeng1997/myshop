package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.service.SearchItemService;

/**
 * 导入商品数据到索引库
 * @author Carl
 *
 */
@Controller
public class SearchItemController {
	@Autowired
	SearchItemService searchItemService;
	
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3Result impotItemIndex() {
		E3Result result = searchItemService.importAllItems();
		return result;
	}
}

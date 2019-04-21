package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.service.ItemService;

/**
 * 后台页面跳转
 * @author Carl
 *
 */
@Controller
public class PageController {
	
	@RequestMapping("/")
	public String getIndex() {
		return "index";
	}
	
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page) {
		return page;
	}
	
	
}	
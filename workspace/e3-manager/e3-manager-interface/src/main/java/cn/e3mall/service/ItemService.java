package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	TbItem getItemById(long itemId);
	EasyUIDataGridResult getItemList(int page,int rows);
	E3Result addItem(TbItem item,String desc);
	TbItemDesc getItemDescById(long itemId);
	E3Result deleteBatch(String ids);			//批量删除
	E3Result productShelves(String ids);		//批量下架
	E3Result productReshelves(String ids);		//批量上架
}

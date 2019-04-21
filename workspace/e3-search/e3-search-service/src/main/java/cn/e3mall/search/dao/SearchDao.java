package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

/**
 * 商品搜索Dao
 * @author Carl
 *
 */
@Repository
public class SearchDao {
	@Autowired
	SolrServer solrServer;
	
	public SearchResult search(SolrQuery query) throws Exception{
		//根据索引查询索引库
		QueryResponse  queryResponse= solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		//取查询结果的总记录数
		long numFound = solrDocumentList.getNumFound();
		SearchResult result =new SearchResult();
		result.setRecourdCount(numFound);
		
		//高亮显示
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		List<SearchItem> list = new ArrayList<>();
		for (SolrDocument solrDocument : solrDocumentList) {
			SearchItem searchItem = new SearchItem();
			searchItem.setId((String) solrDocument.get("id"));
			searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
			searchItem.setImage((String) solrDocument.get("item_image"));
			searchItem.setPrice((long) solrDocument.get("item_price"));
			searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
			//取高亮显示
			List<String> list2 = highlighting.get(solrDocument.get("id")).get("item_title");
			String title="";
			if(list2!=null&&list2.size()>0) {
				title=list2.get(0);
			}else {
				title=(String) solrDocument.get("item_title");
			}
			searchItem.setTitle(title);
			list.add(searchItem);
		}
		//返回结果
		result.setItemList(list);
		return result;
	}
}

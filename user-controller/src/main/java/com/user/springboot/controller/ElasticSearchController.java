package com.user.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/search")
@Slf4j
@Api(value = "elastic-serach-controller", description = "elasticsearch控制层")
public class ElasticSearchController {

	@Autowired
	private TransportClient client;

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail/{id}")
	// @ValidateAttribute(attributes = {"id"})
	@ApiOperation(value = "根据id查询接口")
	@ApiImplicitParam(name = "id", value = "查询id", dataType = "String", paramType = "query")
	public ResponseResult<?> findBookDetail(@PathVariable("id") String id) {
		log.info("得到的id:{}, 类的类型是...{}", id, this.getClass().getName());
		GetResponse result = client.prepareGet("study", "book", id).get();
		return new ResponseResult<Map<String, Object>>(ResultStatus.SUCCESS, result.getSource());
	}

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/add")
	@ApiOperation(value = "添加书本")
	public ResponseResult<?> addBook(String title, String author, Integer world_count) {


		try {
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().field("title", title)
					.field("author", author).field("world_count", world_count).endObject();
			IndexResponse indexResponse = client.prepareIndex("study", "book").setSource(contentBuilder).get();
			return new ResponseResult<String>(ResultStatus.SUCCESS, indexResponse.getId());
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
		}
	}

	/**
	 * 根据id删除书本
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/del/{id}")
	@ApiOperation(value = "删除书本")
	public ResponseResult<?> deleteBook(@PathVariable("id") String id) {
		log.info("得到的id:{}, 类的类型是...{}", id, this.getClass().getName());
		DeleteResponse result = client.prepareDelete("study", "book", id).get();
		return new ResponseResult<>(ResultStatus.SUCCESS, result);
	}

	/**
	 * 根据id查询elasticsearch
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/edit")
	@ApiOperation(value = "更新书本")
	public ResponseResult<?> updateBook(String title, String author, Integer world_count, String id, Boolean upsert) {
		try {
			/*UpdateRequest updateResponse = new UpdateRequest("study", "book", id);
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject().field("title", title)
					.field("author", author).field("world_count", world_count).endObject();
			updateResponse.doc(contentBuilder);*/
			Map<String, Object> esFieldData = new HashMap<>();
			if (title != null) {
				esFieldData.put("title", title);
			}
			if (author != null) {
				esFieldData.put("author", author);
			} if (world_count != null) {
				esFieldData.put("world_count", world_count);
			}
			log.info("map的值:{}", JSONObject.toJSONString(esFieldData));
			UpdateRequestBuilder updateRequestBuilder = client
					.prepareUpdate("study","book", id)
					.setDoc(esFieldData);
			if (upsert) {
				log.info("这是一个upsert操作.....");
				updateRequestBuilder.setDocAsUpsert(true);
			}

			BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
			bulkRequestBuilder.add((updateRequestBuilder));
			BulkResponse response = bulkRequestBuilder.execute().actionGet(); //等待执行完毕
			if (response.hasFailures()) {
				for (BulkItemResponse itemResponse : response.getItems()) {
					if (!itemResponse.isFailed()) {
						continue;
					}

					if (itemResponse.getFailure().getStatus() == RestStatus.NOT_FOUND) {
						log.warn(itemResponse.getFailureMessage());
					} else {
						log.error("ES sync commit error: {}", itemResponse.getFailureMessage());
					}
				}
			}
			return new ResponseResult<>(ResultStatus.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseResult<>(ResultStatus.UNKNOW_ERROR);
		}
	}

	/**
	 * 复杂查询接口
	 * 
	 * @param title
	 * @param author
	 * @return
	 */
	@RequestMapping(value = "/complex/query")
	public ResponseResult<?> complexQuery(String title, String author, Integer gtWorld) {
		BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery();
		if (author != null) {
			booleanQuery.must(QueryBuilders.matchQuery("author", author));
		}
		if (title != null) {
			booleanQuery.must(QueryBuilders.matchQuery("title", title));
		}
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("world_count").from(gtWorld);
		booleanQuery.filter(rangeQuery);
		SearchRequestBuilder builder = client.prepareSearch("study").setTypes("book").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		.setQuery(booleanQuery).setFrom(0).setSize(10);
		log.info("请求体格式:{}", builder);
		SearchResponse searchResponse = builder.get();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : searchResponse.getHits()) {
			result.add(hit.getSourceAsMap());
		}
		return new ResponseResult<>(ResultStatus.SUCCESS, result);
	}

}

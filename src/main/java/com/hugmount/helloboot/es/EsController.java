package com.hugmount.helloboot.es;

import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.es.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: lhm
 * @date: 2022/8/2
 */
@Slf4j
@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    private RestHighLevelClient highLevelClient;

    /**
     * 创建索引
     *
     * @param index
     * @return
     * @throws IOException
     */
    @GetMapping("/createIndex/{index}")
    public Result<Object> createIndex(@PathVariable("index") String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        // 客户端执行请求 IndicesClient，请求获得响应
        CreateIndexResponse createIndexResponse = highLevelClient.indices().create(request, RequestOptions.DEFAULT);
        log.info("es返回: {}", JSON.toJSONString(createIndexResponse));
        return Result.createBySuccess("成功");

    }

    /**
     * 创建文档
     *
     * @param userInfo
     * @return
     * @throws IOException
     */
    @PostMapping("/creteDocument")
    public Result<Object> creteDocument(@RequestBody UserInfo userInfo) throws IOException {
        userInfo.setDate(new Date());
        IndexRequest request = new IndexRequest("lhm");
        //规则 put /caw_index/_doc/1
        //设置文档id，不指定ES会随机产生
        request.id("3");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.source(JSON.toJSONString(userInfo), XContentType.JSON);
        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("返回: {}", JSON.toJSONString(response));
        return Result.createBySuccess("成功");
    }

    @PostMapping("/get")
    public Result<Object> sava(@RequestBody UserInfo userInfo) throws IOException {
        GetRequest request = new GetRequest("lhm", "3");
        GetResponse response = highLevelClient.get(request, RequestOptions.DEFAULT);
        return Result.createBySuccess("成功", response);
    }

    @PostMapping("/updateDocument")
    public Result<Object> updateDocument(@RequestBody UserInfo userInfo) throws IOException {
        userInfo.setDate(new Date());
        UpdateRequest request = new UpdateRequest("lhm", "3");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.doc(JSON.toJSONString(userInfo), XContentType.JSON);
        UpdateResponse response = highLevelClient.update(request, RequestOptions.DEFAULT);
        log.info("返回: {}", JSON.toJSONString(response));
        return Result.createBySuccess("成功");
    }

    @PostMapping("/getPage")
    public Result<Object> getPage(@RequestBody UserInfo userInfo) throws IOException {
        SearchRequest request = new SearchRequest("lhm");
        //构建搜索条件
        //查询条件 QueryBuilders
        //精准搜索  termQuery
        //所有搜索   matchAllQuery
        //创建查询对象
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // taskId =
        // 注意 如果查询字段类型为 keyword 则需在字典后添加.keyword
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name.keyword", userInfo.getName());
        boolQuery.must(termQuery);
        if (!Objects.isNull(userInfo)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("date");
            rangeQuery.gte(userInfo.getDate());
            boolQuery.must(rangeQuery);
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(3);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.sort("id", SortOrder.ASC);
        searchSourceBuilder.query(boolQuery);
        request.source(searchSourceBuilder);
        SearchResponse response = highLevelClient.search(request, RequestOptions.DEFAULT);
        log.info("返回结果: {}", JSON.toJSONString(response));
        SearchHit[] hits = response.getHits().getHits();
        ArrayList<SearchHit> searchHits = new ArrayList<>(Arrays.asList(hits));
        return Result.createBySuccess("成功", searchHits);

    }


}

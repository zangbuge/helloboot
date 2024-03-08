package com.hugmount.helloboot.es;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.hugmount.helloboot.core.Result;
import com.hugmount.helloboot.es.model.UserInfo;
import lombok.SneakyThrows;
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
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        request.id(userInfo.getId());
        request.timeout(TimeValue.timeValueSeconds(1));
        request.source(JSON.toJSONString(userInfo), XContentType.JSON);
        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("返回: {}", JSON.toJSONString(response));
        return Result.createBySuccess("成功");
    }

    @PostMapping("/get")
    public Result<Object> sava(@RequestBody UserInfo userInfo) throws IOException {
        GetRequest request = new GetRequest("lhm", userInfo.getId());
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
        // 比较大小
        boolQuery.must(QueryBuilders.rangeQuery("date").lte(new Date().getTime()));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(3);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 排序只能用数字类型
        searchSourceBuilder.sort("date", SortOrder.ASC);
        searchSourceBuilder.query(boolQuery);
        request.source(searchSourceBuilder);
        log.info("es RestHighLevelClient调试打印请求体: {}", request.source().toString());
        SearchResponse response = highLevelClient.search(request, RequestOptions.DEFAULT);
        log.info("返回结果: {}", JSON.toJSONString(response));
        SearchHit[] hits = response.getHits().getHits();
        ArrayList<SearchHit> searchHits = new ArrayList<>(Arrays.asList(hits));
        List<UserInfo> collect = searchHits.stream().map(it -> JSON.parseObject(it.getSourceAsString(), UserInfo.class)).collect(Collectors.toList());
        // es 统计
        CountRequest countRequest = new CountRequest("lhm");
        countRequest.query(boolQuery);
        CountResponse countResponse = highLevelClient.count(countRequest, RequestOptions.DEFAULT);
        log.info("es count : {}", countResponse.getCount());

        BoolQueryBuilder boolQueryBuilder = buildQuery();
        CountRequest cntQuery = new CountRequest("lhm");
        cntQuery.query(boolQueryBuilder);
        CountResponse cntRes = highLevelClient.count(cntQuery, RequestOptions.DEFAULT);
        int count = (int) cntRes.getCount();
        System.out.println("总条数: " + count);
        List<SearchHit> searchHits1 = searchEsPage(count, 2);
        List<Map> collect1 = searchHits1.stream().map(it -> JSONUtil.toBean(it.getSourceAsString(), Map.class)).collect(Collectors.toList());
        System.out.println("es分页查询: " + JSONUtil.toJsonStr(collect1));

        return Result.createBySuccess("成功", collect);

    }

    List<SearchHit> searchEsPage(int cnt, int pageSize) throws IOException {
        long pages = cnt / pageSize;
        long rem = cnt % pageSize;
        pages = rem == 0 ? pages : pages + 1;
        List<SearchHit> esDatalist = new ArrayList<>();
        // search_after 分页, objects标记
        Object[] objects = new Object[]{};
        for (int i = 0; i < pages; i++) {
            BoolQueryBuilder boolQueryBuilder = buildQuery();
            SearchSourceBuilder pageBuilder = new SearchSourceBuilder();
            pageBuilder.size(pageSize);
            // 必须指定排序
            pageBuilder.sort("_id", SortOrder.ASC);
            pageBuilder.query(boolQueryBuilder);
            if (objects.length > 0) {
                pageBuilder.searchAfter(objects);
            }
            SearchRequest searchRequest = new SearchRequest("lhm");
            searchRequest.source(pageBuilder);
            SearchResponse pageResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hitsArr = pageResponse.getHits().getHits();
            log.info("当前页i: {}, size: {}", i, hitsArr.length);
            Collections.addAll(esDatalist, hitsArr);
            // 取最后一个
            objects = hitsArr[hitsArr.length - 1].getSortValues();
        }
        return esDatalist;
    }


    BoolQueryBuilder buildQuery() {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("name.keyword", "李会明"));
        return boolQuery;
    }

    @SneakyThrows
    @GetMapping("/aggregation")
    public Result<Object> aggregation() {
        SearchRequest request = new SearchRequest("lhm_test");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("addr.keyword", "xy"));
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_userId").field("userId.keyword")
                .subAggregation(AggregationBuilders.count("count_id").field("id.keyword"));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        request.source(searchSourceBuilder);
        SearchResponse search1 = highLevelClient.search(request, RequestOptions.DEFAULT);
        Aggregations aggregations1 = search1.getAggregations();
        log.info(JSONUtil.toJsonStr(aggregations1));
        // String json {"code":"0","msg":"","data":{"asMap":{"group_userId":{"name":"group_userId","buckets":[{"aggregations":{"asMap":{"count_id":{"name":"count_id","value":2,"type":"value_count","valueAsString":"2.0","fragment":true}},"fragment":true},"keyAsString":"12341","docCount":2,"docCountError":0,"key":"12341","keyAsNumber":12341,"fragment":true},{"aggregations":{"asMap":{"count_id":{"name":"count_id","value":1,"type":"value_count","valueAsString":"1.0","fragment":true}},"fragment":true},"keyAsString":"123412","docCount":1,"docCountError":0,"key":"123412","keyAsNumber":123412,"fragment":true}],"type":"sterms","sumOfOtherDocCounts":0,"docCountError":0,"fragment":true}},"fragment":true}}
        return Result.createBySuccess("", aggregations1);
    }

}

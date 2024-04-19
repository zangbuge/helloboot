package com.hugmount.helloboot.es.service;

import cn.hutool.json.JSONUtil;
import com.hugmount.helloboot.es.dto.EsDataDTO;
import com.hugmount.helloboot.es.dto.EsStatDTO;
import com.hugmount.helloboot.es.vo.EsStatVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * es统计,聚合
 *
 * @author lhm
 * @date 2024/4/16
 */

@Slf4j
@Service
public class EsStatServiceImpl {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    private static final String ES_TEST_INDEX = "es_test_index";

    @SneakyThrows
    public List<EsStatVO> queryTravelPage(EsStatDTO labelStatDTO) {
        log.info("es数据查询入参: {}", labelStatDTO);
        Assert.notEmpty(labelStatDTO.getTypeCodeList(), "条件类型不能为空");
        BoolQueryBuilder boolQueryBuilder = travelQueryBuilder(labelStatDTO);
        SearchSourceBuilder searchSourceBuilder = searchPageBuilder(labelStatDTO, "startTime");
        searchSourceBuilder.query(boolQueryBuilder);
        if (Objects.nonNull(labelStatDTO.getTop())) {
            TermsAggregationBuilder aggregationBuilder = travelQueryAggregationBuilder(labelStatDTO, "gpField");
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        SearchRequest searchRequest = new SearchRequest(ES_TEST_INDEX);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return dealResponse(response, labelStatDTO, false);
    }


    @SneakyThrows
    public List<EsStatVO> querySsmCheckPage(EsStatDTO labelStatDTO) {
        log.info("分页查询入参: {}", labelStatDTO);
        BoolQueryBuilder boolQueryBuilder = ssmCheckQueryBuilder(labelStatDTO);
        SearchSourceBuilder searchSourceBuilder = searchPageBuilder(labelStatDTO, "startTime");
        searchSourceBuilder.query(boolQueryBuilder);
        if (Objects.nonNull(labelStatDTO.getTop())) {
            TermsAggregationBuilder aggregationBuilder = travelQueryAggregationBuilder(labelStatDTO, "userId.keyword");
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        CollapseBuilder collapseBuilder = new CollapseBuilder("userId.keyword");
        searchSourceBuilder.collapse(collapseBuilder);
        SearchRequest searchRequest = new SearchRequest(ES_TEST_INDEX);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return dealResponse(response, labelStatDTO, true);
    }

    /**
     * 处理es结果
     *
     * @param response
     * @param labelStatDTO
     * @param isCheck      行程数据
     * @return
     */
    List<EsStatVO> dealResponse(SearchResponse response, EsStatDTO labelStatDTO, boolean isCheck) {
        List<EsStatVO> list = new LinkedList<>();
        // 聚合
        if (Objects.nonNull(labelStatDTO.getTop())) {
            Aggregations aggregations = response.getAggregations();
            Aggregation group_userId = aggregations.getAsMap().get("group_id");
            ParsedStringTerms parsedStringTerms = (ParsedStringTerms) group_userId;
            List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
            if (ObjectUtils.isEmpty(buckets)) {
                return null;
            }
            for (Terms.Bucket it : buckets) {
                EsStatVO vo = new EsStatVO();
                vo.setGroupId(it.getKeyAsString());
                vo.setCnt(Integer.parseInt(String.valueOf(it.getDocCount())));
                list.add(vo);
            }
            return list;
        }

        // 记录
        if (response.getHits().getTotalHits().value == 0) {
            return null;
        }

        SearchHit[] hits = response.getHits().getHits();
        Object[] objects = hits[hits.length - 1].getSortValues();
        int len = hits.length;
        for (int i = 0; i < len; i++) {
            SearchHit hit = hits[i];
            EsStatVO vo = new EsStatVO();
            vo.setObjects(objects);
            if (isCheck) {
                // 索引1
                EsDataDTO scanRecordDto = JSONUtil.toBean(hit.getSourceAsString(), EsDataDTO.class);
                vo.setGroupId(scanRecordDto.getUserId());
            }
            // 索引2
            else {
                EsDataDTO travelOrderDto = JSONUtil.toBean(hit.getSourceAsString(), EsDataDTO.class);
                vo.setGroupId(travelOrderDto.getUserId());
            }
            list.add(vo);
        }
        return list;
    }

    /**
     * 组装分页条件
     *
     * @param labelStatDTO
     * @return
     */
    SearchSourceBuilder searchPageBuilder(EsStatDTO labelStatDTO, String orderFieldName) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(labelStatDTO.getPageSize());
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort(orderFieldName, SortOrder.ASC);
        if (labelStatDTO.getObjects() != null && labelStatDTO.getObjects().length > 0) {
            searchSourceBuilder.searchAfter(labelStatDTO.getObjects());
        }
        return searchSourceBuilder;
    }

    BoolQueryBuilder ssmCheckQueryBuilder(EsStatDTO labelStatDTO) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("field1.keyword", "test1"));
        boolQueryBuilder.must(QueryBuilders.termQuery("field2.keyword", "test2"));
        if (!ObjectUtils.isEmpty(labelStatDTO.getTypeCodeList())) {
            // 条件
            boolQueryBuilder.must(QueryBuilders.termsQuery("type_code", labelStatDTO.getTypeCodeList()));
        }
        // 时间条件
        rangeQueryBuilder(boolQueryBuilder, labelStatDTO, "startTime");
        return boolQueryBuilder;
    }

    void rangeQueryBuilder(BoolQueryBuilder queryBuilder, EsStatDTO labelStatDTO, String fieldName) {
        RangeQueryBuilder currentTimes = QueryBuilders.rangeQuery(fieldName);
        if (Objects.nonNull(labelStatDTO.getStartTime())) {
            currentTimes.gte(labelStatDTO.getStartTime());
        }
        if (Objects.nonNull(labelStatDTO.getEndTime())) {
            currentTimes.lt(labelStatDTO.getEndTime());
        }
        if (Objects.nonNull(labelStatDTO.getStartTime()) || Objects.nonNull(labelStatDTO.getEndTime())) {
            queryBuilder.must(currentTimes);
        }
    }

    /**
     * 行程数据条件
     *
     * @param labelStatDTO
     * @return
     */
    BoolQueryBuilder travelQueryBuilder(EsStatDTO labelStatDTO) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("typeCode", labelStatDTO.getTypeCodeList()));
        rangeQueryBuilder(boolQueryBuilder, labelStatDTO, "startTime");
        // 仅查优惠的订单
        return boolQueryBuilder;
    }

    /**
     * 聚合条件
     *
     * @param labelStatDTO
     * @return
     */
    TermsAggregationBuilder travelQueryAggregationBuilder(EsStatDTO labelStatDTO, String gpFieldName) {
        TermsAggregationBuilder group_userId = AggregationBuilders.terms("group_id");
        TermsAggregationBuilder termsAggregationBuilder = group_userId.field(gpFieldName);
        termsAggregationBuilder.size(labelStatDTO.getTop());
        // 文档个数降序
        termsAggregationBuilder.order(BucketOrder.count(false));
        if (Objects.nonNull(labelStatDTO.getMinDocCount())) {
            termsAggregationBuilder.minDocCount(labelStatDTO.getMinDocCount());
        }
        return termsAggregationBuilder;
    }

}

package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.GoodsVO;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-10 17:34
 * @version: 1.0
 * @modified By:十一。
 */
@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public SearchResponseVO search(SearchParam searchParam) throws IOException {

        SearchRequest searchRequest = this.getDSL(searchParam);
        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("------------------------------------------------------------------");
        System.out.println(searchResponse.toString());
        SearchResponseVO responseVO = this.parseSearchResult(searchResponse);
        responseVO.setPageNum(searchParam.getPageNum());
        responseVO.setPageSize(searchParam.getPageSize());
        return responseVO;
    }

    private SearchResponseVO parseSearchResult(SearchResponse searchResponse) throws JsonProcessingException {
        SearchResponseVO responseVO = new SearchResponseVO();
        /*总记录数*/
        SearchHits hits = searchResponse.getHits();
        responseVO.setTotal(hits.getTotalHits());
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> map = aggregations.asMap();
        //品牌
        SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
        attrVO.setName("品牌");
        ParsedLongTerms brandIdAgg = (ParsedLongTerms) map.get("brandIdAgg");
        List<String> values = brandIdAgg.getBuckets().stream().map(bucket -> {
            Map<String, String> brandIdMap = new HashMap<>();
            //品牌Id
            brandIdMap.put("id", bucket.getKeyAsString());
            //品牌名称
            Map<String, Aggregation> brandIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms brandNameAgg = (ParsedStringTerms) brandIdSubMap.get("brandNameAgg");
            String attrName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandIdMap.put("name", attrName);
            //ok
            try {
                return OBJECT_MAPPER.writeValueAsString(brandIdMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        attrVO.setValue(values);
        responseVO.setBrand(attrVO);
        //分类
        SearchResponseAttrVO categoryAttrVO = new SearchResponseAttrVO();
        categoryAttrVO.setName("分类");
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms) map.get("categoryIdAgg");
        List<String> categoryvalues = categoryIdAgg.getBuckets().stream().map(bucket -> {
            Map<String, String> categoryIdMap = new HashMap<>();
            //分类Id
            categoryIdMap.put("id", bucket.getKeyAsString());
            //分类名称
            Map<String, Aggregation> brandIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms categoryNameAgg = (ParsedStringTerms) brandIdSubMap.get("categoryNameAgg");
            String attrName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
            categoryIdMap.put("name", attrName);
            //ok
            try {
                return OBJECT_MAPPER.writeValueAsString(categoryIdMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        categoryAttrVO.setValue(categoryvalues);
        responseVO.setCatelog(categoryAttrVO);
        //商品
        List<GoodsVO> goodVOs = new ArrayList<>();
        SearchHit[] subHits = hits.getHits();
        for (SearchHit subHit : subHits) {
            GoodsVO goodVO = OBJECT_MAPPER.readValue(subHit.getSourceAsString(), new TypeReference<GoodsVO>(){});
            //高亮结果集
            goodVO.setTitle(subHit.getHighlightFields().get("title").getFragments()[0].toString());
            goodVOs.add(goodVO);
        }
        responseVO.setProducts(goodVOs);
        //属性
        ParsedNested attrAgg = (ParsedNested) map.get("attrAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().asMap().get("attrIdAgg");
        List<SearchResponseAttrVO> searchResponseAttrVOS = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVO responseAttrVO = new SearchResponseAttrVO();
            //id
            responseAttrVO.setProductAttributeId((Long) bucket.getKeyAsNumber());
            //name
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) bucket.getAggregations().asMap().get("attrNameAgg");
            List<? extends Terms.Bucket> attrNameAggBuckets = attrNameAgg.getBuckets();
            responseAttrVO.setName(attrNameAggBuckets.get(0).getKeyAsString());
            //属性
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) bucket.getAggregations().asMap().get("attrValueAgg");
            List<String> atrValues = attrValueAgg.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
            responseAttrVO.setValue(atrValues);
            return responseAttrVO;
        }).collect(Collectors.toList());

        responseVO.setAttrs(searchResponseAttrVOS);

        return responseVO;
    }

    /**
     * 构建查询条件
     * @param searchParam
     * @return
     */
    private SearchRequest getDSL(SearchParam searchParam){
        //查询条件构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String keyword = searchParam.getKeyword();
        if (StringUtils.isEmpty(keyword)){
            return null;
        }
        //封装查询条件和过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword).operator(Operator.AND));
        //构建过滤条件
        String[] brands = searchParam.getBrand();
        if (brands != null && brands.length != 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brands));
        }

        String[] catelog3 = searchParam.getCatelog3();
        if (catelog3 != null && catelog3.length != 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",catelog3));
        }
        //构建属性规格嵌套过滤
        String[] props = searchParam.getProps();
        if (props != null && props.length != 0){
            for (String prop : props) {
                //嵌套查询
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                //嵌套查询中的子查询
                BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();

                String[] split = StringUtils.split(prop, ":");
                if (split == null && split.length != 2){
                    continue;
                }
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));

                String[] attrValues = StringUtils.split(split[1], "-");

                subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                boolQuery.must(QueryBuilders.nestedQuery("attrs",subBoolQuery, ScoreMode.None));

                boolQueryBuilder.filter(boolQuery);
            }

        }
        //价格区间
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        Integer priceFrom = searchParam.getPriceFrom();
        Integer priceTo = searchParam.getPriceTo();
        if (priceFrom != null){
            rangeQueryBuilder.gte(priceFrom);
        }
        if (priceTo != null) {
            rangeQueryBuilder.lte(priceTo);
        }
        sourceBuilder.query(rangeQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);
        //构建分页
        sourceBuilder.from((searchParam.getPageNum() - 1) * searchParam.getPageSize());
        sourceBuilder.size(searchParam.getPageSize());
        //排序
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] split = StringUtils.split(order, ":");
            if (split != null && split.length == 2){
                String field = null;
                switch (split[0]){
                    case "1":
                        field = "sale";
                        break;
                    case "2":
                        field = "price";
                        break;
                }
                sourceBuilder.sort(field,StringUtils.equals("asc",split[1]) ? SortOrder.ASC :SortOrder.DESC);
            }
        }
        //高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
        //聚合
        //品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));
        //分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));
        //规格属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                    .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                    .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

        System.out.println("sourceBuilder = " + sourceBuilder.toString());

        //结果集过滤
        sourceBuilder.fetchSource(new String[]{"skuId","pic","price","title"} , null);

        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(sourceBuilder);
        return searchRequest;

    }
}

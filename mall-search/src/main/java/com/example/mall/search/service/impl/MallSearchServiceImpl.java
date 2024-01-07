package com.example.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.EsConstant;
import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import com.example.mall.search.config.MallElasticSearchConfig;
import com.example.mall.search.feign.ProductFeignService;
import com.example.mall.search.service.MallSearchService;
import com.example.mall.search.vo.AttrResponseVo;
import com.example.mall.search.vo.BrandVo;
import com.example.mall.search.vo.SearchParam;
import com.example.mall.search.vo.SearchResult;
import com.sun.deploy.net.URLEncoder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {


    @Resource
    RestHighLevelClient restHighLevelClient;

    @Resource
    ProductFeignService productFeignService;

    //去es检索
    @Override
    public SearchResult search(SearchParam searchParam) {
        //动态构建出查询需要的DSL语句
        SearchResult searchResult = new SearchResult();
        //准备检索请求
//        SearchRequest searchRequest = new SearchRequest();
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            //执行检索请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);

            //分析响应数据封装成我们需要的数据格式
            searchResult = buildSearchResult(searchResponse, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResult;
    }

    /**
     * 准备检索请求
     * 模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存),排序，分类，高亮，聚合分析
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        //构建DSL语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /**
         *查询 模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)
         */
        //构建bool - query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        must-模糊匹配
        if (!StringUtils.isEmpty(searchParam.getKeyWord())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyWord()));
        }
        //构建bool - filter - 按照三级分类查询
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        //构建bool - filter - 按照品牌ID查询
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
//构建bool - filter - 按照所有指定的属性进行查询
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {


            //attrs=1_5寸:8寸&attrs=2_16G:8G
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
//attrs=1_5寸:8寸
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);

                boolQuery.filter(nestedQuery);
            }


        }


        //构建bool - filter - 按照是否有库存查询
        boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));

        //构建bool - filter - 按照价格区间查询
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            //三种情况 _500/500/500_
            if (s.length == 2) {
//                区间
                skuPrice.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    skuPrice.lte(s[0]);
                }
                if (searchParam.getSkuPrice().endsWith("_")) {
                    skuPrice.lte(s[1]);
                }
            }

            boolQuery.filter(skuPrice);
        }

//所有的条件进行封装
        searchSourceBuilder.query(boolQuery);


        /**
         * 排序，分类，高亮
         */
//        排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            String[] s = searchParam.getSort().split("_");
            SortOrder order = s[1].equalsIgnoreCase("ase") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        //分页 pageSize:5
        //pageNum:1 from:0 size:5  [0,1,2,3,4]
        //pageNum:2 from:5 size:5  [0,1,2,3,4]
        //from=(pageNum-1)*size
        searchSourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
//        高亮
        if (!StringUtils.isEmpty(searchParam.getKeyWord())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
//        品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
//        品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        searchSourceBuilder.aggregation(brand_agg);
        //分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
//        品牌聚合的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_age").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);
//属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
//        子聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
//        孙聚合 对应的名字
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
//      对应的属性值
        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);
        attr_id_agg.subAggregation(attr_name_agg);
        attr_id_agg.subAggregation(attr_value_agg);

        attr_agg.subAggregation(attr_id_agg);

        searchSourceBuilder.aggregation(attr_agg);

        String s = searchSourceBuilder.toString();
        System.out.println("构建的DSL" + s);


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);

        return searchRequest;
    }

    //构建结果数据
    private SearchResult buildSearchResult(SearchResponse response, SearchParam paramVo) {
        SearchResult result = new SearchResult();
        //1.返回所有查询到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModelList = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(paramVo.getKeyWord())) {
                    //设置高亮内容
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }
                esModelList.add(skuEsModel);
            }
        }
        result.setProducts(esModelList);

        //2.当前所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");//根据返回值确定数据类ParsedLongTerms，ParsedNested
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1.得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            //2.得到属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //3.得到属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);
        //3.当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1.品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            //2.品牌名字
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            //3.品牌图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);
        //4.当前所有商品涉及到的所有分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        //5.当前所有商品涉及到的所有分页信息
        //页码
        result.setPageNum(paramVo.getPageNum());
//        result.setPageNum();
        //总计录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //总页码
        Long totalPages = total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? total / EsConstant.PRODUCT_PAGE_SIZE : (total / EsConstant.PRODUCT_PAGE_SIZE) + 1;
        result.setTotalPage(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        //6.构建面包屑导航
        if (paramVo.getAttrs() != null && paramVo.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = paramVo.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //分析每个attr的参数值
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo attrs = r.getData2("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(attrs.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                //取消面包屑后要跳转的地方，将请求url置空
                String replace = replaceQueryString(paramVo, attr, "attrs");
                navVo.setLink("http://search.mall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }
        //品牌、分类面包屑导航
        if (paramVo.getBrandId() != null && paramVo.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //远程查询所有品牌
            R r = productFeignService.infos(paramVo.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brands = r.getData2("brands", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brands) {
                    buffer.append(brandVo.getBrandName() + ";");
                    replace = replaceQueryString(paramVo, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.mall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }
        return result;
    }

    //取消了面包屑之后,跳转的位置(将请求地址的url替换,置空)
    private String replaceQueryString(SearchParam paramVo, String value, String key) {
        String encode = null;
        try {
            //编码
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");//对空格特殊处理(将空格变为%20)
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return paramVo.get_queryString().replace("&" + key + "=" + encode, "");
    }

// dsl.json chatGPT generate code
//    public class ElasticsearchExample {
//        public static void main(String[] args) {
//            try (RestHighLevelClient client = new RestHighLevelClient(
//                    RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
//
//                // Build Query
//                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//
//                // Must
//                boolQuery.must(QueryBuilders.matchQuery("skuTitle", "华为"));
//
//                // Filter
//                boolQuery.filter(QueryBuilders.termQuery("catalogId", "225"));
//                boolQuery.filter(QueryBuilders.termsQuery("brandId", "1", "2", "9"));
//
//                // Nested Query
//                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
//                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", "9"));
//                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", "高通", "海思"));
//                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
//                boolQuery.filter(nestedQuery);
//
//                boolQuery.filter(QueryBuilders.termQuery("hasStock", true));
//                boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").gte(0).lte(6500));
//
//                // Build SearchSourceBuilder
//                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//                searchSourceBuilder.query(boolQuery);
//
//                // Sort
//                searchSourceBuilder.sort("skuPrice", SortOrder.DESC);
//
//                // From and Size
//                searchSourceBuilder.from(0);
//                searchSourceBuilder.size(5);
//
//                // Highlight
//                HighlightBuilder highlightBuilder = new HighlightBuilder();
//                highlightBuilder.field("skuTitle");
//                highlightBuilder.preTags("<b style='color:red'>");
//                highlightBuilder.postTags("</b>");
//                searchSourceBuilder.highlighter(highlightBuilder);
//
//                // Aggregations
//                // Brand Aggregation
//                searchSourceBuilder.aggregation(AggregationBuilders.terms("brand_agg").field("brandId")
//                        .size(10)
//                        .subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(10))
//                        .subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10)));
//
//                // Catalog Aggregation
//                searchSourceBuilder.aggregation(AggregationBuilders.terms("catalog_agg").field("catalogId")
//                        .size(10)
//                        .subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10)));
//
//                // Attribute Aggregation
//                searchSourceBuilder.aggregation(AggregationBuilders.nested("attr_agg", "attrs")
//                        .subAggregation(AggregationBuilders.terms("attr_id_agg").field("attrs.attrId")
//                                .size(100)
//                                .subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(10))
//                                .subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10))));
//
//                // Build SearchRequest
//                SearchRequest searchRequest = new SearchRequest("gulimall_product");
//                searchRequest.source(searchSourceBuilder);
//
//                // Execute SearchRequest
//                SearchResponse searchResponse = client.search(searchRequest);
//
//                // Process SearchResponse as needed
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}

package com.se2.demo.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.se2.demo.dto.request.ProductCriteriaRequest;
import com.se2.demo.dto.request.ProductFilterRequest;
import com.se2.demo.dto.response.PageResponse;
import com.se2.demo.dto.response.ProductDocumentResponse;
import com.se2.demo.dto.response.ProductNameResponse;
import com.se2.demo.dto.response.ProductResponse;
import com.se2.demo.mapper.ProductMapper;
import com.se2.demo.model.entity.ProductDocument;
import com.se2.demo.service.ProductDocumentSearchService;
import com.se2.demo.utils.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductDocumentSearchServiceImpl implements ProductDocumentSearchService {
        private final ElasticsearchOperations elasticsearchOperations;
        private final ProductMapper productMapper;
        private static final String FUZZINESS_ONE = "1";

        @Override
        public PageResponse<ProductDocumentResponse> searchProduct(ProductCriteriaRequest productCriteriaRequest) {
                NativeQueryBuilder nativeQuery = NativeQuery.builder()
                                .withAggregation("sports", Aggregation.of(a -> a
                                                .terms(ta -> ta.field(Constant.ProductField.SPORT_NAME))))
                                .withAggregation("gender", Aggregation.of(a -> a
                                                .terms(ta -> ta.field(Constant.ProductField.GENDER_NAME))))
                                .withAggregation("brands", Aggregation.of(a -> a
                                                .terms(ta -> ta.field(Constant.ProductField.BRAND_NAME))))
                                .withAggregation("categories", Aggregation.of(a -> a
                                                .terms(ta -> ta.field(Constant.ProductField.CATEGORY_NAME))))
                                .withPageable(PageRequest.of(
                                                productCriteriaRequest.page() != null ? productCriteriaRequest.page()
                                                                : 0,
                                                productCriteriaRequest.size() != null ? productCriteriaRequest.size()
                                                                : 10));

                if (StringUtils.isNotBlank(productCriteriaRequest.keyword())) {
                        nativeQuery.withQuery(q -> q
                                        .bool(b -> b
                                                        .should(s -> s
                                                                        .multiMatch(m -> m
                                                                                        .fields(Constant.ProductField.NAME,
                                                                                                        Constant.ProductField.SPORT_NAME,
                                                                                                        Constant.ProductField.GENDER_NAME,
                                                                                                        Constant.ProductField.BRAND_NAME,
                                                                                                        Constant.ProductField.CATEGORY_NAME)
                                                                                        .query(productCriteriaRequest
                                                                                                        .keyword())
                                                                                        .fuzziness(FUZZINESS_ONE)))));
                }

                nativeQuery.withFilter(f -> f
                                .bool(b -> {
                                        extractedTermsFilter(productCriteriaRequest.brands(),
                                                        Constant.ProductField.BRAND_NAME, b);
                                        extractedTermsFilter(productCriteriaRequest.categories(),
                                                        Constant.ProductField.CATEGORY_NAME, b);
                                        extractedTermsFilter(productCriteriaRequest.sports(),
                                                        Constant.ProductField.SPORT_NAME, b);
                                        extractedTermsFilter(productCriteriaRequest.gender(),
                                                        Constant.ProductField.GENDER_NAME, b);
                                        extractedRange(productCriteriaRequest.minPrice(),
                                                        productCriteriaRequest.maxPrice(), b);
                                        return b;
                                }));

                if (Objects.equals(productCriteriaRequest.sortDir(), Constant.SortType.PRICE_ASC)) {
                        nativeQuery.withSort(Sort.by(Sort.Direction.ASC, Constant.ProductField.SHOW_PRICE));
                } else if (Objects.equals(productCriteriaRequest.sortDir(), Constant.SortType.PRICE_DESC)) {
                        nativeQuery.withSort(Sort.by(Sort.Direction.DESC, Constant.ProductField.SHOW_PRICE));
                }

                SearchHits<ProductDocument> searchHitsResult = elasticsearchOperations.search(nativeQuery.build(),
                                ProductDocument.class);
                SearchPage<ProductDocument> productPage = SearchHitSupport.searchPageFor(searchHitsResult,
                                nativeQuery.getPageable());

                List<ProductDocumentResponse> productListVmList = searchHitsResult.stream()
                                .map(i -> this.productMapper.toDocumentResponse(i.getContent())).toList();

                return PageResponse.<ProductDocumentResponse>builder()
                                .content(productListVmList)
                                .pageNo(productPage.getNumber())
                                .pageSize(productPage.getSize())
                                .totalElements(productPage.getTotalElements())
                                .totalPages(productPage.getTotalPages())
                                .last(productPage.isLast())
                                .build();
        }

        private void extractedRange(Number min, Number max, BoolQuery.Builder bool) {
                if (min != null || max != null) {
                        bool.must(m -> m.range(r -> r
                                        .number(n -> {
                                                n.field(Constant.ProductField.SHOW_PRICE);
                                                if (min != null) {
                                                        n.gte(min.doubleValue());
                                                }
                                                if (max != null) {
                                                        n.lte(max.doubleValue());
                                                }
                                                return n;
                                        })));
                }
        }

        private void extractedTermsFilter(String fieldValues, String productField, BoolQuery.Builder b) {
                if (StringUtils.isBlank(fieldValues)) {
                        return;
                }
                String[] valuesArray = fieldValues.split(",");
                b.must(m -> {
                        BoolQuery.Builder innerBool = new BoolQuery.Builder();
                        for (String value : valuesArray) {
                                innerBool.should(s -> s
                                                .term(t -> t
                                                                .field(productField)
                                                                .value(value)
                                                                .caseInsensitive(true)));
                        }
                        return new Query.Builder().bool(innerBool.build());
                });
        }

        @Override
        public List<ProductNameResponse> autoCompleteProductName(String keyword) {
                NativeQuery matchQuery = NativeQuery.builder()
                                .withQuery(
                                                q -> q.matchPhrasePrefix(
                                                                matchPhrasePrefix -> matchPhrasePrefix.field("name")
                                                                                .query(keyword)))
                                .withSourceFilter(new FetchSourceFilter(
                                                true,
                                                new String[] { "name" },
                                                null))
                                .build();
                SearchHits<ProductDocument> result = elasticsearchOperations.search(matchQuery, ProductDocument.class);
                List<ProductDocument> products = result.stream().map(SearchHit::getContent).toList();

                return products.stream()
                                .map(productMapper::toProductNameResponse)
                                .toList();
        }
}

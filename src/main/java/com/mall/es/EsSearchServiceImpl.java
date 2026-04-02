package com.mall.es;

import com.mall.entity.ProductDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSearchServiceImpl implements EsSearchService{

    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public Page<ProductDoc> search(String keyword, Integer page, Integer size) {

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("name", "description")
                        )
                )
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProductDoc> hits = operations.search(query, ProductDoc.class);

        List<ProductDoc> list = hits.getSearchHits().stream().map(hit -> {
            ProductDoc doc = hit.getContent();

            if (hit.getHighlightFields().containsKey("name")) {
                doc.setName(hit.getHighlightFields().get("name").get(0));
            }

            return doc;
        }).collect(Collectors.toList());

        return new PageImpl<>(list);
    }
}

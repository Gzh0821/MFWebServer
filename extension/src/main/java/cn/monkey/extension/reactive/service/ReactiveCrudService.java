package cn.monkey.extension.reactive.service;


import cn.monkey.commons.data.QueryRequest;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Collection;

public interface ReactiveCrudService<ID extends Serializable, Q extends QueryRequest, D, R> {

    default Mono<Result<R>> read(ExtensionQueryRequest authQueryRequest, ID id) {
        throw new UnsupportedOperationException();
    }

    default Mono<Result<R>> readOne(ExtensionQueryRequest authQueryRequest, Q queryRequest) {
        throw new UnsupportedOperationException();
    }


    default Mono<Result<Collection<R>>> read(ExtensionQueryRequest extensionQueryRequest, Q queryRequest, Sort sort) {
        throw new UnsupportedOperationException();
    }

    default Mono<Result<Collection<R>>> read(ExtensionQueryRequest extensionQueryRequest, Q queryRequest) {
        return read(extensionQueryRequest, queryRequest, Sort.unsorted());
    }

    default Mono<Result<Page<R>>> read(ExtensionQueryRequest authQueryRequest, Q queryRequest, Pageable pageable) {
        throw new UnsupportedOperationException();
    }


    default Mono<Result<R>> create(ExtensionQueryRequest authQueryRequest, D dto) {
        throw new UnsupportedOperationException();
    }

    default Mono<Result<R>> update(ExtensionQueryRequest authQueryRequest, ID id, D dto) {
        throw new UnsupportedOperationException();
    }

    default Mono<Result<Void>> delete(ExtensionQueryRequest authQueryRequest, ID... id) {
        throw new UnsupportedOperationException();
    }

}
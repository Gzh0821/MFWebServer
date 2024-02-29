package cn.monkey.extension.service;


import cn.monkey.commons.data.QueryRequest;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public interface CrudService<ID extends Serializable, Q extends QueryRequest, D, R> {

    default Result<R> read(ExtensionQueryRequest authQueryRequest, ID id) {
        throw new UnsupportedOperationException();
    }

    default Result<R> read(ExtensionQueryRequest authQueryRequest, Q queryRequest) {
        throw new UnsupportedOperationException();
    }

    default Result<Page<R>> read(ExtensionQueryRequest authQueryRequest, Q queryRequest, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    default Result<R> create(ExtensionQueryRequest authQueryRequest, D dto) {
        throw new UnsupportedOperationException();
    }

    default Result<R> update(ExtensionQueryRequest authQueryRequest, ID id, D dto) {
        throw new UnsupportedOperationException();
    }

    default Result<Void> delete(ExtensionQueryRequest authQueryRequest, ID... id) {
        throw new UnsupportedOperationException();
    }

}
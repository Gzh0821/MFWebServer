package cn.monkey.extension.reactive.service;

import cn.monkey.commons.data.EntityMapper;
import cn.monkey.commons.data.QueryRequest;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.commons.util.ClassUtil;
import cn.monkey.orm.QueryRequestRepository;
import cn.monkey.orm.reactive.ReactiveQueryRequestRepository;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public abstract class AbstractReactiveCrudService<Q extends QueryRequest, T, ID extends Serializable,
        D, R, Repository extends ReactiveQueryRequestRepository<Q, T, ID>, Mapper extends EntityMapper<D, T, R>>
        implements ReactiveCrudService<ID, Q, D, R> {
    protected final Repository requestRepository;

    protected final Mapper entityMapper;

    public AbstractReactiveCrudService(Repository requestRepository) {
        this(requestRepository, null);
    }

    public AbstractReactiveCrudService(Repository requestRepository, Mapper mapper) {
        this.requestRepository = requestRepository;
        if (mapper == null) {
            Class<Mapper> mapperC = ClassUtil.getActualType(this, AbstractReactiveCrudService.class, "Mapper");
            this.entityMapper = Mappers.getMapper(mapperC);
        } else {
            this.entityMapper = mapper;
        }
    }

    @Override
    public Mono<Result<Page<R>>> read(ExtensionQueryRequest request, Q queryRequest, Pageable pageable) {
        Mono<Page<T>> pageMono = this.requestRepository.selectPageByQueryRequest(queryRequest, pageable);
        return pageMono.map(page -> page.map(this.entityMapper::copyToVo))
                .map(Results::ok);
    }

    @Override
    public Mono<Result<R>> read(ExtensionQueryRequest request, ID id) {
        return this.requestRepository.findById(id)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok)
                .switchIfEmpty(Mono.just(Results.fail("can not find by id:" + id)));
    }

    @Override
    public Mono<Result<R>> readOne(ExtensionQueryRequest request, Q queryRequest) {
        return this.requestRepository.selectOneByQueryRequest(queryRequest)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok)
                .switchIfEmpty(Mono.just(Results.fail("can not find by queryRequest")));
    }

    @Override
    public Mono<Result<Collection<R>>> read(ExtensionQueryRequest extensionQueryRequest, Q queryRequest, Sort sort) {
        return this.requestRepository.selectByQueryRequest(queryRequest, sort)
                .map(this.entityMapper::copyToVo)
                .collectList()
                .map(Results::ok);
    }

    @Override
    public Mono<Result<R>> create(ExtensionQueryRequest queryRequest, D dto) {
        T t = this.entityMapper.copyFromDto(dto);
        return this.requestRepository.save(t)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok);
    }

    @Override
    public Mono<Result<R>> update(ExtensionQueryRequest queryRequest, ID id, D dto) {
        return this.requestRepository.findById(id)
                .map(t -> this.entityMapper.mergeNoNullVal(t, this.entityMapper.copyFromDto(dto)))
                .flatMap(this.requestRepository::save)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok)
                .switchIfEmpty(Mono.just(Results.fail("can not find by id:" + id)));
    }

    @Override
    public Mono<Result<Void>> delete(ExtensionQueryRequest queryRequest, ID... id) {
        return this.requestRepository.deleteAllById(Arrays.asList(id))
                .then(Mono.just(Results.ok()));
    }
}
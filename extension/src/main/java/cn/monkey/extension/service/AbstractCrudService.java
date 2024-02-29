package cn.monkey.extension.service;

import cn.monkey.commons.data.*;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.commons.util.ClassUtil;
import cn.monkey.orm.QueryRequestRepository;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudService<Q extends QueryRequest, T, ID extends Serializable,
        D, R, Repository extends QueryRequestRepository<Q, T, ID>, Mapper extends EntityMapper<D, T, R>>
        implements CrudService<ID, Q, D, R> {
    protected final Repository requestRepository;

    protected final Mapper entityMapper;

    public AbstractCrudService(Repository requestRepository) {
        this(requestRepository, null);
    }

    public AbstractCrudService(Repository requestRepository, Mapper mapper) {
        this.requestRepository = requestRepository;
        if (mapper == null) {
            Class<Mapper> mapperC = ClassUtil.getActualType(this, AbstractCrudService.class, "Mapper");
            this.entityMapper = Mappers.getMapper(mapperC);
        } else {
            this.entityMapper = mapper;
        }
    }

    @Override
    public Result<Page<R>> read(ExtensionQueryRequest request, Q queryRequest, Pageable pageable) {
        Page<T> page = this.requestRepository.selectPageByQueryRequest(queryRequest, pageable);
        return Results.ok(page.map(this.entityMapper::copyToVo));
    }

    @Override
    public Result<R> read(ExtensionQueryRequest request, ID id) {
        return this.requestRepository.findById(id)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok)
                .orElse(Results.fail("can not find by id:" + id));
    }

    @Override
    public Result<R> readOne(ExtensionQueryRequest request, Q queryRequest) {
        return this.requestRepository.selectOneByQueryRequest(queryRequest)
                .map(this.entityMapper::copyToVo)
                .map(Results::ok)
                .orElse(Results.fail("can not find by queryRequest"));
    }

    @Override
    public Result<Collection<R>> read(ExtensionQueryRequest extensionQueryRequest, Q queryRequest, Sort sort) {
        List<T> tList = this.requestRepository.selectByQueryRequest(queryRequest, sort);
        if (CollectionUtils.isEmpty(tList)) {
            return Results.fail("can not find by queryRequest");
        }
        return Results.ok(tList.stream().map(this.entityMapper::copyToVo).toList());
    }

    @Override
    public Result<R> create(ExtensionQueryRequest queryRequest, D dto) {
        T t = this.entityMapper.copyFromDto(dto);
        T insert = this.requestRepository.save(t);
        return Results.ok(this.entityMapper.copyToVo(insert));
    }

    @Override
    public Result<R> update(ExtensionQueryRequest queryRequest, ID id, D dto) {
        Optional<T> optional = this.requestRepository.findById(id);
        if (optional.isEmpty()) {
            return Results.fail("can not find by id:" + id);
        }
        T t = this.entityMapper.copyFromDto(dto);
        t = this.entityMapper.mergeNoNullVal(optional.get(), t);
        T save = this.requestRepository.save(t);
        return Results.ok(this.entityMapper.copyToVo(save));
    }

    @Override
    public Result<Void> delete(ExtensionQueryRequest queryRequest, ID... id) {
        this.requestRepository.deleteAllById(Arrays.asList(id));
        return Results.ok();
    }
}
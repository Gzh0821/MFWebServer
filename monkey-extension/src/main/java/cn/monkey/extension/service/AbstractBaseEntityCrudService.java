package cn.monkey.extension.service;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.commons.data.DataStatus;
import cn.monkey.commons.data.EntityMapper;
import cn.monkey.commons.data.QueryRequest;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.orm.QueryRequestRepository;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractBaseEntityCrudService<Q extends QueryRequest, T extends BaseEntity, D, R, Repository extends QueryRequestRepository<Q, T, String>, Mapper extends EntityMapper<D, T, R>>
        extends AbstractCrudService<Q, T, String, D, R, Repository, Mapper> {
    public AbstractBaseEntityCrudService(Repository requestRepository) {
        super(requestRepository);
    }

    public AbstractBaseEntityCrudService(Repository requestRepository, Mapper mapper) {
        super(requestRepository, mapper);
    }

    @Override
    public Result<Void> delete(ExtensionQueryRequest queryRequest, String... id) {
        List<T> list = this.requestRepository.findAllById(Arrays.asList(id));
        if (CollectionUtils.isEmpty(list)) {
            return Results.ok();
        }
        List<T> collect = list.stream().peek(t -> t.setDataStatus(DataStatus.DELETED.getCode())).collect(Collectors.toList());
        this.requestRepository.saveAll(collect);
        return Results.ok();
    }
}

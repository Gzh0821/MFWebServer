package cn.monkey.extension.reactive.service;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.commons.data.DataStatus;
import cn.monkey.commons.data.EntityMapper;
import cn.monkey.commons.data.QueryRequest;
import cn.monkey.commons.data.vo.Result;
import cn.monkey.commons.data.vo.Results;
import cn.monkey.orm.QueryRequestRepository;
import cn.monkey.orm.reactive.ReactiveQueryRequestRepository;
import cn.monkey.spring.web.data.ExtensionQueryRequest;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractBaseEntityCrudService<Q extends QueryRequest, T extends BaseEntity, D, R, Repository extends ReactiveQueryRequestRepository<Q, T, String>, Mapper extends EntityMapper<D, T, R>>
        extends AbstractReactiveCrudService<Q, T, String, D, R, Repository, Mapper> {
    public AbstractBaseEntityCrudService(Repository requestRepository) {
        super(requestRepository);
    }

    public AbstractBaseEntityCrudService(Repository requestRepository, Mapper mapper) {
        super(requestRepository, mapper);
    }

    @Override
    public Mono<Result<Void>> delete(ExtensionQueryRequest queryRequest, String... id) {
        return this.requestRepository.findAllById(Arrays.asList(id))
                .doOnNext(t -> t.setDataStatus(DataStatus.DELETED.getCode()))
                .collectList()
                .flux().map(this.requestRepository::saveAll)
                .then(Mono.just(Results.ok()));
    }
}

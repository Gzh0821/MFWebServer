package cn.monkey.orm.elasticsearch;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;

import java.util.ArrayList;
import java.util.List;

public class SimpleElasticsearchCrudRepository<T, ID> extends SimpleElasticsearchRepository<T, ID> implements ElasticsearchCrudRepository<T, ID>,
        ApplicationContextAware, InitializingBean {

    protected ApplicationContext applicationContext;

    public SimpleElasticsearchCrudRepository(ElasticsearchEntityInformation<T, ID> metadata, ElasticsearchOperations operations) {
        super(metadata, operations);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected static <T> List<T> applyIterable(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t);
        }
        return list;
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return applyIterable(super.findAllById(ids));
    }

    @Override
    public List<T> findAll(Sort sort) {
        return applyIterable(super.findAll(sort));
    }

    @Override
    public List<T> findAll() {
        return applyIterable(super.findAll());
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return applyIterable(super.saveAll(entities));
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public ElasticsearchEntityInformation<T, ?> getEntityInformation() {
        return super.entityInformation;
    }

    @Override
    public ElasticsearchOperations getOperations() {
        return super.operations;
    }
}

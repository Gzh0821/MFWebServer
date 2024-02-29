package cn.monkey.orm.elasticsearch;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.orm.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;

import java.util.List;

public class SimpleElasticsearchBaseEntityRepository<T extends BaseEntity> extends SimpleElasticsearchCrudRepository<T, String>
        implements BaseEntityCrudRepository<T>, ApplicationContextAware, InitializingBean {

    private BeforeUpdateBehavior<BaseEntity> beforeUpdateBehavior;

    private BeforeCreateBehavior<BaseEntity> beforeCreateBehavior;

    public SimpleElasticsearchBaseEntityRepository(ElasticsearchEntityInformation<T, String> metadata, ElasticsearchOperations operations) {
        super(metadata, operations);
    }

    @Override
    public <S extends T> S save(S entity) {
        ElasticsearchEntityInformation<T, ?> entityInformation = this.getEntityInformation();
        if (entityInformation.isNew(entity)) {
            if (this.beforeCreateBehavior != null) {
                this.beforeCreateBehavior.beforeCreate(entity);
            }
        } else {
            if (this.beforeUpdateBehavior != null) {
                this.beforeUpdateBehavior.beforeUpdate(entity);
            }
        }
        return super.save(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        ElasticsearchEntityInformation<T, ?> entityInformation = this.getEntityInformation();
        entities.forEach(e -> {
            if (entityInformation.isNew(e)) {
                if (this.beforeCreateBehavior != null) {
                    this.beforeCreateBehavior.beforeCreate(e);
                }
            } else {
                if (this.beforeUpdateBehavior != null) {
                    this.beforeUpdateBehavior.beforeUpdate(e);
                }
            }
        });
        return super.saveAll(entities);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (applicationContext != null) {
            try {
                this.beforeCreateBehavior = applicationContext.getBean(BaseEntityBeforeCreateBehavior.class);
                this.beforeUpdateBehavior = applicationContext.getBean(BaseEntityBeforeUpdateBehavior.class);
            } catch (BeansException ignore) {
            }
        }
    }
}

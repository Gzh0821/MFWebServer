package cn.monkey.orm.jpa;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.orm.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

public class SimpleJpaBaseEntityRepository<T extends BaseEntity> extends SimpleJpaCrudRepository<T, String>
        implements BaseEntityCrudRepository<T>, ApplicationContextAware, InitializingBean {

    private BeforeUpdateBehavior<BaseEntity> beforeUpdateBehavior;

    private BeforeCreateBehavior<BaseEntity> beforeCreateBehavior;

    public SimpleJpaBaseEntityRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public SimpleJpaBaseEntityRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
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

    @Override
    public <S extends T> S save(S entity) {
        if (entityInformation.isNew(entity)) {
            if (this.beforeCreateBehavior != null) {
                this.beforeCreateBehavior.beforeCreate(entity);
            }
            entityManager.persist(entity);
            return entity;
        } else {
            if (this.beforeUpdateBehavior != null) {
                this.beforeUpdateBehavior.beforeUpdate(entity);
            }
            return entityManager.merge(entity);
        }
    }
}

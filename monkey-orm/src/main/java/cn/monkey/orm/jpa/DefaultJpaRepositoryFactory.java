package cn.monkey.orm.jpa;

import cn.monkey.orm.BaseEntityCrudRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class DefaultJpaRepositoryFactory extends JpaRepositoryFactory implements ApplicationContextAware {
    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public DefaultJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    protected ApplicationContext applicationContext;

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        if (BaseEntityCrudRepository.class.isAssignableFrom(repositoryInterface)
                && JpaCrudRepository.class.isAssignableFrom(repositoryInterface)) {
            return SimpleJpaBaseEntityRepository.class;
        }
        if (JpaCrudRepository.class.isAssignableFrom(repositoryInterface)) {
            return SimpleJpaCrudRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaRepositoryImplementation<?, ?> targetRepository = super.getTargetRepository(information, entityManager);
        if (targetRepository instanceof SimpleJpaCrudRepository<?, ?> repository) {
            repository.setApplicationContext(this.applicationContext);
            try {
                repository.afterPropertiesSet();
            } catch (Exception ignore) {
            }
        }
        return targetRepository;
    }


    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return super.getQueryLookupStrategy(key, evaluationContextProvider);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

package cn.monkey.orm.mongo;

import cn.monkey.orm.BaseEntityCrudRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class DefaultMongoRepositoryFactory extends MongoRepositoryFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Creates a new {@link MongoRepositoryFactory} with the given {@link MongoOperations}.
     *
     * @param mongoOperations must not be {@literal null}.
     */
    public DefaultMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        if (BaseEntityCrudRepository.class.isAssignableFrom(repositoryInterface)
                && MongoCrudRepository.class.isAssignableFrom(repositoryInterface)) {
            return SimpleMongoBaseEntityRepository.class;
        }
        if (MongoCrudRepository.class.isAssignableFrom(repositoryInterface)) {
            return SimpleMongoCrudRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        Object targetRepository = super.getTargetRepository(information);
        if (targetRepository instanceof SimpleMongoCrudRepository<?, ?> repository) {
            repository.setApplicationContext(this.applicationContext);
            try {
                repository.afterPropertiesSet();
            } catch (Exception ignore) {
            }
        }
        return targetRepository;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

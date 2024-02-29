package cn.monkey.orm.jpa;

import cn.monkey.orm.IRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@NoRepositoryBean
public interface JpaCrudRepository<T, ID> extends JpaRepositoryImplementation<T, ID>, IRepository<T, ID>, QueryByExampleExecutor<T> {
    JpaEntityInformation<T, ?> getEntityInformation();

    EntityManager getEntityManager();

}

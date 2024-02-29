package cn.monkey.orm;

import cn.monkey.commons.data.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseEntityCrudRepository<T extends BaseEntity> extends IRepository<T, String> {

}

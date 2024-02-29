package cn.monkey.orm.reactive;

import cn.monkey.commons.data.BaseEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReactiveBaseEntityCrudRepository<T extends BaseEntity> extends IReactiveRepository<T, String> {

}

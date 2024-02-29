package cn.monkey.orm.ldap;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.orm.BaseEntityCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LdapBaseEntityCrudRepository<T extends BaseEntity> extends BaseEntityCrudRepository<T>, LdapCrudRepository<T, String> {
}

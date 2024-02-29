package cn.monkey.orm;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.commons.data.KVPair;
import cn.monkey.commons.data.UserSession;

import java.util.Date;
import java.util.function.Supplier;

public interface RepositoryConfiguration {
    default BaseEntityBeforeCreateBehavior beforeCreateBehavior(Supplier<UserSession> userSessionSupplier) {
        return new BaseEntityBeforeCreateBehavior() {
            @Override
            public void beforeCreate(BaseEntity baseEntity) {
                UserSession userSession = userSessionSupplier.get();
                if (userSession != null) {
                    baseEntity.setCreator(KVPair.of(userSession.getUid(), userSession.getUsername()));
                    baseEntity.setUpdater(KVPair.of(userSession.getUid(), userSession.getUsername()));
                }
                long time = new Date().getTime();
                baseEntity.setCreateDateTime(time);
                baseEntity.setUpdateDateTime(time);
            }
        };
    }

    default BaseEntityBeforeUpdateBehavior beforeUpdateBehavior(Supplier<UserSession> userSessionSupplier) {

        return new BaseEntityBeforeUpdateBehavior() {
            @Override
            public void beforeUpdate(BaseEntity baseEntity) {
                UserSession userSession = userSessionSupplier.get();
                if (userSession != null) {
                    baseEntity.setUpdater(KVPair.of(userSession.getUid(), userSession.getUsername()));
                }
                long time = new Date().getTime();
                baseEntity.setUpdateDateTime(time);
            }
        };
    }
}

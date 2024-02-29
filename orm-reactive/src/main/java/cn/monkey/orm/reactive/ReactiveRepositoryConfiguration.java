package cn.monkey.orm.reactive;

import cn.monkey.commons.data.BaseEntity;
import cn.monkey.commons.data.KVPair;
import cn.monkey.commons.data.UserSession;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.NoSuchElementException;

public interface ReactiveRepositoryConfiguration {
    default ReactiveBeforeCreateBehavior<BaseEntity> beforeCreateBehavior() {
        return new ReactiveBeforeCreateBehavior<>() {
            @Override
            public Mono<Void> beforeCreate(BaseEntity baseEntity) {
                return Mono.just(baseEntity)
                        .contextWrite(context -> {
                            try {
                                UserSession userSession = context.get(UserSession.class);
                                baseEntity.setCreator(KVPair.of(userSession.getUid(), userSession.getUsername()));
                                baseEntity.setUpdater(KVPair.of(userSession.getUid(), userSession.getUsername()));
                            } catch (NoSuchElementException ignore) {
                            }
                            long time = new Date().getTime();
                            baseEntity.setCreateDateTime(time);
                            baseEntity.setUpdateDateTime(time);
                            return context;
                        }).then();
            }
        };
    }

    default ReactiveBeforeUpdateBehavior<BaseEntity> beforeUpdateBehavior() {

        return new ReactiveBeforeUpdateBehavior<>() {
            @Override
            public Mono<Void> beforeUpdate(BaseEntity baseEntity) {
                return Mono.just(baseEntity)
                        .contextWrite(context -> {
                            try {
                                UserSession userSession = context.get(UserSession.class);
                                baseEntity.setUpdater(KVPair.of(userSession.getUid(), userSession.getUsername()));
                            } catch (NoSuchElementException ignore) {
                            }
                            long time = new Date().getTime();
                            baseEntity.setUpdateDateTime(time);
                            return context;
                        }).then();
            }
        };
    }
}

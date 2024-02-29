package cn.monkey.orm.mongo;

import cn.monkey.orm.BeforeSelectBehavior;

public interface MongoBeforeSelectBehavior extends BeforeSelectBehavior {
    @Override
    void beforeSelect(Object o);
}

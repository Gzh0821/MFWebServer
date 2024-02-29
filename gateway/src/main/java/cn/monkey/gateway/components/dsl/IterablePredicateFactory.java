package cn.monkey.gateway.components.dsl;

import com.google.common.base.Preconditions;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class IterablePredicateFactory<D, T> implements PredicateFactory<D, T> {

    private final Function<D, List<D>> andFunc;

    private final Function<D, List<D>> orFunc;

    private Supplier<PredicateFactory<D, T>> delegateSupplier = () -> null;

    public IterablePredicateFactory(Function<D, List<D>> andFunc,
                                    Function<D, List<D>> orFunc) {
        this.andFunc = andFunc;
        this.orFunc = orFunc;
    }

    @Override
    public Predicate<T> apply(D definition) {
        Predicate<T> p = t -> true;
        PredicateFactory<D, T> delegate = delegateSupplier.get();
        if (delegate == null) {
            return p;
        }
        List<D> andDefinitions = this.andFunc.apply(definition);
        if (!CollectionUtils.isEmpty(andDefinitions)) {
            p = p.and(andDefinitions.stream().map(delegate).reduce(Predicate::and).get());
        }
        List<D> orDefinitions = this.orFunc.apply(definition);
        if (!CollectionUtils.isEmpty(orDefinitions)) {
            p = p.and(orDefinitions.stream().map(delegate).reduce(Predicate::or).get());
        }
        return p;
    }


    public void setDelegateSupplier(Supplier<PredicateFactory<D, T>> delegateSupplier) {
        Preconditions.checkNotNull(delegateSupplier);
        this.delegateSupplier = delegateSupplier;
    }
}

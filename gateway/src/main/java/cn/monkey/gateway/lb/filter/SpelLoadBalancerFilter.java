package cn.monkey.gateway.lb.filter;

import cn.monkey.gateway.lb.config.LoadBalancerFilterDefinition;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public class SpelLoadBalancerFilter implements Ordered, LoadBalancerFilter {

    public static final String PREDICATE_KEY = "predicate";

    public static final String REQUEST_KEY = "request-key";

    public static final String SERVICE_INSTANCE_KEY = "service-instance-key";

    public static final String DEFAULT_PREDICATE = "true";

    private final int order;

    protected final Expression expression;

    protected String requestKey = "request";

    protected String instanceKey = "instance";

    public SpelLoadBalancerFilter(LoadBalancerFilterDefinition definition) {
        this.order = definition.getOrder();
        Map<String, String> args = definition.getArgs();
        String predicate = args.getOrDefault(PREDICATE_KEY, DEFAULT_PREDICATE);
        String requestKey = args.get(REQUEST_KEY);
        if (!Strings.isNullOrEmpty(requestKey)) {
            this.setRequestKey(requestKey);
        }
        String serviceInstanceKey = args.get(SERVICE_INSTANCE_KEY);
        if (!Strings.isNullOrEmpty(serviceInstanceKey)) {
            this.setInstanceKey(serviceInstanceKey);
        }
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        this.expression = spelExpressionParser.parseExpression(predicate);
    }

    public void setInstanceKey(String instanceKey) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(instanceKey), "invalid instanceKey: " + instanceKey);
        this.instanceKey = instanceKey;
    }

    public void setRequestKey(String requestKey) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(requestKey), "invalid requestKey: " + requestKey);
        this.requestKey = requestKey;
    }

    protected boolean check(ServiceInstance serviceInstance, StandardEvaluationContext standardEvaluationContext) {
        standardEvaluationContext.setVariable(this.instanceKey, parseServiceInstance(serviceInstance));
        return Boolean.TRUE.equals(this.expression.getValue(standardEvaluationContext));
    }

    protected Object parseRequest(Request<?> request) {
        return request;
    }

    protected Object parseServiceInstance(ServiceInstance serviceInstance) {
        return serviceInstance;
    }

    protected void addVariable(StandardEvaluationContext standardEvaluationContext) {

    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public Flux<ServiceInstance> filter(LoadBalancerFilterChain chain, Request<?> request, List<ServiceInstance> serviceInstances) {
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        standardEvaluationContext.setVariable(this.requestKey, this.parseRequest(request));
        this.addVariable(standardEvaluationContext);
        return chain.doFilter(request, serviceInstances.stream().filter(serviceInstance -> this.check(serviceInstance, standardEvaluationContext)).toList());
    }
}

package cn.monkey.gateway.stream.blacklist.config;

import cn.monkey.gateway.stream.blacklist.async.*;
import cn.monkey.gateway.stream.blacklist.DefaultBlackEntity;
import cn.monkey.gateway.stream.blacklist.async.local.InMemoryBlackEntityRepository;
import cn.monkey.gateway.stream.blacklist.async.local.InMemoryFailCounter;
import com.google.common.base.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(AsyncBlackListAutoConfiguration.CONFIGURATION_PROPERTIES_PREFIX + ".enabled")
public class AsyncBlackListAutoConfiguration {

    static final String CONFIGURATION_PROPERTIES_PREFIX = "spring.cloud.stream.blacklist.async";


    @Bean
    @Primary
    BlackEntityRepository blackEntityRepository(List<BlackEntityRepository> blackEntityRepositories) {
        List<BlackEntityRepository> list = blackEntityRepositories.stream().sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
        return new CompositeBlackEntityRepository(list);
    }

    @Bean
    BlackEntityRepository inMemoryBlackEntityRepository(BlackListConfigurationProperties blackListConfigurationProperties) {
        BlackListConfigurationProperties.Key key = blackListConfigurationProperties.getKey();
        BlackListConfigurationProperties.Cache cache = blackListConfigurationProperties.getCache();
        InMemoryBlackEntityRepository blackEntityRepository = new InMemoryBlackEntityRepository(cache.getAutoRemoveTimeMs());
        if (key != null) {
            List<String> blackKeys = key.getBlackKeys();
            if (!CollectionUtils.isEmpty(blackKeys)) {
                Flux.fromIterable(blackKeys).map(DefaultBlackEntity::new).doOnNext(blackEntityRepository::add).subscribe();
            }
        }
        return blackEntityRepository;
    }

    @Bean
    FailCounter failCounter(BlackListConfigurationProperties blackListConfigurationProperties) {
        BlackListConfigurationProperties.Fail fail = blackListConfigurationProperties.getFail();
        return new InMemoryFailCounter(fail.getCountExpireTimeMs());
    }

    @Bean
    BlackKeyResolver ipKeyResolver(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String property = environment.getProperty(CONFIGURATION_PROPERTIES_PREFIX + ".keyResolver", "token,ip");
        if (Strings.isNullOrEmpty(property)) {
            return CompositeBlackKeyResolverBuilder
                    .builder().withIp().build();
        }
        String[] split = property.split(",");
        CompositeBlackKeyResolverBuilder builder = CompositeBlackKeyResolverBuilder.builder();
        for (String s : split) {
            if ("ip".equals(s)) {
                builder.withIp();
                continue;
            }
            if ("token".equals(s)) {
                builder.withToken();
            }
        }
        return builder.build();
    }

    @Bean
    DefaultBlackListFilter blackListFilter(BlackEntityRepository blackEntityRepository,
                                           BlackKeyResolver blackKeyResolver,
                                           BlackListConfigurationProperties blackListConfigurationProperties,
                                           FailCounter failCounter,
                                           FailPredicate failPredicate) {
        return new DefaultBlackListFilter(blackEntityRepository, failCounter, failPredicate, blackKeyResolver, blackListConfigurationProperties);
    }

    @ConditionalOnMissingBean(FailPredicate.class)
    @Bean
    FailPredicate failPredicate() {
        return NoopFailPredicate.INSTANCE;
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(CONFIGURATION_PROPERTIES_PREFIX + ".dynamic.enabled")
    static class DynamicAsyncBlackListAutoConfig {
        @Bean
        FailPredicate failPredicate(BlackListConfigurationProperties blackListConfigurationProperties) {
            BlackListConfigurationProperties.Fail fail = blackListConfigurationProperties.getFail();
            List<Integer> failHttpRespCode = fail.getFailHttpRespCode();
            if (CollectionUtils.isEmpty(failHttpRespCode)) {
                return new ResponseStatusPredicate();
            }
            HttpStatus[] array = failHttpRespCode.stream().map(HttpStatus::valueOf).toList().toArray(new HttpStatus[0]);
            return new ResponseStatusPredicate(array);
        }
    }
}

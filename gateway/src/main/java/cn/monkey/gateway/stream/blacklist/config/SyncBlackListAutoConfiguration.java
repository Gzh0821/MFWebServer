package cn.monkey.gateway.stream.blacklist.config;

import cn.monkey.gateway.stream.blacklist.DefaultBlackEntity;
import cn.monkey.gateway.stream.blacklist.sync.*;
import cn.monkey.gateway.stream.blacklist.sync.local.InMemoryBlackEntityRepository;
import cn.monkey.gateway.stream.blacklist.sync.local.InMemoryFailCounter;
import com.google.common.base.Strings;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static cn.monkey.gateway.stream.blacklist.config.SyncBlackListAutoConfiguration.CONFIGURATION_PROPERTIES_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(CONFIGURATION_PROPERTIES_PREFIX + ".enabled")
public class SyncBlackListAutoConfiguration {

    static final String CONFIGURATION_PROPERTIES_PREFIX = "spring.cloud.stream.blacklist.sync";

    @Bean
    BlackListChannelHandlerFactory blackListChannelHandlerFactory(BlackListConfigurationProperties blackListConfigurationProperties,
                                                                  BlackEntityRepository blackListRepository,
                                                                  FailCounter failCounter,
                                                                  FailPredicate failPredicate,
                                                                  BlackKeyResolver keyResolver) {
        return new DefaultBlackListChannelHandlerFactory(blackListConfigurationProperties, failCounter, failPredicate, blackListRepository, keyResolver);
    }

    @Bean
    BlackListCustomizer blackListCustomizer(BlackListChannelHandlerFactory blackListChannelHandlerFactory) {
        return new BlackListCustomizer(blackListChannelHandlerFactory);
    }

    @Bean
    BlackEntityRepository blackEntityRepository(BlackListConfigurationProperties blackListConfigurationProperties) {
        InMemoryBlackEntityRepository inMemoryBlackEntityRepository = new InMemoryBlackEntityRepository();
        BlackListConfigurationProperties.Key key = blackListConfigurationProperties.getKey();
        List<String> blackKeys = key.getBlackKeys();
        if (!CollectionUtils.isEmpty(blackKeys)) {
            blackKeys.stream().map(DefaultBlackEntity::new).forEach(inMemoryBlackEntityRepository::add);
        }
        return inMemoryBlackEntityRepository;
    }

    @Bean
    BlackKeyResolver blackKeyResolver(ConfigurableApplicationContext context) {
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
    FailCounter failCounter(BlackListConfigurationProperties blackListConfigurationProperties) {
        BlackListConfigurationProperties.Fail fail = blackListConfigurationProperties.getFail();
        return new InMemoryFailCounter(fail.getCountExpireTimeMs());
    }

    @ConditionalOnMissingBean(FailPredicate.class)
    @Bean
    FailPredicate failPredicate() {
        return new NoopFailPredicate();
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty("spring.cloud.stream.blacklist.sync.dynamic.enabled")
    static class DynamicSyncBlackListAutoConfig {
        @Bean
        FailPredicate failPredicate(BlackListConfigurationProperties blackListConfigurationProperties) {
            BlackListConfigurationProperties.Fail fail = blackListConfigurationProperties.getFail();
            List<Integer> failHttpRespCode = fail.getFailHttpRespCode();
            FailPredicate failPredicate;
            if (CollectionUtils.isEmpty(failHttpRespCode)) {
                throw new IllegalArgumentException("sync blacklist config is dynamic but failHttpRespCode is empty");
            } else {
                HttpResponseStatus[] array = failHttpRespCode.stream().map(HttpResponseStatus::valueOf).toList().toArray(new HttpResponseStatus[]{});
                failPredicate = new ResponseStatusPredicate(array);
            }
            return failPredicate;
        }
    }
}

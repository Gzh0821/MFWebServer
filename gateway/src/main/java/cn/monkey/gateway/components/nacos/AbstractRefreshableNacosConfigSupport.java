package cn.monkey.gateway.components.nacos;

import cn.monkey.commons.util.ClassUtil;
import cn.monkey.gateway.utils.ConfigurationUtils;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.util.function.Consumer;

public abstract class AbstractRefreshableNacosConfigSupport<T> implements Consumer<T>, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AbstractRefreshableNacosConfigSupport.class);
    protected final Environment environment;
    protected final NacosConfigManager nacosConfigManager;
    protected final Class<T> configPropertiesType;

    public AbstractRefreshableNacosConfigSupport(Environment environment,
                                                 NacosConfigManager nacosConfigManager) {
        this.environment = environment;
        this.nacosConfigManager = nacosConfigManager;
        this.configPropertiesType = ClassUtil.getActualType(this, AbstractRefreshableNacosConfigSupport.class, "T");
    }

    protected void decodeConfigAndConsume(String fileExtension, String config) {
        if (Strings.isNullOrEmpty(config)) {
            return;
        }
        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(configPropertiesType, ConfigurationProperties.class);
        String name = annotation == null ? "" : annotation.prefix();
        try {
            T load = ConfigurationUtils.load(name, fileExtension, config, (binder) -> binder.bind(name, configPropertiesType));
            if (load != null) {
                this.accept(load);
            }
        } catch (Exception e) {
            log.error("config load error:\n", e);
        }
    }

    protected void bindConfigListener() {
        try {
            ConfigService configService = this.nacosConfigManager.getConfigService();
            NacosConfigProperties nacosConfigProperties = this.nacosConfigManager.getNacosConfigProperties();
            String appName = environment.getProperty("spring.application.name");
            String fileExtension = nacosConfigProperties.getFileExtension();
            String group = nacosConfigProperties.getGroup();
            String config = configService.getConfig(appName, group, nacosConfigProperties.getTimeout());
            this.decodeConfigAndConsume(fileExtension, config);
            configService.addListener(appName, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    AbstractRefreshableNacosConfigSupport.this.decodeConfigAndConsume(fileExtension, configInfo);
                }
            });
        } catch (NacosException e) {
            log.error("nacos configListener bind error: ", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.bindConfigListener();
    }
}

package cn.monkey.gateway.utils;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.function.Function;

public interface ConfigurationUtils {

    static <T> T load(String name, String fileExtension, String config, Function<Binder, BindResult<T>> binderFunc) throws Exception {
        List<PropertySource<?>> propertySourceList;
        ByteArrayResource resource = new ByteArrayResource(config.getBytes());
        switch (fileExtension) {
            case "yaml" -> {
                YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
                propertySourceList = yamlPropertySourceLoader.load(name, resource);
            }
            case "xml", "properties" -> {
                PropertiesPropertySourceLoader propertiesPropertySourceLoader = new PropertiesPropertySourceLoader();
                propertySourceList = propertiesPropertySourceLoader.load(name, resource);
            }
            default -> throw new IllegalArgumentException("unsupported file-extension: " + fileExtension);
        }
        if (propertySourceList.isEmpty()) {
            return null;
        }
        ConfigurationPropertySource configurationPropertySource = ConfigurationPropertySource.from(propertySourceList.get(0));
        Binder binder = new Binder(configurationPropertySource);
        BindResult<T> bindResult = binderFunc.apply(binder);
        if (bindResult.isBound()) {
            return bindResult.get();
        }
        return null;
    }
}

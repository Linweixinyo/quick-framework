package org.weixin.framework.common.toolkit.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Resource res = resource.getResource();
        String sourceName = res.getFilename();

        // Check if the file is a YAML file
        if (StrUtil.isNotBlank(sourceName) && StrUtil.endWithAny(sourceName, ".yml", ".yaml")) {
            // Use YamlPropertySourceLoader to load the YAML file
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            List<PropertySource<?>> sources = loader.load(name, res);

            // Return the first loaded PropertySource, or null if none were found
            return CollectionUtil.getFirst(sources);
        }

        // Fallback to the default implementation for other formats
        return super.createPropertySource(name, resource);
    }

}

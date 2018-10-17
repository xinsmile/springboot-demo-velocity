package com.xingy.framework.velocity.support;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author xinguiyuan
 * @date 2018-10-10 14:58:29
 * 
 */
public class SpringResourceLoader extends ResourceLoader {
    public static final String NAME = "spring";
    public static final String SPRING_RESOURCE_LOADER_CLASS = "spring.resource.loader.class";
    public static final String SPRING_RESOURCE_LOADER_CACHE = "spring.resource.loader.cache";
    public static final String SPRING_RESOURCE_LOADER = "spring.resource.loader";
    public static final String SPRING_RESOURCE_LOADER_PATH = "spring.resource.loader.path";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private org.springframework.core.io.ResourceLoader resourceLoader;
    private String[] resourceLoaderPaths;

    public SpringResourceLoader() {
    }

    public void init(ExtendedProperties configuration) {
        this.resourceLoader = (org.springframework.core.io.ResourceLoader)this.rsvc.getApplicationAttribute("spring.resource.loader");
        String resourceLoaderPath = (String)this.rsvc.getApplicationAttribute("spring.resource.loader.path");
        if (this.resourceLoader == null) {
            throw new IllegalArgumentException("'resourceLoader' application attribute must be present for SpringResourceLoader");
        } else if (resourceLoaderPath == null) {
            throw new IllegalArgumentException("'resourceLoaderPath' application attribute must be present for SpringResourceLoader");
        } else {
            this.resourceLoaderPaths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);

            for(int i = 0; i < this.resourceLoaderPaths.length; ++i) {
                String path = this.resourceLoaderPaths[i];
                if (!path.endsWith("/")) {
                    this.resourceLoaderPaths[i] = path + "/";
                }
            }

            if (this.logger.isInfoEnabled()) {
                this.logger.info("SpringResourceLoader for Velocity: using resource loader [" + this.resourceLoader + "] and resource loader paths " + Arrays.asList(this.resourceLoaderPaths));
            }

        }
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking for Velocity resource with name [" + source + "]");
        }

        String[] var2 = this.resourceLoaderPaths;
        int var3 = var2.length;
        int var4 = 0;

        while(var4 < var3) {
            String resourceLoaderPath = var2[var4];
            Resource resource = this.resourceLoader.getResource(resourceLoaderPath + source);

            try {
                return resource.getInputStream();
            } catch (IOException var8) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Could not find Velocity resource: " + resource);
                }

                ++var4;
            }
        }

        throw new ResourceNotFoundException("Could not find resource [" + source + "] in Spring resource loader path");
    }

    public boolean isSourceModified(org.apache.velocity.runtime.resource.Resource resource) {
        return false;
    }

    public long getLastModified(org.apache.velocity.runtime.resource.Resource resource) {
        return 0L;
    }
}

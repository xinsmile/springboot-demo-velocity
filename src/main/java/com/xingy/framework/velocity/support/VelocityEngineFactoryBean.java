package com.xingy.framework.velocity.support;

import java.io.IOException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

/**
 * @author xinguiyuan
 * @date 2018-10-10 14:26:51
 * 
 */
public class VelocityEngineFactoryBean extends VelocityEngineFactory
		implements FactoryBean<VelocityEngine>, InitializingBean, ResourceLoaderAware {

	private VelocityEngine velocityEngine;

	@Override
	public void afterPropertiesSet() throws IOException, VelocityException {
		this.velocityEngine = createVelocityEngine();
	}

	@Override
	public VelocityEngine getObject() {
		return this.velocityEngine;
	}

	@Override
	public Class<? extends VelocityEngine> getObjectType() {
		return VelocityEngine.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}

package com.xingy.framework.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.NestedIOException;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author xinguiyuan
 * @date 2018-10-10 14:58:29
 * 
 */
public class VelocityView extends AbstractTemplateView {
    private Map<String, Class<?>> toolAttributes;
    private String dateToolAttribute;
    private String numberToolAttribute;
    private String encoding;
    private boolean cacheTemplate = false;
    private VelocityEngine velocityEngine;
    private Template template;

    public VelocityView() {
    }

    public void setToolAttributes(Map<String, Class<?>> toolAttributes) {
        this.toolAttributes = toolAttributes;
    }

    public void setDateToolAttribute(String dateToolAttribute) {
        this.dateToolAttribute = dateToolAttribute;
    }

    public void setNumberToolAttribute(String numberToolAttribute) {
        this.numberToolAttribute = numberToolAttribute;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected String getEncoding() {
        return this.encoding;
    }

    public void setCacheTemplate(boolean cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    protected boolean isCacheTemplate() {
        return this.cacheTemplate;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    protected VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }

    protected void initApplicationContext() throws BeansException {
        super.initApplicationContext();
        if (this.getVelocityEngine() == null) {
            this.setVelocityEngine(this.autodetectVelocityEngine());
        }

    }

    protected VelocityEngine autodetectVelocityEngine() throws BeansException {
        try {
            VelocityConfig velocityConfig = (VelocityConfig)BeanFactoryUtils.beanOfTypeIncludingAncestors(this.getApplicationContext(), VelocityConfig.class, true, false);
            return velocityConfig.getVelocityEngine();
        } catch (NoSuchBeanDefinitionException var2) {
            throw new ApplicationContextException("Must define a single VelocityConfig bean in this web application context (may be inherited): VelocityConfigurer is the usual implementation. This bean may be given any name.", var2);
        }
    }

    public boolean checkResource(Locale locale) throws Exception {
        try {
            this.template = this.getTemplate(this.getUrl());
            return true;
        } catch (ResourceNotFoundException var3) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No Velocity view found for URL: " + this.getUrl());
            }

            return false;
        } catch (Exception var4) {
            throw new NestedIOException("Could not load Velocity template for URL [" + this.getUrl() + "]", var4);
        }
    }

    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.exposeHelpers(model, request);
        Context velocityContext = this.createVelocityContext(model, request, response);
        this.exposeHelpers(velocityContext, request, response);
        this.exposeToolAttributes(velocityContext, request);
        this.doRender(velocityContext, response);
    }

    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
    }

    protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.createVelocityContext(model);
    }

    protected Context createVelocityContext(Map<String, Object> model) throws Exception {
        return new VelocityContext(model);
    }

    protected void exposeHelpers(Context velocityContext, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.exposeHelpers(velocityContext, request);
    }

    protected void exposeHelpers(Context velocityContext, HttpServletRequest request) throws Exception {
    }

    protected void exposeToolAttributes(Context velocityContext, HttpServletRequest request) throws Exception {
        if (this.toolAttributes != null) {
            Iterator var3 = this.toolAttributes.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Class<?>> entry = (Map.Entry)var3.next();
                String attributeName = (String)entry.getKey();
                Class toolClass = (Class)entry.getValue();

                try {
                    Object tool = toolClass.newInstance();
                    this.initTool(tool, velocityContext);
                    velocityContext.put(attributeName, tool);
                } catch (Exception var8) {
                    throw new NestedServletException("Could not instantiate Velocity tool '" + attributeName + "'", var8);
                }
            }
        }

        if (this.dateToolAttribute != null || this.numberToolAttribute != null) {
            if (this.dateToolAttribute != null) {
                velocityContext.put(this.dateToolAttribute, new VelocityView.LocaleAwareDateTool(request));
            }

            if (this.numberToolAttribute != null) {
                velocityContext.put(this.numberToolAttribute, new VelocityView.LocaleAwareNumberTool(request));
            }
        }

    }

    protected void initTool(Object tool, Context velocityContext) throws Exception {
    }

    protected void doRender(Context context, HttpServletResponse response) throws Exception {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Rendering Velocity template [" + this.getUrl() + "] in VelocityView '" + this.getBeanName() + "'");
        }

        this.mergeTemplate(this.getTemplate(), context, response);
    }

    protected Template getTemplate() throws Exception {
        return this.isCacheTemplate() && this.template != null ? this.template : this.getTemplate(this.getUrl());
    }

    protected Template getTemplate(String name) throws Exception {
        return this.getEncoding() != null ? this.getVelocityEngine().getTemplate(name, this.getEncoding()) : this.getVelocityEngine().getTemplate(name);
    }

    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws Exception {
        try {
            template.merge(context, response.getWriter());
        } catch (MethodInvocationException var6) {
            Throwable cause = var6.getWrappedThrowable();
            throw new NestedServletException("Method invocation failed during rendering of Velocity view with name '" + this.getBeanName() + "': " + var6.getMessage() + "; reference [" + var6.getReferenceName() + "], method '" + var6.getMethodName() + "'", (Throwable)(cause == null ? var6 : cause));
        }
    }

    private static class LocaleAwareNumberTool extends NumberTool {
        private final HttpServletRequest request;

        public LocaleAwareNumberTool(HttpServletRequest request) {
            this.request = request;
        }

        public Locale getLocale() {
            return RequestContextUtils.getLocale(this.request);
        }
    }

    private static class LocaleAwareDateTool extends DateTool {
        private final HttpServletRequest request;

        public LocaleAwareDateTool(HttpServletRequest request) {
            this.request = request;
        }

        public Locale getLocale() {
            return RequestContextUtils.getLocale(this.request);
        }

        public TimeZone getTimeZone() {
            TimeZone timeZone = RequestContextUtils.getTimeZone(this.request);
            return timeZone != null ? timeZone : super.getTimeZone();
        }
    }
}

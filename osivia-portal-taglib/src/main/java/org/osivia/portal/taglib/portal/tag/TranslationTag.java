package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Translation tag.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class TranslationTag extends SimpleTagSupport {

    /** Internationalization service attribute name. */
    private static final String INTERNATIONALIZATION_SERVICE_ATTRIBUTE_NAME = "osivia.internationalization.service";
    /** Property attributes separator. */
    private static final String SEPARATOR = ",";

    /** Internationalization resource property key. */
    private String key;
    /** Internationalization resource property arguments, separated by commas. */
    private String args;


    /**
     * Constructor.
     */
    public TranslationTag() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        Locale locale = pageContext.getRequest().getLocale();

        // Internationalization service
        IInternationalizationService internationalizationService = this.getInternationalizationService(pageContext);
        // Current class loader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // Customized class loader
        ClassLoader customizedClassLoader = this.getCustomizedClassLoader(pageContext);

        // Property arguments
        Object[] arguments = StringUtils.split(this.args, SEPARATOR);

        // Internationalization service invocation
        String property = internationalizationService.getString(this.key, locale, classLoader, customizedClassLoader, arguments);

        // Write property into JSP
        JspWriter out = pageContext.getOut();
        out.write(property);
    }


    /**
     * Get internationalization service.
     *
     * @param pageContext page context
     * @return internationalization service
     */
    private IInternationalizationService getInternationalizationService(PageContext pageContext) {
        IInternationalizationService internationalizationService = (IInternationalizationService) pageContext.getAttribute(
                INTERNATIONALIZATION_SERVICE_ATTRIBUTE_NAME, PageContext.REQUEST_SCOPE);
        if (internationalizationService == null) {
            internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
            pageContext.setAttribute(INTERNATIONALIZATION_SERVICE_ATTRIBUTE_NAME, internationalizationService, PageContext.REQUEST_SCOPE);
        }
        return internationalizationService;
    }


    /**
     * Get customized class loader.
     * 
     * @param pageContext page context
     * @return class loader
     */
    private ClassLoader getCustomizedClassLoader(PageContext pageContext) {
        ClassLoader customizedClassLoader = null;
        PortletInvocation invocation = (PortletInvocation) pageContext.getRequest().getAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);
        if (invocation != null) {
            PortletRequest request = (PortletRequest) invocation.getDispatchedRequest().getAttribute("javax.portlet.request");
            customizedClassLoader = (ClassLoader) request.getAttribute("osivia.customizer.cms.jsp.classloader");
        }
        return customizedClassLoader;
    }


    /**
     * Setter for key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Setter for args.
     *
     * @param args the args to set
     */
    public void setArgs(String args) {
        this.args = args;
    }

}

package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.taglib.common.PortalSimpleTag;
import org.springframework.context.ApplicationContext;

/**
 * Translation tag.
 *
 * @author Cédric Krommenhoek
 * @see PortalSimpleTag
 */
public class TranslationTag extends PortalSimpleTag {

    /** Internationalization service attribute name. */
    private static final String INTERNATIONALIZATION_SERVICE_ATTRIBUTE_NAME = "osivia.internationalization.service";
    /** Property attributes separator. */
    private static final String SEPARATOR = ",";

    /** Internationalization resource property key. */
    private String key;
    /** Internationalization resource class loader. */
    private ClassLoader classLoader;
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
        // Page context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Servlet request
        ServletRequest servletRequest = pageContext.getRequest();
        
        Boolean layoutParsing = (Boolean) servletRequest.getAttribute(InternalConstants.ATTR_LAYOUT_PARSING);
        if (BooleanUtils.isNotTrue(layoutParsing)) {
            // Portlet request
            PortletRequest portletRequest = this.getPortletRequest();
            // Portlet context
            PortletContext portletContext = this.getPortletContext();

            // Locale
            Locale locale = servletRequest.getLocale();

            // Internationalization service
            IInternationalizationService internationalizationService = this.getInternationalizationService(pageContext);

            // Current class loaders
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // Customized class loader
            ClassLoader customizedClassLoader;
            if ((this.classLoader == null) && (portletRequest != null)) {
                customizedClassLoader = (ClassLoader) portletRequest.getAttribute("osivia.customizer.cms.jsp.classloader");
            } else {
                customizedClassLoader = this.classLoader;
            }

            // Optional Spring framework application context
            ApplicationContext applicationContext = this.getApplicationContext();

            // Property arguments
            Object[] arguments = StringUtils.split(this.args, SEPARATOR);

            // Internationalization service invocation
            String property = internationalizationService.getString(this.key, locale, classLoader, customizedClassLoader, applicationContext, arguments);

            // Write property into JSP
            JspWriter out = pageContext.getOut();
            out.write(property);
        }
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
     * Setter for key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Setter for classLoader.
     * 
     * @param classLoader the classLoader to set
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
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

package org.osivia.portal.taglib.taglib.portal;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Translation tag.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class TranslationTag extends SimpleTagSupport {

    /** Bundle factory attribute name. */
    private static final String BUNDLE_FACTORY_ATTRIBUTE_NAME = "osivia.internationalization.bundleFactory";
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

        IBundleFactory bundleFactory = (IBundleFactory) pageContext.getAttribute(BUNDLE_FACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
        if (bundleFactory == null) {
            // Internationalization service
            IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                    IInternationalizationService.MBEAN_NAME);
            // Current class loader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Set bundle factory
            bundleFactory = internationalizationService.getBundleFactory(classLoader);
            pageContext.setAttribute(BUNDLE_FACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
        }

        // Property arguments
        Object[] arguments = StringUtils.split(this.args, SEPARATOR);

        // Internationalization service invocation
        Bundle bundle = bundleFactory.getBundle(locale);
        String property = bundle.getString(this.key, arguments);

        // Write property into JSP
        JspWriter out = pageContext.getOut();
        out.write(property);
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

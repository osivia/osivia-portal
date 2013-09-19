package org.osivia.portal.core.tags;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Internationalization service tag.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class InternationalizationServiceTag extends SimpleTagSupport {

    /** Bundle factory. */
    private static IBundleFactory bundleFactory;

    /** Resource property key. */
    private String key;


    /**
     * Default constructor.
     */
    public InternationalizationServiceTag() {
        super();

        if (bundleFactory == null) {
            ClassLoader classLoader = this.getClass().getClassLoader();
            IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                    IInternationalizationService.MBEAN_NAME);
            bundleFactory = internationalizationService.getBundleFactory(classLoader);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        Locale locale = pageContext.getRequest().getLocale();

        // Internationalization service invocation
        Bundle bundle = bundleFactory.getBundle(locale);
        String property = bundle.getString(this.key);

        JspWriter out = pageContext.getOut();
        out.write(property);
        out.flush();
    }


    /**
     * Getter for key.
     *
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Setter for key.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

}

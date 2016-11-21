package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Format relative date tag.
 * 
 * @author ckrommenhoek
 */
public class FormatRelativeDateTag extends SimpleTagSupport {

    /** Date. */
    private Date value;
    /** Tooltip indicator. */
    private boolean tooltip;
    /** Capitalize indicator. */
    private boolean capitalize;


    /** Internationalization bundle factory. */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public FormatRelativeDateTag() {
        super();
        this.tooltip = true;

        // Internationalization bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Page context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Locale
        Locale locale = pageContext.getRequest().getLocale();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(locale);
        
        if (this.value != null) {
            // Current date
            Date currentDate = new Date();
            // Current calendar
            Calendar currentCalendar = DateUtils.toCalendar(currentDate);
           
            
            // Calendar fields
            Integer[] fields = new Integer[]{Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_YEAR, Calendar.HOUR_OF_DAY, Calendar.MINUTE};
            
            // Current field
            Integer currentField = null;
            // Amount
            int amount = 0;
            
            for (Integer field : fields) {
                // Calendar
                Calendar calendar = DateUtils.toCalendar(this.value);
                calendar.add(field, 1);
                
                if (calendar.before(currentCalendar)) {
                    currentField = field;
                    
                    while (calendar.before(currentCalendar)) {
                        calendar.add(field, 1);
                        amount++;
                    }
                    
                    break;
                }
            }
            
            
            // Internationalization key
            String key;
            if (currentField == null) {
                key = "RELATIVE_DATE_JUST_NOW";
            } else {
                String fragment;
                switch (currentField) {
                    case Calendar.YEAR:
                        fragment = "YEAR";
                        break;

                    case Calendar.MONTH:
                        fragment = "MONTH";
                        break;

                    case Calendar.WEEK_OF_YEAR:
                        fragment = "WEEK";
                        break;

                    case Calendar.DAY_OF_YEAR:
                        fragment = "DAY";
                        break;

                    case Calendar.HOUR:
                        fragment = "HOUR";
                        break;

                    case Calendar.MINUTE:
                        fragment = "MINUTE";
                        break;

                    default:
                        fragment = null;
                }

                if (fragment == null) {
                    key = "RELATIVE_DATE_JUST_NOW";
                } else if (amount == 1) {
                    key = "RELATIVE_DATE_ONE_" + fragment + "_AGO";
                } else {
                    key = "RELATIVE_DATE_N_" + fragment + "S_AGO";
                }
            }

            // Text
            String text = bundle.getString(key, amount);
            
            if (this.capitalize) {
                text = StringUtils.capitalize(text);
            }


            // Container
            Element container;
            if (this.tooltip) {
                container = DOM4JUtils.generateElement("abbr", null, text);

                // Date format
                DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

                DOM4JUtils.addAttribute(container, "title", dateFormat.format(this.value));
            } else {
                container = DOM4JUtils.generateElement("span", null, text);
            }

            // HTML writer
            HTMLWriter htmlWriter = new HTMLWriter(this.getJspContext().getOut());
            htmlWriter.setEscapeText(false);
            htmlWriter.write(container);
        }
    }




    /**
     * Setter for value.
     * 
     * @param value the value to set
     */
    public void setValue(Date value) {
        this.value = value;
    }

    /**
     * Setter for tooltip.
     * 
     * @param tooltip the tooltip to set
     */
    public void setTooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Setter for capitalize.
     * 
     * @param capitalize the capitalize to set
     */
    public void setCapitalize(boolean capitalize) {
        this.capitalize = capitalize;
    }

}

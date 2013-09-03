package org.osivia.portal.core.internationalization;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.NumberUtils;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.customization.ICustomizationService;

/**
 * Internationalization service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IInternationalizationService
 */
public class InternationalizationService implements IInternationalizationService {

    /** Customization service. */
    private ICustomizationService customizationService;


    /**
     * Default constructor.
     */
    public InternationalizationService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public IBundleFactory getBundleFactory(ClassLoader classLoader) {
        return new BundleFactory(this, classLoader);
    }


    /**
     * {@inheritDoc}
     */
    public String getString(String key, Locale locale, Object... args) {
        return this.getString(key, locale, null, args);
    }


    /**
     * {@inheritDoc}
     */
    public String getString(String key, Locale locale, ClassLoader classLoader, Object... args) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_KEY, key);
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_LOCALE, locale);
        CustomizationContext context = new CustomizationContext(attributes);

        // Customizer invocation
        this.customizationService.customize(IInternationalizationService.CUSTOMIZER_ID, context);

        String pattern;
        if (attributes.containsKey(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT)) {
            // Custom result
            pattern = (String) attributes.get(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT);
        } else {
            // Get resource bundle
            ResourceBundle resourceBundle = null;
            if (classLoader != null) {
                resourceBundle = ResourceBundle.getBundle(InternationalizationConstants.RESOURCE_BUNDLE_NAME, locale, classLoader);
            }
            if (resourceBundle == null) {
                // Portal default result
                resourceBundle = ResourceBundle.getBundle(InternationalizationConstants.RESOURCE_BUNDLE_NAME, locale);
            }

            try {
                pattern = resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                return "[Missing resource: " + key + "]";
            }
        }

        Object[] formattedArguments = this.formatArguments(args, locale);
        return MessageFormat.format(pattern, formattedArguments);
    }


    /**
     * Utility method used to format arguments.
     *
     * @param args arguments
     * @param locale locale
     * @return formatted arguments
     */
    private Object[] formatArguments(Object[] args, Locale locale) {
        if (args == null) {
            return null;
        }

        List<Object> formattedArguments = new ArrayList<Object>(args.length);
        for (Object arg : args) {
            if (NumberUtils.isNumber(arg.toString())) {
                // Number
                String number = NumberFormat.getNumberInstance(locale).format(arg);
                formattedArguments.add(number);
            } else if (arg instanceof Date) {
                // Date
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(arg);
                formattedArguments.add(date);
            } else {
                // Default : text
                formattedArguments.add(arg);
            }
        }

        return formattedArguments.toArray();
    }


    /**
     * Getter for customizationService.
     *
     * @return the customizationService
     */
    public ICustomizationService getCustomizationService() {
        return this.customizationService;
    }

    /**
     * Setter for customizationService.
     *
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }

}

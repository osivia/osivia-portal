package org.osivia.portal.core.internationalization;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.auth.constants.InternationalizationConstants;
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
     * {@inheritDoc}
     */
    public String getString(String key, Locale locale) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_KEY, key);
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_LOCALE, locale);
        CustomizationContext context = new CustomizationContext(attributes);

        // Customizer invocation
        this.customizationService.customize(IInternationalizationService.CUSTOMIZER_ID, context);

        if (attributes.containsKey(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT)) {
            // Custom result
            String result = (String) attributes.get(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT);
            return result;
        } else {
            // Portal default result
            ResourceBundle resourceBundle = ResourceBundle.getBundle(InternationalizationConstants.RESOURCE_BUNDLE_NAME, locale);
            try {
                String result = resourceBundle.getString(key);
                return result;
            } catch (MissingResourceException e) {
                return "[Missing resource: " + key + "]";
            }
        }
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

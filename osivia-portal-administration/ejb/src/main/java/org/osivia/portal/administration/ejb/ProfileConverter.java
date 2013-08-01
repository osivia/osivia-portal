package org.osivia.portal.administration.ejb;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

/**
 * Profile converter.
 *
 * @author Cédric Krommenhoek
 * @see Converter
 */
public class ProfileConverter implements Converter {

    /** String separator. */
    private static final String SEPARATOR = ":";


    /**
     * Default constructor.
     */
    public ProfileConverter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String[] stringsArray = value.split(SEPARATOR);
        ProfileData data = new ProfileData(stringsArray);
        return data;
    }


    /**
     * {@inheritDoc}
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        ProfileData data = (ProfileData) value;
        String[] stringsArray = data.toStringsArray();
        String result = StringUtils.join(stringsArray, SEPARATOR);
        return result;
    }

}

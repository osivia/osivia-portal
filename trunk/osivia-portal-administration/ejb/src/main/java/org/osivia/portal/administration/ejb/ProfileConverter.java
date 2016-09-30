/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
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

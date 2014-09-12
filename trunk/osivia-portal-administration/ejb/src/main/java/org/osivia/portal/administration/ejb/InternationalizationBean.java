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

import java.io.Serializable;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;

/**
 * Internationalization bean.
 *
 * @author Cédric Krommenhoek
 * @see Serializable
 */
@Name("internationalizationBean")
@Scope(ScopeType.APPLICATION)
public class InternationalizationBean implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Internationalization service. */
    private IInternationalizationService internationalizationService;

    /** Locale. */
    private Locale locale;


    /**
     * Default constructor.
     */
    public InternationalizationBean() {
        super();
    }


    /**
     * Init method.
     */
    @Create
    public void init() {
        PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        this.internationalizationService = (IInternationalizationService) portletContext.getAttribute(AdministrationConstants.INTERNATIONALIZATION_SERVICE_NAME);
        this.locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }


    /**
     * Access to bundle resource string.
     * 
     * @param key bundle resource key
     * @return internationalized string
     */
    public String getString(String key) {
        return this.internationalizationService.getString(key, this.locale);
    }


}

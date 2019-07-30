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
package org.osivia.portal.core.oauth2;

import java.util.HashMap;
import java.util.Map;

import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.oauth2.ClientDetail;
import org.osivia.portal.api.oauth2.IOAuth2Service;
import org.osivia.portal.core.customization.ICustomizationService;



/**
 * OAuth2 service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see IInternationalizationService
 */
public class OAuth2Service implements IOAuth2Service {

    /** Customization service. */
    private ICustomizationService customizationService;


    /**
     * Constructor.
     */
    public OAuth2Service() {
        super();

    }


    

    @Override
    public ClientDetail getClientDetail(String clientId) {
        ClientDetail clientdetail = null;
        
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IOAuth2Service.CUSTOMIZER_ATTRIBUTE_CLIENT_KEY, clientId);

        CustomizationContext context = new CustomizationContext(attributes);

        
        // Customizer invocation
        this.customizationService.customize(IOAuth2Service.CLIENT_CUSTOMIZER_ID, context);
        
        
        if (attributes.containsKey(IOAuth2Service.CUSTOMIZER_ATTRIBUTE_CLIENT_RESULT)) {
            // Custom result
            clientdetail = (ClientDetail) attributes.get(IOAuth2Service.CUSTOMIZER_ATTRIBUTE_CLIENT_RESULT);
        } 
        

        return clientdetail;
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

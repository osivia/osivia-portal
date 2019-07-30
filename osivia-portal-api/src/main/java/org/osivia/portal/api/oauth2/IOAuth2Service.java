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
package org.osivia.portal.api.oauth2;

/**
 * OAuth2 client detail service
 *
 * @author Jean-Sébastien Steux
 */
public interface IOAuth2Service {

    
    /** MBean name. */
    String MBEAN_NAME = "osivia:service=OAuth2Service";
    
    /** Client detail customizer identifier. */
    String CLIENT_CUSTOMIZER_ID = "osivia.customizer.oauth2.client.id";
    /** Client detail customizer resource key attribute. */
    String CUSTOMIZER_ATTRIBUTE_CLIENT_KEY = "osivia.customizer.oauth2.clientId.key";
    /** Client detail customizer custom result attribute. */
    String CUSTOMIZER_ATTRIBUTE_CLIENT_RESULT = "osivia.customizer.oauth2.clientId.result";


    /**
     * Get client detail
     *
     * @param  clientId OOauth2 client identifier
     * @return client detail
     */
    ClientDetail getClientDetail(String clientId);


  
}

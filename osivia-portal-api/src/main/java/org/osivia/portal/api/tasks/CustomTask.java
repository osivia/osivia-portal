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
package org.osivia.portal.api.tasks;

import java.util.Map;

import org.osivia.portal.api.cms.EcmDocument;

public class CustomTask implements EcmDocument {



    /** The title. */
    private String title;
    
    /** The inner document. */
    private EcmDocument innerDocument;

    /** The properties. */
    private Map<String, String> properties;
    
    /** The publication DTO. */
    private Object publicationDTO;
    





    public CustomTask(String title, EcmDocument innerDocument, Map<String, String> properties) {
        super();
        this.title = title;
        this.innerDocument = innerDocument;
        this.properties = properties;
    }
    

    public CustomTask(String title, EcmDocument innerDocument, Map<String, String> properties, Object publicationDTO) {
        this(title, innerDocument, properties);
        this.publicationDTO = publicationDTO;
    }


    /**
     * Getter for innerDocument.
     * 
     * @return the innerDocument
     */
    public EcmDocument getInnerDocument() {
        return innerDocument;
    }


    /**
     * Setter for innerDocument.
     * 
     * @param innerDocument the innerDocument to set
     */
    public void setInnerDocument(EcmDocument innerDocument) {
        this.innerDocument = innerDocument;
    }


    /**
     * Getter for properties.
     * 
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }


    /**
     * Setter for properties.
     * 
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }


    @Override
    public String getTitle() {
        return title;
    }


    
    /**
     * Getter for publicationDTO.
     * @return the publicationDTO
     */
    public Object getPublicationDTO() {
        return publicationDTO;
    }

    
}

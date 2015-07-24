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
package org.osivia.portal.api.urls;

import java.util.HashMap;
import java.util.Map;



/**
 * @author David Chevrier.
 *
 */
public class ExtendedParameters {
    
    /** Map of additional parameters. */
    private Map<String, String> parameters;
    
    /**
     * Default constructor.
     */
    public ExtendedParameters(){
        this.parameters = new HashMap<String, String>(0);
    }
    
    /**
     * Constructor with map argument.
     * 
     * @param parameters
     */
    public ExtendedParameters(Map<String, String> parameters){
        this.parameters = parameters;
    }
    
    /**
     * Adds new parameter.
     * 
     * @param parameter
     * @param value
     */
    public void addParameter(String parameter, String value){
        this.parameters.put(parameter, value);
    }
    
    /**
     * @param parameter
     * @return value of parameter.
     */
    public String getParameter(String parameter){
        return this.parameters.get(parameter);
    }
    
    /**
     * @return all additional parameters and their values.
     */
    public Map<String, String> getAllParameters(){
        return this.parameters;
    }
    
    /**
     * Set additional parameters.
     * 
     * @param parameters
     */
    public void setAllParameters(Map<String, String> parameters){
        this.parameters = parameters;
    }

}

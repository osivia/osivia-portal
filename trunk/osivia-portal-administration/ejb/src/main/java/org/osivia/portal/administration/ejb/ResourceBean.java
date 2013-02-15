/******************************************************************************
 * $Id$
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.osivia.portal.administration.ejb;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import java.io.ByteArrayOutputStream;

/**
 * @author asmirnov
 *
 */
public class ResourceBean implements Map<String, byte[]> {

	public void clear() {
    }

	public boolean containsKey(Object key) {
	    return true;
    }

	public boolean containsValue(Object value) {
	    return true;
    }

	public Set<java.util.Map.Entry<String, byte[]>> entrySet() {
	    return Collections.emptySet();
    }

	public byte[] get(Object key) {
		/*
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String url = null;
		if(null == key){
			url = null;
		} else if(null != facesContext){
			url = facesContext.getApplication().getViewHandler().getResourceURL(facesContext, key.toString());
			url = facesContext.getExternalContext().encodeResourceURL(url);
		} else {
			url = key.toString();
		}
	    return url;
	    */
		String xmlStr = "";
		//xmlStr = "<?xml version='1.0' encoding = 'UTF-8'?>";
		//xmlStr += "<ROWSET>";
		//xmlStr += "</ROWSET>";
		xmlStr = "azerty";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			System.out.print("--------------------------------------------------avant");
		baos.write(xmlStr.getBytes());
		System.out.print("--------------------------------------------------après");
		}
		catch(Exception e)
		{
			System.out.print(e.toString());
		}
		byte[] content = baos.toByteArray();

 

		return content;
    }

	public boolean isEmpty() {
	    return false;
    }

	public Set<String> keySet() {
	    return Collections.emptySet();
    }

	public byte[] put(String key, byte[] value) {
	    return null;
    }

	public void putAll(Map<? extends String, ? extends byte[]> t) {
    }

	public byte[] remove(Object key) {
	    return null;
    }

	public int size() {
	    return 0;
    }

	public Collection<byte[]> values() {
	    return Collections.emptySet();
    }
	
	

}

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
package org.osivia.portal.api.cms;

/**
 * State of a document
 * @author Loïc Billon
 *
 */
public enum DocumentState {

	/** live (draft) */
	LIVE,
	
	/** published */
	PUBLISHED,
	
	/** show the state depending of his parents state */
	INHERITED;
	
	private DocumentState() {
		
	}

	public static DocumentState parse(String s) {
		if("1".equals(s)) {
			return LIVE;
		}
		else if ("__inherited".equals(s)) {
			return INHERITED;
		}
		else return PUBLISHED;
	}
	
	public DocumentState parse(Boolean b) {
		if(Boolean.TRUE.equals(b)) {
			return LIVE;
		}
		else return PUBLISHED;
	}
	
	@Override
	public String toString() {
		
		if(this == LIVE) {
			return "1";
		}
		else return "0";
	}
}

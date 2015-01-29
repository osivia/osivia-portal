/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
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
 */

package org.osivia.portal.core.cms;


/**
 * The Class BinaryDescription.
 */
public class BinaryDescription {


    /**
     * The Enum Type.
     */
    public static enum Type {
        
        /** The attached picture. */
        ATTACHED_PICTURE, 
 /** The picture. */
 PICTURE, 
 /** The file. */
 FILE, 
 /** The attached file. */
 ATTACHED_FILE, 
 /** The blob. */
 BLOB;
    }

    /** The type. */
    private final Type type;
    
    /** The path. */
    private final String path;
    
    /** The index. */
    private String index;
    
    /** The content. */
    private String content;
    
    /** The field name. */
    private String fieldName;
    
    /** The document. */
    private Object document;
    
    /** The scope. */
    private String scope;


    /**
     * Gets the scope.
     *
     * @return the scope
     */
    public String getScope() {
        return scope;
    }


    /**
     * Sets the scope.
     *
     * @param scope the new scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }


    /**
     * Gets the document.
     *
     * @return the document
     */
    public Object getDocument() {
        return document;
    }


    /**
     * Sets the document.
     *
     * @param document the new document
     */
    public void setDocument(Object document) {
        this.document = document;
    }


    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }


    /**
     * Sets the field name.
     *
     * @param fieldName the new field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    /**
     * Instantiates a new binary description.
     *
     * @param type the type
     * @param path the path
     */
    public BinaryDescription(Type type, String path) {
        super();
        this.type = type;
        this.path = path;
    }


    /**
     * Gets the index.
     *
     * @return the index
     */
    public String getIndex() {
        return index;
    }


    /**
     * Sets the index.
     *
     * @param index the new index
     */
    public void setIndex(String index) {
        this.index = index;
    }


    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }


    /**
     * Sets the content.
     *
     * @param content the new content
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }


    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }


}

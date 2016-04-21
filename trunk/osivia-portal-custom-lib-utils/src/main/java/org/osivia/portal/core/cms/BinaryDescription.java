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

    /** File name. */
    private String fileName;

    /** The document. */
    private Object document;

    /** The scope. */
    private String scope;


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
     * Getter for index.
     *
     * @return the index
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * Setter for index.
     *
     * @param index the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Getter for content.
     *
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Setter for content.
     *
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for fieldName.
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Setter for fieldName.
     *
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Getter for fileName.
     * 
     * @return the fileName
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Setter for fileName.
     * 
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter for document.
     *
     * @return the document
     */
    public Object getDocument() {
        return this.document;
    }

    /**
     * Setter for document.
     *
     * @param document the document to set
     */
    public void setDocument(Object document) {
        this.document = document;
    }

    /**
     * Getter for scope.
     *
     * @return the scope
     */
    public String getScope() {
        return this.scope;
    }

    /**
     * Setter for scope.
     *
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Getter for type.
     *
     * @return the type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

}

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
 */
package org.osivia.portal.api.menubar;

import java.util.HashMap;
import java.util.Map;



/**
 * The Class MenubarItem.
 *
 * Each portlet has a menu bar that can be customized inside the code of the portlet
 *
 */
public class MenubarItem {

    /** The order portlet specific. */
    public static final int ORDER_PORTLET_SPECIFIC = 0;

    // CMS Items : 40 TO 60
    /** The order portlet specific cms. */
    public static final int ORDER_PORTLET_SPECIFIC_CMS = 40;

    // Portal Items >= 100
    /** The order portlet generic. */
    public static final int ORDER_PORTLET_GENERIC = 100;


    /** Item order. */
    private int order;
    /** Item URL. */
    private String url;
    /** Item title. */
    private String title;
    /** Item HTML class name. */
    private String className;
    /** Item glyphicon. */
    private String glyphicon;
    /** Item onclick event. */
    private String onClickEvent;
    /** Item target. */
    private String target;
    /** Item associated HTML. */
    private String associatedHtml;
    /** State item indicator. */
    private boolean stateItem;
    /** Dropdown item indicator. */
    private boolean dropdownItem;
    /** Item AJAX disabled indicator. */
    private boolean ajaxDisabled = false;
    /** First item indicator. */
    private boolean firstItem;

    /** Item identifier. */
    private final String id;
    /** Item data attributes. */
    private final Map<String, String> data;


    /**
     * Constructor.
     *
     * @param id item identifier
     * @param title item title
     * @param order item order
     * @param url item URL
     * @param onClickEvent item onclick event (optional)
     * @param className item HTML class name (optional)
     * @param target item target, for external link (optional)
     */
    public MenubarItem(String id, String title, int order, String url, String onClickEvent, String className, String target) {
        super();
        this.id = id;
        this.order = order;
        this.url = url;
        this.onClickEvent = onClickEvent;
        this.title = title;
        this.className = className;
        this.target = target;
        this.ajaxDisabled = false;
        this.firstItem = false;
        this.data = new HashMap<String, String>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MenubarItem clone()  {
        MenubarItem item = new MenubarItem(this.id, this.title, ORDER_PORTLET_GENERIC, this.url, this.onClickEvent, this.className, this.target);
        item.setStateItem(this.stateItem);
        item.setDropdownItem(this.stateItem);
        item.setGlyphicon(this.glyphicon);
        item.setAjaxDisabled(this.ajaxDisabled);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "MenubarItem [title=" + this.title + "]";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MenubarItem other = (MenubarItem) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for order.
     *
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Setter for order.
     *
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Getter for url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for className.
     *
     * @return the className
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Setter for className.
     *
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Getter for glyphicon.
     *
     * @return the glyphicon
     */
    public String getGlyphicon() {
        return this.glyphicon;
    }

    /**
     * Setter for glyphicon.
     *
     * @param glyphicon the glyphicon to set
     */
    public void setGlyphicon(String glyphicon) {
        this.glyphicon = glyphicon;
    }

    /**
     * Getter for onClickEvent.
     *
     * @return the onClickEvent
     */
    public String getOnClickEvent() {
        return this.onClickEvent;
    }

    /**
     * Setter for onClickEvent.
     *
     * @param onClickEvent the onClickEvent to set
     */
    public void setOnClickEvent(String onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    /**
     * Getter for target.
     *
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Setter for target.
     *
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Getter for associatedHtml.
     *
     * @return the associatedHtml
     */
    public String getAssociatedHtml() {
        return this.associatedHtml;
    }

    /**
     * Setter for associatedHtml.
     *
     * @param associatedHtml the associatedHtml to set
     */
    public void setAssociatedHtml(String associatedHtml) {
        this.associatedHtml = associatedHtml;
    }

    /**
     * Getter for stateItem.
     *
     * @return the stateItem
     */
    public boolean isStateItem() {
        return this.stateItem;
    }

    /**
     * Setter for stateItem.
     *
     * @param stateItem the stateItem to set
     */
    public void setStateItem(boolean stateItem) {
        this.stateItem = stateItem;
    }

    /**
     * Getter for dropdownItem.
     *
     * @return the dropdownItem
     */
    public boolean isDropdownItem() {
        return this.dropdownItem;
    }

    /**
     * Setter for dropdownItem.
     *
     * @param dropdownItem the dropdownItem to set
     */
    public void setDropdownItem(boolean dropdownItem) {
        this.dropdownItem = dropdownItem;
    }

    /**
     * Getter for ajaxDisabled.
     *
     * @return the ajaxDisabled
     */
    public boolean isAjaxDisabled() {
        return this.ajaxDisabled;
    }

    /**
     * Setter for ajaxDisabled.
     *
     * @param ajaxDisabled the ajaxDisabled to set
     */
    public void setAjaxDisabled(boolean ajaxDisabled) {
        this.ajaxDisabled = ajaxDisabled;
    }

    /**
     * Checks if is first item.
     *
     * @return true, if is first item
     */
    public boolean isFirstItem() {
		return this.firstItem;
	}

	/**
	 * Sets the first item.
	 *
	 * @param firstItem the new first item
	 */
	public void setFirstItem(boolean firstItem) {
		this.firstItem = firstItem;
	}

    /**
     * Getter for data.
     * 
     * @return the data
     */
    public Map<String, String> getData() {
        return data;
    }

}

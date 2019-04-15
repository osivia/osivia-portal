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
 * Menubar item.
 *
 * @author Cédric Krommenhoek
 * @see MenubarObject
 */
public class MenubarItem extends MenubarObject implements Cloneable {

    /**
     * Portlet specific order range : 0 to 39.
     *
     * @deprecated use {@link MenubarItem#setGroup(MenubarGroup)}
     */
    @Deprecated
    public static final int ORDER_PORTLET_SPECIFIC = 0;

    /**
     * Portlet CMS order range : 40 to 60.
     *
     * @deprecated use {@link MenubarItem#setGroup(MenubarGroup)}
     */
    @Deprecated
    public static final int ORDER_PORTLET_SPECIFIC_CMS = 40;

    /**
     * Portlet generic order range : from 100.
     *
     * @deprecated use {@link MenubarItem#setGroup(MenubarGroup)}
     */
    @Deprecated
    public static final int ORDER_PORTLET_GENERIC = 100;


    /** Menubar item URL. */
    private String url;
    /** Menubar item target. */
    private String target;
    /** Menubar item onclick event. */
    private String onclick;
    /** Menubar item HTML classes. */
    private String htmlClasses;
    /** Menubar item associated HTML. */
    private String associatedHTML;
    /** Disabled menubar item AJAX indicator. */
    private boolean ajaxDisabled;
    /** State menubar item indicator. */
    private boolean state;
    /** Active menubar item indicator. */
    private boolean active;
    /** Menubar item tooltip content. */
    private String tooltip;
    /** Menubar item add dropdown divider indicator. */
    private boolean divider;
    /** Menubar item counter. */
    private Integer counter;
    /** Menubar item add dropdown divider indicator. */
    private boolean visible = true;
    /** Menubar item parent. */
    private MenubarContainer parent;

    /** Menubar item data attributes. */
    private final Map<String, String> data;


    /**
     * Constructor.
     *
     * @param id menubar item identifier
     * @param title menubar item title
     * @param glyphicon menubar item glyphicon
     * @param parent menubar item parent container
     * @param order menubar item order
     * @param url menubar item URL
     * @param target menubar item target
     * @param onclick menubar item onclick event
     * @param htmlClasses menubar item HTML classes
     */
    public MenubarItem(String id, String title, String glyphicon, MenubarContainer parent, int order, String url, String target, String onclick,
            String htmlClasses) {
        super(id, title, glyphicon, order, false);
        this.url = url;
        this.target = target;
        this.onclick = onclick;
        this.htmlClasses = htmlClasses;
        this.parent = parent;
        this.data = new HashMap<String, String>();
    }


    /**
     * Constructor.
     *
     * @param id menubar item identifier
     * @param title menubar item title
     * @param parent menubar item parent container
     * @param order menubar item order
     * @param htmlClasses menubar item HTML classes
     */
    public MenubarItem(String id, String title, MenubarContainer parent, int order, String htmlClasses) {
        this(id, title, null, parent, order, null, null, null, htmlClasses);
    }


    /**
     * Constructor.
     *
     * @param id menubar dropdown identifier
     * @param title menubar dropdown title
     * @param order menubar dropdown order
     * @param url menubar item URL
     * @param onclick menubar item onclick event
     * @param htmlClasses menubar item HTML classes
     * @param target menubar item target
     *
     * @deprecated
     */
    @Deprecated
    public MenubarItem(String id, String title, int order, String url, String onclick, String htmlClasses, String target) {
        this(id, title, null, getNewGroup(order), getNewOrder(order), url, target, onclick, htmlClasses);
    }


    /**
     * Get menubar group from old order value.
     *
     * @param order old order
     * @return menubar group
     * @deprecated
     */
    @Deprecated
    private static MenubarGroup getNewGroup(int order) {
        MenubarGroup group;

        if (order < ORDER_PORTLET_SPECIFIC_CMS) {
            group = MenubarGroup.SPECIFIC;
        } else if (order < ORDER_PORTLET_GENERIC) {
            group = MenubarGroup.CMS;
        } else {
            group = MenubarGroup.GENERIC;
        }

        return group;
    }


    /**
     * Get menubar order value from old order value.
     *
     * @param oldOrder old order value
     * @return new order value
     * @deprecated
     */
    @Deprecated
    private static int getNewOrder(int oldOrder) {
        int newOrder;

        if (oldOrder < ORDER_PORTLET_SPECIFIC_CMS) {
            newOrder = oldOrder;
        } else if (oldOrder < ORDER_PORTLET_GENERIC) {
            newOrder = oldOrder - ORDER_PORTLET_SPECIFIC_CMS;
        } else {
            newOrder = oldOrder - ORDER_PORTLET_GENERIC;
        }

        return newOrder;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MenubarItem clone() {
        MenubarItem clone = new MenubarItem(this.getId(), this.getTitle(), this.getGlyphicon(), this.parent, this.getOrder(), this.url, this.target,
                this.onclick, this.htmlClasses);
        clone.setCustomizedIcon(this.getCustomizedIcon());
        clone.setDisabled(this.isDisabled());
        clone.setBreadcrumb(this.isBreadcrumb());
        clone.associatedHTML = this.associatedHTML;
        clone.ajaxDisabled = this.ajaxDisabled;
        clone.state = this.state;
        clone.active = this.active;
        clone.tooltip = this.tooltip;
        clone.divider = this.divider;
        clone.counter = this.counter;
        clone.visible = this.visible;
        clone.data.putAll(this.data);
        return clone;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MenubarItem [id=");
        builder.append(this.getId());
        builder.append("]");
        return builder.toString();
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
     * Getter for onclick.
     *
     * @return the onclick
     */
    public String getOnclick() {
        return this.onclick;
    }

    /**
     * Setter for onclick.
     *
     * @param onclick the onclick to set
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /**
     * Getter for htmlClasses.
     *
     * @return the htmlClasses
     */
    public String getHtmlClasses() {
        return this.htmlClasses;
    }

    /**
     * Setter for htmlClasses.
     *
     * @param htmlClasses the htmlClasses to set
     */
    public void setHtmlClasses(String htmlClasses) {
        this.htmlClasses = htmlClasses;
    }

    /**
     * Getter for associatedHTML.
     *
     * @return the associatedHTML
     */
    public String getAssociatedHTML() {
        return this.associatedHTML;
    }

    /**
     * Setter for associatedHTML.
     *
     * @param associatedHTML the associatedHTML to set
     */
    public void setAssociatedHTML(String associatedHTML) {
        this.associatedHTML = associatedHTML;
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
     * Getter for state.
     *
     * @return the state
     */
    public boolean isState() {
        return this.state;
    }

    /**
     * Setter for state.
     *
     * @param state the state to set
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * Getter for active.
     *
     * @return the active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Setter for active.
     *
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for tooltip.
     *
     * @return the tooltip
     */
    public String getTooltip() {
        return this.tooltip;
    }

    /**
     * Setter for tooltip.
     *
     * @param tooltip the tooltip to set
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Getter for divider.
     *
     * @return the divider
     */
    public boolean isDivider() {
        return this.divider;
    }

    /**
     * Setter for divider.
     *
     * @param divider the divider to set
     */
    public void setDivider(boolean divider) {
        this.divider = divider;
    }

    /**
     * Getter for counter.
     *
     * @return the counter
     */
    public Integer getCounter() {
        return this.counter;
    }

    /**
     * Setter for counter.
     *
     * @param counter the counter to set
     */
    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    /**
     * Getter for visible.
     * 
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Setter for visible.
     * 
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Getter for parent.
     *
     * @return the parent
     */
    public MenubarContainer getParent() {
        return this.parent;
    }

    /**
     * Setter for parent.
     * 
     * @param parent the parent to set
     */
    public void setParent(MenubarContainer parent) {
        this.parent = parent;
    }

    /**
     * Getter for data.
     *
     * @return the data
     */
    public Map<String, String> getData() {
        return this.data;
    }

}

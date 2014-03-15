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
package org.osivia.portal.api.menubar;


/**
 * The Class MenubarItem.
 * 
 * Each portlet has a menu bar that can be customized inside the code of the portlet
 * 
 */
public class MenubarItem {
    
    /** The id. */
    private String id;
	
    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /** The order. */
    private int order;
	
	/** The url. */
	private String url;
	
	/** The title. */
	private String title;
	
	/** The class name. */
	private String className;
	
	/** The on click event. */
	private String onClickEvent;
	
	/** The target. */
	private String target;
    
    /** The associated html. */
    private String associatedHtml;	
    
    /** The state item. */
    private boolean stateItem;
    
    /** The dropdown item. */
    private boolean dropdownItem;


 


    
    /**
     * Checks if is state item.
     *
     * @return true, if is state item
     */
    public boolean isStateItem() {
        return stateItem;
    }

    
    /**
     * Sets the state item.
     *
     * @param stateItem the new state item
     */
    public void setStateItem(boolean stateItem) {
        this.stateItem = stateItem;
    }

    
    /**
     * Checks if is dropdown item.
     *
     * @return true, if is dropdown item
     */
    public boolean isDropdownItem() {
        return dropdownItem;
    }

    
    /**
     * Sets the dropdown item.
     *
     * @param dropdownItem the new dropdown item
     */
    public void setDropdownItem(boolean dropdownItem) {
        this.dropdownItem = dropdownItem;
    }

    /** The ajax disabled. */
    private boolean ajaxDisabled=false;

	/** The order portlet specific. */
	public static int ORDER_PORTLET_SPECIFIC = 0;
	
	// CMS Items : 40 TO 60
	/** The order portlet specific cms. */
	public static int ORDER_PORTLET_SPECIFIC_CMS = 40;	
	
	//Portal Items >= 100
	/** The order portlet generic. */
	public static int ORDER_PORTLET_GENERIC = 100;


	/**
	 * Instantiates a new menubar item.
	 *
	 * @param id the id
	 * @param title : titre d'affichage
	 * @param order : ordre relatif de l'item
	 * @param url : url de redirection
	 * @param onClickEvent : code javascript (optionnel)
	 * @param className : classe CSS
	 * @param target : target window (pour les liens externes)
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
	}
	
	/**
	 * Gets the on click event.
	 *
	 * @return the on click event
	 */
	public String getOnClickEvent() {
		return onClickEvent;
	}

	/**
	 * Sets the on click event.
	 *
	 * @param onClickEvent the new on click event
	 */
	public void setOnClickEvent(String onClickEvent) {
		this.onClickEvent = onClickEvent;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Sets the class name.
	 *
	 * @param className the new class name
	 */
	public void setClassName(String className) {
		this.className = className;
	}	
	
	
	/**
	 * Checks if is ajax disabled.
	 *
	 * @return true, if is ajax disabled
	 */
	public boolean isAjaxDisabled() {
		return ajaxDisabled;
	}

	/**
	 * Sets the ajax disabled.
	 *
	 * @param ajaxDisabled the new ajax disabled
	 */
	public void setAjaxDisabled(boolean ajaxDisabled) {
		this.ajaxDisabled = ajaxDisabled;
	}
	
    /**
     * Gets the associated html.
     *
     * @return the associated html
     */
    public String getAssociatedHtml() {
        return associatedHtml;
    }

    /**
     * Sets the associated html.
     *
     * @param associatedHtml the new associated html
     */
    public void setAssociatedHtml(String associatedHtml) {
        this.associatedHtml = associatedHtml;
    }


	
	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(String target) {
		this.target = target;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenubarItem other = (MenubarItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

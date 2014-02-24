package org.osivia.portal.api.menubar;

public class MenubarItem {
    
    private String id;
	
    public String getId() {
        return id;
    }

    private int order;
	private String url;
	private String title;
	private String className;
	private String onClickEvent;
	private String target;
    private String associatedHtml;	
    private boolean stateItem;
    private boolean dropdownItem;


 


    
    public boolean isStateItem() {
        return stateItem;
    }

    
    public void setStateItem(boolean stateItem) {
        this.stateItem = stateItem;
    }

    
    public boolean isDropdownItem() {
        return dropdownItem;
    }

    
    public void setDropdownItem(boolean dropdownItem) {
        this.dropdownItem = dropdownItem;
    }

    private boolean ajaxDisabled=false;

	public static int ORDER_PORTLET_SPECIFIC = 0;
	
	// CMS Items : 40 TO 60
	public static int ORDER_PORTLET_SPECIFIC_CMS = 40;	
	
	//Portal Items >= 100
	public static int ORDER_PORTLET_GENERIC = 100;


	/**
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
	
	public String getOnClickEvent() {
		return onClickEvent;
	}

	public void setOnClickEvent(String onClickEvent) {
		this.onClickEvent = onClickEvent;
	}

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}	
	
	
	public boolean isAjaxDisabled() {
		return ajaxDisabled;
	}

	public void setAjaxDisabled(boolean ajaxDisabled) {
		this.ajaxDisabled = ajaxDisabled;
	}
	
    public String getAssociatedHtml() {
        return associatedHtml;
    }

    public void setAssociatedHtml(String associatedHtml) {
        this.associatedHtml = associatedHtml;
    }


	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


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

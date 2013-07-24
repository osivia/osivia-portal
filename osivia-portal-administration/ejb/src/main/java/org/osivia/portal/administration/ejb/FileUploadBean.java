/**
 *
 */
package org.osivia.portal.administration.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;


@Name("fileUploadBean")
@Scope(ScopeType.SESSION)
public class FileUploadBean {

	private static final String PORTAL_STYLES_DECLARED_PROPERTY = "osivia.liste_styles";
    private int uploadsAvailable = 5;
	private boolean autoUpload = false;
	private boolean useFlash = false;

	@Create
	public void setAdminPrivileges ()	{
		Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();

		if( request instanceof PortletRequest){
			String isAdmin = (String) ((PortletRequest) request).getAttribute("osivia.isAdministrator");
			if( "true".equals(isAdmin))	{
				PortletSession session = ((PortletRequest) request).getPortletSession();
				session.setAttribute("isAdmin", "true", PortletSession.APPLICATION_SCOPE);
			}
		}
	}

	/* Permet de controler les droits d'admin pour les servlets */

	public static boolean checkAdminPrivileges (HttpServletRequest request)	{

		String isAdmin = (String) request.getSession().getAttribute("isAdmin");

		if( "true".equals(isAdmin)) {
            return true;
        } else {
            return false;
        }

	}


	String action = "";

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	String titrePopup = "";

	public String getTitrePopup() {
		return this.titrePopup;
	}

	public void setTitrePopup(String titrePopup) {
		this.titrePopup = titrePopup;
	}

	private String profilNom ="";
	public String getProfilNom() {
		return this.profilNom;
	}

	public void setProfilNom(String profilNom) {
		this.profilNom = profilNom;
	}

	public String getProfilRole() {
		return this.profilRole;
	}

	public void setProfilRole(String profilRole) {
		this.profilRole = profilRole;
	}

	public String getProfilUrl() {
		return this.profilUrl;
	}

	public void setProfilUrl(String profilUrl) {
		this.profilUrl = profilUrl;
	}

	public String getProfilNuxeoVirtualUser() {
		return this.profilNuxeoVirtualUser;
	}

	public void setProfilNuxeoVirtualUser(String profilNuxeoVirtualUser) {
		this.profilNuxeoVirtualUser = profilNuxeoVirtualUser;
	}


	private String profilRole ="";
	private String profilUrl ="";
	private String profilNuxeoVirtualUser = "";


	PortalObjectContainer portalObjectContainer;


	private final List<SelectItem> portails = new ArrayList<SelectItem>();

	public List<SelectItem> getPortails() {
		this.portails.clear();
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		this.portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		Collection<PortalObject> portals = this.portalObjectContainer.getContext().getChildren();
		for (PortalObject po : portals) {
			this.portails.add(new SelectItem(po.getName()));
		}

		// this.setPortail("default");

		return this.portails;
	}

	private String portail = "";

	public String getPortail() {
		return this.portail;
	}

	public void setPortail(String portail) {
		this.portail = portail;
	}

	private String layout = "";

	public String getLayout() {
		return this.layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	private List<SelectItem> layouts = new ArrayList<SelectItem>();

	public List<SelectItem> getLayouts() {
		return this.layouts;
	}

	public void setLayouts(List<SelectItem> layouts) {
		this.layouts = layouts;
	}

	private String theme = "";

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	private String style = "";

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	private List<SelectItem> themes = new ArrayList<SelectItem>();

	public List<SelectItem> getThemes() {
		return this.themes;
	}

	public void setThemes(List<SelectItem> themes) {
		this.themes = themes;
	}

	private String message = "";

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FileUploadBean() {
	}

	public void updateTheme() {
		System.out.println("updateThemes");
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());
		portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, this.theme);
		System.out.println("updateThemes : " + portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME));
		this.setMessage("Thème enregistré avec succès.");

		/*
		 * PortalObjectId poid = PortalObjectId.parse(windowId,
		 * PortalObjectPath.SAFEST_FORMAT); PortalObject window =
		 * getControllerContext
		 * ().getController().getPortalObjectContainer().getObject(poid);
		 * PortalObject page = window.getParent();
		 */
	}

	public void updateLayout() {
		System.out.println("updateLayout");
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());
		portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, this.layout);
		this.setMessage("Layout enregistré avec succès.");
	}

	public void updateStyle() {
		System.out.println("updateStyle");
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());
		portal.setDeclaredProperty(PORTAL_STYLES_DECLARED_PROPERTY, this.style);
		this.setMessage("Style enregistré avec succès.");

		/*
		 * PortalObjectId poid = PortalObjectId.parse(windowId,
		 * PortalObjectPath.SAFEST_FORMAT);
		 * PortalObject window = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		 * PortalObject page = window.getParent();
		 */
	}


	public void portailChanged(ValueChangeEvent event) {
		HtmlSelectOneListbox element = (HtmlSelectOneListbox) event.getComponent();
		this.setPortail((String) element.getValue());
		String portail = this.getPortail();


		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		/*----- Styles -----*/
		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());

		String propriete = portal.getDeclaredProperty(PORTAL_STYLES_DECLARED_PROPERTY);
		this.style = propriete;


		/*----- Themes -----*/
		portal = portalObjectContainer.getContext().getPortal(portail);

		String selTheme = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME);
		this.setTheme(selTheme);
		/*
		 * Enumeration<String> toto = ctx.getAttributeNames(); String o = null;
		 * while(toto.hasMoreElements()) { o=(String)toto.nextElement();
		 * System.out.println("Attr : "+o); o=null; }
		 */

		ThemeService themeService = (ThemeService) ctx.getAttribute("ThemeService");
		this.themes.clear();

		if (themeService != null) {
			Collection mesThemes = themeService.getThemes();

			for (Iterator i = mesThemes.iterator(); i.hasNext();) {
				PortalTheme monTheme = (PortalTheme) i.next();
				this.themes.add(new SelectItem(monTheme.getThemeInfo().getName()));
			}
			this.themes.add(new SelectItem("default"));
		} else {
			this.themes.add(new SelectItem("Pb avec le service de thème"));
		}

		System.out.println("--------------------");

		/*----- Layout -----*/
		String selLayout = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
		this.setLayout(selLayout);

		LayoutService layoutService = (LayoutService) ctx.getAttribute("LayoutService");
		this.layouts.clear();

		if (themeService != null) {
			Collection mesLayouts = layoutService.getLayouts();

			for (Iterator i = mesLayouts.iterator(); i.hasNext();) {
				PortalLayout monLayout = (PortalLayout) i.next();
				this.layouts.add(new SelectItem(monLayout.getLayoutInfo().getName()));
				//System.out.println("Layout : " + monLayout.getLayoutInfo().getName());
			}
			this.layouts.add(new SelectItem("default"));
		} else {
			this.layouts.add(new SelectItem("Pb avec le service de layouts"));
		}

		/*----- Profils -----*/
		this.findProfils();


	}


	public long getTimeStamp() {
		return System.currentTimeMillis();
	}


	public int getUploadsAvailable() {
		return this.uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return this.autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return this.useFlash;
	}

	public void setUseFlash(boolean useFlash) {
		this.useFlash = useFlash;
	}

	private DataModel dataModel = new ListDataModel();

	public DataModel getDataModel() {
		return this.dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public void findProfils() {

		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");
		Portal portal = portalObjectContainer.getContext().getPortal(this.getPortail());

		IProfilManager profilManager = (IProfilManager)ctx.getAttribute("PiaProfilManager");
		List<ProfilBean> profils = profilManager.getListeProfils(portal);

		//dataModel.setWrappedData(listeProfils);

	}

	public void saveProfils() throws Exception {

		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");
		Portal portal = portalObjectContainer.getContext().getPortal(this.getPortail());

		IProfilManager profilManager = (IProfilManager)ctx.getAttribute("PiaProfilManager");
		List<ProfilBean> profils = new ArrayList<ProfilBean>();


		profilManager.setListeProfils(portal, profils);
	}


	private List<String> listeTest = new ArrayList<String>();

	public List<String> getlisteTest() {
		return this.listeTest;
	}

	public void setListelisteTest(List<String> listeTest) {
		this.listeTest = listeTest;
	}

}
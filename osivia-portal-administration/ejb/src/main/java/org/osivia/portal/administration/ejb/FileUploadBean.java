/**
 * 
 */
package org.osivia.portal.administration.ejb;

import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.core.deploiement.IParametresPortailDeploymentManager;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ActionEvent;
import javax.management.MBeanServer;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/*
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
*/
import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import java.util.Collections;
import java.util.Set;


@Name("fileUploadBean")
@Scope(ScopeType.SESSION)
public class FileUploadBean {

	private ArrayList<PiaFile> piaFiles = new ArrayList<PiaFile>();
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
		
		if( "true".equals(isAdmin))
			return true;
		else return false;
		
	}

	public int getSize() {
		if (getPiaFiles().size() > 0) {
			return getPiaFiles().size();
		} else {
			return 0;
		}
	}

	String action = "";
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	String titrePopup = "";

	public String getTitrePopup() {
		return titrePopup;
	}

	public void setTitrePopup(String titrePopup) {
		this.titrePopup = titrePopup;
	}
	
	private String profilNom ="";
	public String getProfilNom() {
		return profilNom;
	}

	public void setProfilNom(String profilNom) {
		this.profilNom = profilNom;
	}

	public String getProfilRole() {
		return profilRole;
	}

	public void setProfilRole(String profilRole) {
		this.profilRole = profilRole;
	}

	public String getProfilUrl() {
		return profilUrl;
	}

	public void setProfilUrl(String profilUrl) {
		this.profilUrl = profilUrl;
	}
	
	public String getProfilNuxeoVirtualUser() {
		return profilNuxeoVirtualUser;
	}

	public void setProfilNuxeoVirtualUser(String profilNuxeoVirtualUser) {
		this.profilNuxeoVirtualUser = profilNuxeoVirtualUser;
	}
	

	private String profilRole ="";
	private String profilUrl ="";
	private String profilNuxeoVirtualUser = "";
	

	PortalObjectContainer portalObjectContainer;


	private List<SelectItem> portails = new ArrayList<SelectItem>();

	public List<SelectItem> getPortails() {
		portails.clear();
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		Collection<PortalObject> portals = portalObjectContainer.getContext().getChildren();
		for (PortalObject po : portals) {
			portails.add(new SelectItem(po.getName()));
		}

		// this.setPortail("default");

		return portails;
	}

	private String portail = "";

	public String getPortail() {
		return portail;
	}

	public void setPortail(String portail) {
		this.portail = portail;
	}

	private String layout = "";

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	private List<SelectItem> layouts = new ArrayList<SelectItem>();

	public List<SelectItem> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<SelectItem> layouts) {
		this.layouts = layouts;
	}

	private String theme = "";

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	private String style = "";

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	private List<SelectItem> themes = new ArrayList<SelectItem>();

	public List<SelectItem> getThemes() {
		return themes;
	}

	public void setThemes(List<SelectItem> themes) {
		this.themes = themes;
	}

	private String message = "";

	public String getMessage() {
		return message;
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
		portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, theme);
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
		portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, layout);
		this.setMessage("Layout enregistré avec succès.");
	}

	public void updateStyle() {
		System.out.println("updateStyle");
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());
		portal.setDeclaredProperty("osivia.liste_styles", style);
		this.setMessage("Style enregistré avec succès.");

		/*
		 * PortalObjectId poid = PortalObjectId.parse(windowId,
		 * PortalObjectPath.SAFEST_FORMAT); 
		 * PortalObject window = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		 * PortalObject page = window.getParent();
		 */
	}
	

	@In(required=false, value="treeBean")
	private PortalPagesBean treePages;

	public void portailChanged(ValueChangeEvent event) {
		HtmlSelectOneListbox element = (HtmlSelectOneListbox) event.getComponent();
		this.setPortail((String) element.getValue());
		String portail = this.getPortail();


		/* Stockage en session pour l'export */
		PortletRequest req = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		req.getPortletSession().setAttribute(ExportServlet.EXPORT_PORTALNAME_SESSION, portail,
				PortletSession.APPLICATION_SCOPE);

		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");

		/*----- Styles -----*/
		PortalObject portal = portalObjectContainer.getContext().getPortal(this.getPortail());
		
		String propriete = portal.getDeclaredProperty("osivia.liste_styles");
		style = propriete;


		/*----- Themes -----*/
		portal = portalObjectContainer.getContext().getPortal(portail);

		String selTheme = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME);
		setTheme(selTheme);
		/*
		 * Enumeration<String> toto = ctx.getAttributeNames(); String o = null;
		 * while(toto.hasMoreElements()) { o=(String)toto.nextElement();
		 * System.out.println("Attr : "+o); o=null; }
		 */

		ThemeService themeService = (ThemeService) ctx.getAttribute("ThemeService");
		themes.clear();

		if (themeService != null) {
			Collection mesThemes = themeService.getThemes();

			for (Iterator i = mesThemes.iterator(); i.hasNext();) {
				PortalTheme monTheme = (PortalTheme) i.next();
				themes.add(new SelectItem(monTheme.getThemeInfo().getName()));
			}
			themes.add(new SelectItem("default"));
		} else {
			themes.add(new SelectItem("Pb avec le service de thème"));
		}

		System.out.println("--------------------");

		/*----- Layout -----*/
		String selLayout = portal.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
		setLayout(selLayout);

		LayoutService layoutService = (LayoutService) ctx.getAttribute("LayoutService");
		layouts.clear();

		if (themeService != null) {
			Collection mesLayouts = layoutService.getLayouts();

			for (Iterator i = mesLayouts.iterator(); i.hasNext();) {
				PortalLayout monLayout = (PortalLayout) i.next();
				layouts.add(new SelectItem(monLayout.getLayoutInfo().getName()));
				//System.out.println("Layout : " + monLayout.getLayoutInfo().getName());
			}
			layouts.add(new SelectItem("default"));
		} else {
			layouts.add(new SelectItem("Pb avec le service de layouts"));
		}
		
		/*----- Profils -----*/
		findProfils();

		if( treePages != null)
			treePages.initPages();
	}

	public Image getUImage() {
		if (getSize() > 0) {
			byte[] bytes = piaFiles.get(0).getData();

			byte[] rgb = new byte[256];
			for (int i = 0; i < rgb.length; i++)
				rgb[i] = (byte) i;

			IndexColorModel cm = new IndexColorModel(8, 256, rgb, rgb, rgb);
			MemoryImageSource source = new MemoryImageSource(640, 480, cm, bytes, 0, 480);
			Image newImage = Toolkit.getDefaultToolkit().createImage(source);

			// need workaround for different OS
			// apple.awt.OSXImage newImage =
			// (apple.awt.OSXImage)Toolkit.getDefaultToolkit().createImage(source);
			return newImage;// .getBufferedImage();
		} else {
			return null;
		}
	}

	public void paint(OutputStream stream, Object object) throws IOException {
		Object responseObject = FacesContext.getCurrentInstance().getExternalContext().getResponse();
		if (responseObject instanceof RenderResponse) {
			// BufferedRenderResponseWrapper brrw =
			// (BufferedRenderResponseWrapper)responseObject;
			// RenderResponse response = brrw.getResponse();
			// response.getPortletOutputStream().write(getFiles().get((Integer)object).getData());
		} else {
			// servlet response
			stream.write(getPiaFiles().get((Integer) object).getData());
		}
	}

	

	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	public ArrayList<PiaFile> getPiaFiles() {
		return piaFiles;
	}

	public void setFiles(ArrayList<PiaFile> files) {
		this.piaFiles = files;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return useFlash;
	}

	public void setUseFlash(boolean useFlash) {
		this.useFlash = useFlash;
	}

	private DataModel dataModel = new ListDataModel();

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	private List<Profil> listeProfils = new ArrayList<Profil>();

	public List<Profil> getListeProfils() {
		return listeProfils;
	}

	public void setListeProfils(List<Profil> listeProfils) {
		this.listeProfils = listeProfils;
	}

	public void findProfils() {
		
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");
		Portal portal = (Portal) portalObjectContainer.getContext().getPortal(this.getPortail());
		
		IProfilManager profilManager = (IProfilManager)ctx.getAttribute("PiaProfilManager");
		List<ProfilBean> profils = profilManager.getListeProfils(portal);
		
		listeProfils.clear();
		
		for( ProfilBean profil : profils)	{
			Profil p = new Profil(); p.setNom(profil.getName()); p.setRole(profil.getRoleName()); p.setUrl(profil.getDefaultPageName());p.setNuxeoVirtualUser(profil.getNuxeoVirtualUser());
			listeProfils.add(p);
		}
		//dataModel.setWrappedData(listeProfils);
		
	}
	
	public void saveProfils() throws Exception {
		
		PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		
		PortalObjectContainer portalObjectContainer = (PortalObjectContainer) ctx.getAttribute("PortalObjectContainer");
		Portal portal = (Portal) portalObjectContainer.getContext().getPortal(this.getPortail());
		
		IProfilManager profilManager = (IProfilManager)ctx.getAttribute("PiaProfilManager");
		List<ProfilBean> profils = new ArrayList<ProfilBean>();
		
		System.out.println("saveProfils");
		Profil p = null;
		List<Profil> profilsToSave = getListeProfils();
		Iterator i=profilsToSave.iterator(); // on crée un Iterator pour parcourir notre HashSet
		while(i.hasNext()) // tant qu'on a un suivant
		{
			p = (Profil)i.next();
			System.out.println(p.getNom()+":"+p.getRole()+":"+p.getUrl()); // on affiche le suivant
			profils.add( new ProfilBean(p.getNom(),p.getRole(),p.getUrl(), p.getNuxeoVirtualUser()));
		}
		
		profilManager.setListeProfils(portal, profils);
	}	
	
	//@In(required=false)
	Set<Profil> profilSelectionne = null;
	
	public Set<Profil> getProfilSelectionne() {
		return profilSelectionne;
	}
	public void setProfilSelectionne(Set<Profil> profilSelectionne) {
		this.profilSelectionne = profilSelectionne;
	}
	
	public String getSelectionString() 
	{
          StringBuffer buff = new StringBuffer();
          for (Iterator<Profil> it = profilSelectionne.iterator(); it.hasNext();) 
          {
        	  Profil item = it.next();
                  buff.append(item.getNom());
                  if (it.hasNext()) {
                          buff.append(',');
                  }
          }
          return buff.toString();
	}

	
	public void detailProfil() 
	{
		System.out.println("detailProfil");
		System.out.println(getProfilSelectionne());
		this.action = "EDIT";
		titrePopup = "Modifier le profil ";
		Iterator<Profil> it = profilSelectionne.iterator(); 
        Profil item = it.next();
		titrePopup += item.getNom();
		//profilSelectionne = (Profil) dataModel.getRowData();
		/*
		appliSelectionneProfils.clear();
		for(String s: appliSelectionne.getProfils())
		{appliSelectionneProfils.add(profilRef.findProfilByDn(s));}
		appliSelectionneRoles.clear();
		for(String s: appliSelectionne.getRolesApplicatifs())
		{appliSelectionneRoles.add(roleRef.findRoleByDn(s));}
		*/
		//System.out.println("detailProfil : "+profilSelectionne.getNom()+"/"+profilSelectionne.getRole()+"/"+profilSelectionne.getUrl());listeTest.add("azerty");
	}
	
	public void addProfil() 
	{
		System.out.println("addProfil");
		this.action = "ADD";
		this.profilSelectionne = null;
		
		titrePopup = "Ajouter le profil";
		setProfilNom("");
		setProfilRole("");
		setProfilUrl("");
		setProfilNuxeoVirtualUser("");
		
		//profilSelectionne = (Profil) dataModel.getRowData();
		/*
		appliSelectionneProfils.clear();
		for(String s: appliSelectionne.getProfils())
		{appliSelectionneProfils.add(profilRef.findProfilByDn(s));}
		appliSelectionneRoles.clear();
		for(String s: appliSelectionne.getRolesApplicatifs())
		{appliSelectionneRoles.add(roleRef.findRoleByDn(s));}
		*/
		//System.out.println("detailProfil : "+profilSelectionne.getNom()+"/"+profilSelectionne.getRole()+"/"+profilSelectionne.getUrl());listeTest.add("azerty");
	}
	
	public void updateProfil() {
		System.out.println("updateProfil");
		
		Profil p = null;
		Set<Profil> protest = getProfilSelectionne();
		Iterator i=protest.iterator(); // on crée un Iterator pour parcourir notre HashSet
		while(i.hasNext()) // tant qu'on a un suivant
		{
			p = (Profil)i.next();
			System.out.println(p.getNom()+":"+p.getRole()+":"+p.getUrl()); // on affiche le suivant
		}
		
		// On se positionne sur l'élément courant
		int pos = -1;
		int iProfil =0;
		for (Profil iterP:listeProfils)	{
			if( p.getNom().equals(iterP.getNom()))
				pos=iProfil;
			iProfil++;
		}
		
		
		listeProfils.remove(p);
		
		// On remplace l'élément
		if( pos != -1)
			listeProfils.add(pos, p);
		else
			listeProfils.add(p);
	}
	
	
	public void storeProfil() {
		System.out.println("storeProfil");
		
		Profil p = new Profil();
		p.setNom(this.getProfilNom());
		p.setRole(this.getProfilRole());
		p.setUrl(this.getProfilUrl());
		p.setNuxeoVirtualUser(this.getProfilNuxeoVirtualUser());
		
		listeProfils.add(p);
	}
	
	public void deleteProfil() {
		System.out.println("deleteProfil");
		
		Profil p = null;
		Set<Profil> protest = getProfilSelectionne();
		Iterator i=protest.iterator(); // on crée un Iterator pour parcourir notre HashSet
		while(i.hasNext()) // tant qu'on a un suivant
		{
			p = (Profil)i.next();
			System.out.println(p.getNom()+":"+p.getRole()+":"+p.getUrl()); // on affiche le suivant
		}
		
		listeProfils.remove(p);
	}

	Set<Profil> selection = null;

	public Set<Profil> getSelection() {
		return selection;
	}

	public void setSelection(Set<Profil> selection) {
		this.selection = selection;
	}
	
	private List<String> listeTest = new ArrayList<String>();

	public List<String> getlisteTest() {
		return listeTest;
	}

	public void setListelisteTest(List<String> listeTest) {
		this.listeTest = listeTest;
	}

}
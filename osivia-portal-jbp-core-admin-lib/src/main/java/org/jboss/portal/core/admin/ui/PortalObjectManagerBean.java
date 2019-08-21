

package org.jboss.portal.core.admin.ui;

import org.jboss.portal.api.content.SelectedContent;
import org.jboss.portal.core.CoreConstants;
import org.jboss.portal.core.admin.ui.actions.AddPageAction;
import org.jboss.portal.core.admin.ui.actions.PropertyAction;
import org.jboss.portal.core.admin.ui.common.PageManagerBean;
import org.jboss.portal.core.admin.ui.portlet.PortletDefinitionInvoker;
import org.jboss.portal.core.controller.coordination.CoordinationConfigurator;
import org.jboss.portal.core.event.PortalEventListenerRegistry;
import org.jboss.portal.core.impl.model.content.ContentProviderRegistryService;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.portlet.ContentPortlet;
import org.jboss.portal.core.model.instance.Instance;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.faces.component.portlet.PortletEventEvent;
import org.jboss.portal.faces.gui.JSFBeanContext;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvoker;
import org.jboss.portal.portlet.info.NavigationInfo;
import org.jboss.portal.portlet.info.ParameterInfo;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse.Event;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.core.page.PageProperties;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;



/**
 * 
 * OSIVIA : Mise en place du multi-portails
 * @author jeanseb
 *
 */
public class PortalObjectManagerBean implements Serializable, AddPageAction.Listener
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -8923517554726982622L;

   private static final QName CONTENT_SELECT = new QName("urn:jboss:portal:content", "select");

   // Configuration

   /** . */
   private String namespace = "";

   // Wired services

   /** . */
   private PortalObjectContainer portalObjectContainer;

   /** . */
   private CoordinationConfigurator coordinationService;

   /** . */
   private InstanceContainer instanceContainer;

   /** . */
   private LayoutService layoutService;

   /** . */
   private ThemeService themeService;

   /** . */
   private DomainConfigurator domainConfigurator;

   /** . */
   private RoleModule roleModule;

   /** Listener registry to bind even listener to PortalNodes. */
   private PortalEventListenerRegistry listenerRegistry;

   // Navigational state

   /** The selected id. */
   private PortalObjectId selectedId;

   /** The current tab name. */
   private String selectedPlugin;

   /** The selected type for content. */
   private ContentType selectedContentType = ContentType.PORTLET;

   /** The uri value for content. */
   private String selectedContentURI;

   /** . */
   private Map selectedContentParameters = new HashMap();

   /** . */
   private Map<String, String[]> renderParameters;

   // Runtime state

   /** . */
 
   private final AuthorizationBean auth = new PortalObjectAuthorizationBean();

   /** . */
   private SelectItem[] instanceItems;

   /** . */
   private SelectItem[] portalPageItems;

   /** . */
   private List selectedObjectPath;

   /** . */
   private PortalObject selectedObject;

   /** . */
   private PortalObject selectedDeletingObject;

   /** . */
   private PropertiesBean selectedProperties;

   /** . */
   PropertyAction propertyAction;

   /** . */
   private ControlPropertiesBean controlProperties;

   /** . */
   private PortletInvoker portletDefinitionInvoker;

   /** . */
   private ThemeBean theme;

   /** . */
   public PageManagerBean pageManager;

   /** . */
   private Boolean maximizedStateExists;

   // Wired services

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public List getAvailableContentTypes()
   {
      LinkedList<SelectItem> types = new LinkedList<SelectItem>();

      //
      for (Object o : ContentProviderRegistryService.getInstance().getContentTypes())
      {
         ContentType contentType = (ContentType)o;
         SelectItem item = new SelectItem();
         item.setValue(contentType);
         item.setLabel(contentType.toString());
         if (contentType.equals(ContentType.PORTLET))
         {
            types.addFirst(item);
         }
         else
         {
            types.addLast(item);
         }
      }
      return types;
   }

   public String getSelectedContentEditorInstance()
   {
      if (selectedContentType != null)
      {
         ContentProvider contentProvider = ContentProviderRegistryService.getInstance().getContentProvider(selectedContentType);
         // the content provider exists (i.e. the associated portlet is deployed see: JBPORTAL-1656)
         if (contentProvider != null)
         {
            return contentProvider.getPortletInfo().getPortletName(ContentPortlet.EDIT_CONTENT_MODE);
         }
      }
      return null;
   }

   public PortalEventListenerRegistry getListenerRegistry()
   {
      return listenerRegistry;
   }

   public void setListenerRegistry(PortalEventListenerRegistry listenerRegistry)
   {
      this.listenerRegistry = listenerRegistry;
   }

   public RoleModule getRoleModule()
   {
      return roleModule;
   }

   public void setRoleModule(RoleModule roleModule)
   {
      this.roleModule = roleModule;
   }

   public PortalObjectContainer getPortalObjectContainer()
   {
      return portalObjectContainer;
   }

   public void setPortalObjectContainer(PortalObjectContainer poc)
   {
      this.portalObjectContainer = poc;
   }

   public CoordinationConfigurator getCoordinationService()
   {
      return coordinationService;
   }

   public void setCoordinationService(CoordinationConfigurator coordinationService)
   {
      this.coordinationService = coordinationService;
   }

   public InstanceContainer getInstanceContainer()
   {
      return instanceContainer;
   }

   public void setInstanceContainer(InstanceContainer instanceContainer)
   {
      this.instanceContainer = instanceContainer;
      this.portletDefinitionInvoker = new PortletDefinitionInvoker(instanceContainer);
   }

   public LayoutService getLayoutService()
   {
      return layoutService;
   }

   public void setLayoutService(LayoutService layoutService)
   {
      this.layoutService = layoutService;
   }

   public ThemeService getThemeService()
   {
      return themeService;
   }

   public void setThemeService(ThemeService themeService)
   {
      this.themeService = themeService;
   }

   public ContentType getSelectedContentType()
   {
      return selectedContentType;
   }

   public void setSelectedContentType(ContentType selectedContentType)
   {
      this.selectedContentType = selectedContentType;
   }

   public String getSelectedContentURI()
   {
      return selectedContentURI;
   }

   public void setSelectedContentURI(String selectedContentURI)
   {
      this.selectedContentURI = selectedContentURI;
   }

   public String getSelectedPlugin()
   {
      return selectedPlugin;
   }

   public void setSelectedPlugin(String selectedPlugin)
   {
      this.selectedPlugin = selectedPlugin;
   }

   public Map getRenderParameters()
   {
      return renderParameters;
   }

   public void setRenderParameters(Map renderParameters)
   {
      this.renderParameters = renderParameters;
   }

   public Map getSelectedContentParameters()
   {
      return selectedContentParameters;
   }

   public void setSelectedContentParameters(Map selectedContentParameters)
   {
      this.selectedContentParameters = selectedContentParameters;
   }

   public DomainConfigurator getDomainConfigurator()
   {
      return domainConfigurator;
   }

   public void setDomainConfigurator(DomainConfigurator domainConfigurator)
   {
      this.domainConfigurator = domainConfigurator;
   }

   // Runtime state

   public PortletInvoker getPortletDefinitionInvoker()
   {
      return portletDefinitionInvoker;
   }

   public AuthorizationBean getAuth()
   {
      return auth;
   }

   public SelectItem[] getInstanceItems()
   {
      return instanceItems;
   }

   public SelectItem[] getPortalPageItems()
   {
      return portalPageItems;
   }

   public List getSelectedObjectPath()
   {
      return selectedObjectPath;
   }

   public PortalObject getSelectedObject()
   {
      return selectedObject;
   }

   public PortalObject getSelectedDeletingObject()
   {
      return selectedDeletingObject;
   }

   public PropertiesBean getSelectedProperties()
   {
      return selectedProperties;
   }

   public ThemeBean getTheme()
   {
      return theme;
   }

   public Boolean getMaximizedStateExists()
   {
      return maximizedStateExists;
   }

   public void setMaximizedStateExists(Boolean maximizedStateExists)
   {
      this.maximizedStateExists = maximizedStateExists;
   }

   // UI operations

   public void selectObject(PortalObject po)
   {
      if (po != null)
      {
         selectObject(po.getId());
      }
      else
      {
         selectObject((PortalObjectId)null);
      }
   }

   public void selectObject(PortalObjectId id)
   {
      if (id == null)
      {
         selectedId = null;
         selectedPlugin = null;
         selectedContentType = ContentType.PORTLET;
         selectedContentURI = null;
      }
      else
      {
         selectedId = id;
         selectedPlugin = "manager";
         selectedContentType = ContentType.PORTLET;
         selectedContentURI = null;
      }
   }

   public void selectObject(ActionEvent ae)
   {
      selectObject();
   }

   public void selectDeletingObject(ActionEvent ae)
   {
      PortalObjectId poid = getSelectedPortalObjectId();

      selectedDeletingObject = portalObjectContainer.getObject(poid);
      selectObject();

   }

   public String makeObjectDefault()
   {
      PortalObjectId poid = getSelectedPortalObjectId();
      PortalObject object = portalObjectContainer.getObject(poid);
      if (object != null)
      {
         String typeName;
         int type = object.getType();
         if (type == PortalObject.TYPE_PORTAL || type == PortalObject.TYPE_CONTEXT)
         {
            typeName = "portal";
         }
         else if (type == PortalObject.TYPE_PAGE)
         {
            typeName = "page";
         }
         else
         {
            throw new IllegalArgumentException("Invalid object type to set as default");
         }

         PortalObject parent = object.getParent();
         if (parent != null)
         {
            String name = object.getName();
            parent.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, name);


            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
               "'" + name + "' " + typeName + " was successfully set as default " + typeName);
            FacesContext.getCurrentInstance().addMessage("status", message);
         }
      }

      return null;
   }

   public String selectObject()
   {
      try
      {
         PortalObjectId poid = getSelectedPortalObjectId();

         Map<String, String> pmap = getRequestParameterMap();
         maximizedStateExists = Boolean.valueOf(pmap.get("maximizedStateExists"));

         PortalObject object = portalObjectContainer.getObject(poid);

         // Update state if possible
         if (object != null)
         {
            selectObject(poid);
            selectedObject = object;

            //
            switch (object.getType())
            {
               case PortalObject.TYPE_CONTEXT:
                  break;
               case PortalObject.TYPE_PORTAL:
                  break;
               case PortalObject.TYPE_PAGE:
                  break;
               case PortalObject.TYPE_WINDOW:
                  Window window = (Window)object;
                  selectedContentType = window.getContentType();
                  Content content = window.getContent();
                  if (content != null)
                  {
                     renderParameters = new HashMap<String, String[]>();

                     Portlet portlet = instanceContainer.getDefinition(getSelectedContentEditorInstance()).getPortlet();

                     NavigationInfo navigationInfo = portlet.getInfo().getNavigation();

                     ParameterInfo parameterInfo = navigationInfo.getPublicParameter(CoreConstants.JBOSS_PORTAL_CONTENT_URI);
                     if (parameterInfo != null)
                     {
                        renderParameters.put(parameterInfo.getId(), new String[]{content.getURI()});
                     }
                  }
                  break;
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      //
      return "objects";
   }

   public PortalObjectId getSelectedPortalObjectId()
   {
      // Get id
      Map<String, String> pmap = getRequestParameterMap();
      String id = pmap.get("id");

      // set the state from the id
      PortalObjectId portalObjectId = null;
      if (id != null)
      {
         portalObjectId = PortalObjectId.parse(id, PortalObjectPath.LEGACY_BASE64_FORMAT);
      }
      selectObject(portalObjectId);

      return selectedId;
   }

   public void selectPlugin()
   {
      // Get plugin
      Map<String, String> pmap = getRequestParameterMap();
      selectedPlugin = pmap.get("plugin");
   }

   public Map<String, String> getRequestParameterMap()
   {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
   }

   public void selectParentObject(ActionEvent actionEvent)
   {
      PortalObject parent = getSelectedObjectParent();
      selectObject(parent);
   }

   public PortalObject getSelectedObjectParent()
   {
      PortalObject current = getSelectedObject();
      PortalObject parent = current.getParent();
      return parent == null ? current : parent; // if parent is null, return the current object
   }

   public void selectRootObject(ActionEvent ae)
   {
      PortalObject root = portalObjectContainer.getContext(namespace);
      selectObject(root);
   }

   public void udpateContentType()
   {
      // Do nothing
   }

   public void destroyObject(ActionEvent ae)
   {
      try
      {
         selectObject((PortalObjectId)null);

         // Get id
         String id = (String)ae.getComponent().getAttributes().get("objectId");

         // Destroy the object
         if (id != null)
         {
            PortalObjectId poid = PortalObjectId.parse(id, PortalObjectPath.LEGACY_BASE64_FORMAT);
            PortalObject object = portalObjectContainer.getObject(poid);

            if (object != null)
            {
               selectObject(object.getParent());

               String stringMessage = object.getName() + " has successfully been destroyed";

               //
               object.getParent().destroyChild(object.getName());

               FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, stringMessage, stringMessage);
               FacesContext.getCurrentInstance().addMessage("status", message);

               selectedDeletingObject = null;
            }
            else
            {
               String stringMessage = "Cannot delete this already deleted object";

               FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, stringMessage, stringMessage);
               FacesContext.getCurrentInstance().addMessage("status", message);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void refresh()
   {
      propertyAction = null;
      instanceItems = null;
      portalPageItems = null;
      selectedObjectPath = null;
      selectedObject = null;
      selectedProperties = null;
      controlProperties = null;
      theme = null;

      //
      if (selectedId == null)
      {
         selectedId = new PortalObjectId(namespace, PortalObjectPath.ROOT_PATH);
      }

      //
      selectedObject = portalObjectContainer.getObject(selectedId);

      //
      if (selectedObject.getType() == PortalObject.TYPE_PAGE)
      {
         if (pageManager == null)
         {
            // it'd be better if propertyAction was injected in faces-config.xml so that we can also inject the shared beanContext
            pageManager = new PageManagerBean(layoutService, portletDefinitionInvoker);
            pageManager.setBeanContext(new JSFBeanContext());
         }
         pageManager.page = (Page)selectedObject;
      }

      //
      selectedProperties = new PropertiesBean(this);
      controlProperties = new ControlPropertiesBean(this);

      // it'd be better if propertyAction was injected in faces-config.xml so that we can also inject the shared beanContext
      propertyAction = new PropertyAction(this);
      propertyAction.setBeanContext(new JSFBeanContext());

      //
      theme = new ThemeBean(selectedObject);

      //
      Collection<PortalObject> pages = getSelectedObject().getChildren(PortalObject.PAGE_MASK);
      ArrayList<SelectItem> list = new ArrayList<SelectItem>(pages.size() + 1);
      for (PortalObject page : pages)
      {
         SelectItem item = new SelectItem(page.getName());
         list.add(item);
      }
      list.add(new SelectItem("", "no selection"));
      portalPageItems = list.toArray(new SelectItem[list.size()]);

      //
      PortalObject o = getSelectedObject();
      ArrayList<PortalObject> path = new ArrayList<PortalObject>();
      while (o != null)
      {
         path.add(o);
         o = o.getParent();
      }
      Collections.reverse(path);
      selectedObjectPath = path;

      // rather dirty code...
      List tmp = new ArrayList(instanceContainer.getDefinitions());
      Collections.sort(tmp, InstanceManagerBean.INSTANCE_COMPARATOR);
      for (int i = 0; i < tmp.size(); i++)
      {
         Instance instance = (Instance)tmp.get(i);
         SelectItem item = new SelectItem(instance.getId());
         tmp.set(i, item);
      }
      instanceItems = (SelectItem[])tmp.toArray(new SelectItem[tmp.size()]);
   }

   public void processEvent(ActionEvent event)
   {
      if (event instanceof PortletEventEvent)
      {
         PortletEventEvent eventEvent = (PortletEventEvent)event;
         Event portletEvent = eventEvent.getEvent();
         QName name = portletEvent.getName();
         // only react to content selection events
         if (CONTENT_SELECT.equals(name))
         {
            if (portletEvent.getPayload() instanceof String)
            {
               String uri = (String)portletEvent.getPayload();

               PortalObject po = getSelectedObject();
               switch (po.getType())
               {
                  case PortalObject.TYPE_WINDOW:
                  {
                     Window window = (Window)po;
                     window.getContent().setURI(uri);
                     selectParentObject(event);
                     break;
                  }
               }
            }
            else if (portletEvent.getPayload() instanceof SelectedContent)
            {
               SelectedContent selectedContent = (SelectedContent)portletEvent.getPayload();
               String uri = selectedContent.getUri();
               Map<String, String> parameters = selectedContent.getParameters();

               PortalObject po = getSelectedObject();
               switch (po.getType())
               {
                  case PortalObject.TYPE_WINDOW:
                  {
                     Window window = (Window)po;
                     window.getContent().setURI(uri);
                     window.getContent().setParameters(parameters);
                     selectParentObject(event);
                     break;
                  }
               }
            }
         }
      }
   }

   /** The implication configure properly the page security for default access. */
   public void pageCreated(Page page)
   {
      Set constraints = Collections.singleton(new RoleSecurityBinding(PortalObjectPermission.VIEW_RECURSIVE_ACTION, SecurityConstants.UNCHECKED_ROLE_NAME));
      getDomainConfigurator().setSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), constraints);
   }

   public class PortalObjectAuthorizationBean extends MultiPortalsAuthorizationBean
   {
	   
	   // Adapt portal name from uri
	   public String getManagedPortalName()	{
		   
			String portalName = null;
			String uri = getURI();
			
			if( uri != null && uri.length() > 0)	{
				portalName = uri.substring(1);
				int endPortal = portalName.indexOf("/");
				if( endPortal != -1)	
					portalName = portalName.substring(0, endPortal);
			}		
			
			return portalName;
		}
	   

      public DomainConfigurator getDomainConfigurator()
      {
         return PortalObjectManagerBean.this.getDomainConfigurator();
      }

      protected String getURI()
      {
         PortalObject po = getSelectedObject();
         if (po != null)
         {
            return po.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
         }
         else
         {
            return null;
         }
      }

      public SelectItem[] getAvailableActions()
      {
         return new SelectItem[]{
            new SelectItem("view", "View"),
            new SelectItem("viewrecursive", "View Recursive"),
            new SelectItem("personalize", "Personalize"),
            new SelectItem("personalizerecursive", "Personalize Recursive")
         };
      }

      public String submit()
      {
         String stringMessage = "Security has been correctly updated on the page";
         FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, stringMessage, stringMessage);
         FacesContext.getCurrentInstance().addMessage("status", message);

         //
         return "objects";
      }

      public String cancel()
      {
         return "objects";
      }

      public RoleModule getRoleModule()
      {
         return roleModule;
      }
   }

   public String getPreviewURL()
   {
//      try
//      {
//         //set up the root node
//         PortalNode root = Navigation.getCurrentNode();
//         for (; root.getParent() != null;)
//         {
//            root = root.getParent();
//         }
//
//         //obtain the path to our node
//         PortalObjectImpl object = (PortalObjectImpl)getSelectedObject();
//         String path = object.getObjectNode().getPath();
//
//         //iterate to our point
//         String[] nodes = path.split("\\.");
//         PortalNode dest = root;
//
//         for (int i = 0; i < nodes.length; i++)
//         {
//            String s = nodes[i];
//            dest = dest.getChild(s);
//         }
//
//         //generate url
//         JBossRenderResponse response = (JBossRenderResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
//         String url = response.createRenderURL(dest).toString();
//         return url;
//      }
//      catch (Exception e)
//      {
//         log.info("Failed to generate object preview link");
//         e.printStackTrace();
//      }
//      //in case it fails let's point to some nice page ;)
      return "http://www.jboss.org";
   }


   /**
    * Helper method to recognize object type in EL easily
    */
   public String getSelectedObjectType()
   {
      PortalObject object = getSelectedObject();
      return object != null ? getReadableObjectTypeFor(object.getType()) : "unknown";
   }

   public String getReadableObjectTypeFor(int portalObjectType)
   {
      switch (portalObjectType)
      {
         case PortalObject.TYPE_CONTEXT:
            return "context";
         case PortalObject.TYPE_PORTAL:
            return "portal";
         case PortalObject.TYPE_PAGE:
            return "page";
         case PortalObject.TYPE_WINDOW:
            return "window";
      }

      return "unknown";
   }

   public ControlPropertiesBean getControlProperties()
   {
      return controlProperties;
   }

   public SelectItem[] getListenerIds()
   {
      Set ids = listenerRegistry.getListenerIds();
      if (ids != null)
      {
         SelectItem[] result = new SelectItem[ids.size() + 1];
         int i = 1;
         ResourceBundle rb = ResourceBundle.getBundle("Resource", FacesContext.getCurrentInstance().getExternalContext().getRequestLocale());
         result[0] = new SelectItem("", rb.getString("NO_BOUND_LISTENER"));
         for (Object id : ids)
         {
            result[i++] = new SelectItem(id);
         }

         return result;
      }

      return null;
   }
}

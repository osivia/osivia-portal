
package org.jboss.portal.core.controller.portlet;

import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.controller.state.PortletPageNavigationalState;
import org.jboss.portal.portlet.controller.state.PortletWindowNavigationalState;
import org.jboss.portal.portlet.info.NavigationInfo;
import org.jboss.portal.portlet.info.ParameterInfo;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.core.CoreConstants;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.controller.coordination.CoordinationManager;

import javax.xml.namespace.QName;
import javax.xml.XMLConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Collection;


public class ControllerPageNavigationalState implements PortletPageNavigationalState
{

   /** . */
   private static final String[] REMOVAL = new String[0];

   /** . */
   private final NavigationalStateContext navigationalStateContext;

   /** . */
   private final ControllerPortletControllerContext controllerContext;

   /** . */
   private final boolean mutable;

   /** . */
   private Map<String, org.jboss.portal.core.model.portal.navstate.WindowNavigationalState> updates;

   /** . */
   private Map<QName, String[]> pageUpdates;

   /** . */
   private Map<String, HashMap<QName, String[]>> windowPublicNavigationalStateUpdate;

   /** . */
   private final boolean implicitMode;

   public ControllerPageNavigationalState(
      NavigationalStateContext navigationalStateContext,
      ControllerPortletControllerContext controllerContext,
      boolean mutable)
   {
      this.navigationalStateContext = navigationalStateContext;
      this.controllerContext = controllerContext;
      this.mutable = mutable;
      this.updates = null;
      this.pageUpdates = null;
      this.windowPublicNavigationalStateUpdate = null;
      this.implicitMode = getCoordinationManager().resolveParameterBindingImplicitModeEnabled(controllerContext.getPage());

   }

   public ControllerPageNavigationalState(
      ControllerPageNavigationalState that,
      boolean mutable)
   {
      this.navigationalStateContext = that.navigationalStateContext;
      this.controllerContext = that.controllerContext;
      this.mutable = mutable;
      this.updates = that.updates != null ? new HashMap<String, org.jboss.portal.core.model.portal.navstate.WindowNavigationalState>(that.updates) : null;
      this.pageUpdates = that.pageUpdates != null ? new HashMap<QName, String[]>(that.pageUpdates) : null;
      //v1.0.23 : suppression hashmap (synchronisations)
      //this.windowPublicNavigationalStateUpdate = that.windowPublicNavigationalStateUpdate != null ? new HashMap<String, HashMap<QName, String[]>>(that.windowPublicNavigationalStateUpdate) : null;
      this.windowPublicNavigationalStateUpdate = that.windowPublicNavigationalStateUpdate != null ? new Hashtable<String, HashMap<QName, String[]>>(that.windowPublicNavigationalStateUpdate) : null;
      this.implicitMode = getCoordinationManager().resolveParameterBindingImplicitModeEnabled(controllerContext.getPage());
   }

   /**
    * Flush all updates to the navigational state context. 
    */
   public void flushUpdates()
   {
      
      if (windowPublicNavigationalStateUpdate != null && updates != null)
      {
         for (Map.Entry<String, HashMap<QName, String[]>> entry : windowPublicNavigationalStateUpdate.entrySet())
         {
            org.jboss.portal.core.model.portal.navstate.WindowNavigationalState wns = updates.get(entry.getKey());
            if (wns == null)
            {
               Window window = controllerContext.getWindow(entry.getKey());
               String windowId = window.getId().toString();
               wns = navigationalStateContext.getWindowNavigationalState(windowId);
            }


            if (wns != null)
            {

               Map<String, String[]> parameters = new HashMap<String, String[]>();
            
               for (Map.Entry<QName, String[]> value : entry.getValue().entrySet())
               {
                  parameters.put(value.getKey().toString(), value.getValue());
               }

               ParametersStateString pss = ParametersStateString.create(parameters);
               updates.put(entry.getKey(), new org.jboss.portal.core.model.portal.navstate.WindowNavigationalState(wns.getWindowState(), wns.getMode(), wns.getContentState(), pss));
            }
         }

         //
         windowPublicNavigationalStateUpdate.clear();

      }
      
      if (updates != null)
      {
         for (Map.Entry<String, org.jboss.portal.core.model.portal.navstate.WindowNavigationalState> entry : updates.entrySet())
         {
            Window window = controllerContext.getWindow(entry.getKey());
            org.jboss.portal.core.model.portal.navstate.WindowNavigationalState wns = entry.getValue();
            navigationalStateContext.setWindowNavigationalState(window.getId().toString(), wns);
         }

         //
         updates.clear();
      }

      //
      if (pageUpdates != null)
      {
         org.jboss.portal.core.model.portal.navstate.PageNavigationalState storedPNS = navigationalStateContext.getPageNavigationalState(controllerContext.getPageId());

         //
         Map<QName, String[]> parameters;
         if (storedPNS != null)
         {
            parameters = new HashMap<QName, String[]>(storedPNS.getParameters());
         }
         else
         {
            parameters = new HashMap<QName, String[]>();
         }

         //
         for (Map.Entry<QName, String[]> update : pageUpdates.entrySet())
         {
            String[] value = update.getValue();

            //
            if (value.length == 0)
            {
               parameters.remove(update.getKey());
            }
            else
            {
               parameters.put(update.getKey(), value);
            }
         }


         navigationalStateContext.setPageNavigationalState(controllerContext.getPageId(), new org.jboss.portal.core.model.portal.navstate.PageNavigationalState(parameters));


         //
         pageUpdates.clear();
      }



   }

   /**
    * 
    */
   public Set<String> getPortletWindowIds()
   {
      return controllerContext.getWindowNames();
   }

   /**
    * 
    */
   public PortletWindowNavigationalState getPortletWindowNavigationalState(String windowName) throws IllegalArgumentException
   {
      org.jboss.portal.core.model.portal.navstate.WindowNavigationalState update = null;

      //
      if (updates != null)
      {
         update = updates.get(windowName);
      }

      //
      if (update != null)
      {
         return new PortletWindowNavigationalState(update.getContentState(), update.getMode(), update.getWindowState());
      }

      //
      Window window = controllerContext.getWindow(windowName);

      //
      if (window != null)
      {
         String windowId = window.getId().toString();
         org.jboss.portal.core.model.portal.navstate.WindowNavigationalState wns = navigationalStateContext.getWindowNavigationalState(windowId);

         //
         if (wns != null)
         {
            return new PortletWindowNavigationalState(wns.getContentState(), wns.getMode(), wns.getWindowState());
         }
         else
         {
            return new PortletWindowNavigationalState(null, window.getInitialMode(), window.getInitialWindowState());
         }
      }

      //
      return null;
   }

   /**
    * 
    */
   public void setPortletWindowNavigationalState(String windowName, PortletWindowNavigationalState windowNavigationalState) throws IllegalArgumentException, IllegalStateException
   {
      if (!mutable)
      {
         throw new IllegalStateException();
      }

      //
      Window window = controllerContext.getWindow(windowName);
      if (window != null)
      {
         if (updates == null)
         {
            updates = new HashMap<String, org.jboss.portal.core.model.portal.navstate.WindowNavigationalState>();
         }

         //
         updates.put(windowName, new org.jboss.portal.core.model.portal.navstate.WindowNavigationalState(windowNavigationalState.getWindowState(), windowNavigationalState.getMode(), windowNavigationalState.getPortletNavigationalState(), null));
      }
   }

   public void setWindowPublicNavigationalState(String windowName, QName name, String[] value) throws IllegalArgumentException, IllegalStateException
   {
      if (!mutable)
      {
         throw new IllegalStateException();
      }

      //
      if (windowPublicNavigationalStateUpdate == null)
      {
         initiateWindowPublicNavigationalStateUpdate();
      }

      //
      if (windowPublicNavigationalStateUpdate.get(windowName) == null)
      {
         windowPublicNavigationalStateUpdate.put(windowName, new HashMap<QName, String[]>());
      }

      //
      windowPublicNavigationalStateUpdate.get(windowName).put(name, value);
   }

   public String[] getWindowPublicNavigationalState(String windowName, QName name) throws IllegalArgumentException, IllegalStateException
   {
      String[] value = null;

      //
      if (windowPublicNavigationalStateUpdate != null)
      {
         if (windowPublicNavigationalStateUpdate.get(windowName) != null)
         {
            value = windowPublicNavigationalStateUpdate.get(windowName).get(name);
         }
         
      }
      
      //
      if (value == null)
      {
         
         Window window = controllerContext.getWindow(windowName);
         org.jboss.portal.core.model.portal.navstate.WindowNavigationalState wns = navigationalStateContext.getWindowNavigationalState(window.getId().toString());

         //
         if (wns != null)
         {
            ParametersStateString pss = (ParametersStateString)wns.getPublicContentState();
            if (pss != null)
            {
               value = pss.getValues(name.toString());
            }
         }
      }

      //
      return value != null && value.length > 0 ? value : null;
   }

   /**
    * For now we do not implement any kind of mapping between qnames, it's the basic straightforward 1-1 mapping.
    */
   public Map<String, String[]> getPortletPublicNavigationalState(String windowName) throws IllegalArgumentException
   {
      PortletInfo info = controllerContext.getPortletInfo(windowName);

      CoordinationManager manager = getCoordinationManager();

      // For explicit initiate windowPublicNavigationStateUpdate with previous state
      if (windowPublicNavigationalStateUpdate == null)
      {
         initiateWindowPublicNavigationalStateUpdate();
      }

      //
      if (info != null && info.getNavigation() != null)
      {
         Map<String, String[]> publicNavigationalState = new HashMap<String, String[]>();
         for (ParameterInfo parameterInfo : info.getNavigation().getPublicParameters())
         {

            Collection<String> bindings = manager.getBindingNames(getWindow(windowName), parameterInfo.getName());

            // Don't store the URI as a page scoped public render parameter but window scoped
            // Also for explicit and parameter with no bindings
            if (CoreConstants.JBOSS_PORTAL_CONTENT_URI.equals(parameterInfo.getName()) || (!implicitMode && bindings.size() == 0))
            {
               String[] parameterValue = getWindowPublicNavigationalState(windowName, parameterInfo.getName());

               //
               if (parameterValue != null)
               {
                  String parameterId = parameterInfo.getId();

                  // We clone the value here so we keep the internal state not potentially changed
                  publicNavigationalState.put(parameterId, parameterValue.clone());

               }
            }
            else
            {
               String[] parameterValue = getPublicNavigationalState(parameterInfo.getName());

               // Explicit binding
               String[] explicitParameterValue = null;

               // Check all bindings for this window/qname pair
               // If this window/qname is binded several times with different updated params value will be unpredictable...
               for (String binding : bindings)
               {
                  explicitParameterValue = getPublicNavigationalState(new QName(XMLConstants.DEFAULT_NS_PREFIX, binding));
               }

               //
               String parameterId = parameterInfo.getId();

               //
               if (explicitParameterValue != null)
               {
                  // We clone the value here so we keep the internal state not potentially changed
                  publicNavigationalState.put(parameterId, explicitParameterValue.clone());
               }
               else if (implicitMode && parameterValue != null)
               {
                  // We clone the value here so we keep the internal state not potentially changed
                  publicNavigationalState.put(parameterId, parameterValue.clone());
               }
            }
         }

         //
         return publicNavigationalState;
      }

      //
      return null;
   }
   


   public String getPublicNavigationalParameterId(String windowName, QName name)
   {
      PortletInfo info = controllerContext.getPortletInfo(windowName);

      //
      if (info != null)
      {
         for (ParameterInfo parameterInfo : info.getNavigation().getPublicParameters())
         {
            if (parameterInfo.getName().equals(name))
            {
               return parameterInfo.getId();
            }
         }
      }

      //
      return null;
   }

   /**
    * 
    */
   public void setPortletPublicNavigationalState(String windowName, Map<String, String[]> update)
   {
      if (!mutable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      CoordinationManager manager = getCoordinationManager();

      //
      PortletInfo info = controllerContext.getPortletInfo(windowName);

      //
      if (info != null)
      {
         Window window = getWindow(windowName);
         NavigationInfo navigationInfo = info.getNavigation();
         for (Map.Entry<String, String[]> entry : update.entrySet())
         {
            String id = entry.getKey();

            //
            ParameterInfo parameterInfo = navigationInfo.getPublicParameter(id);

            //
            if (parameterInfo != null)
            {
               QName name = parameterInfo.getName();
               String[] value = entry.getValue();

               //
               Collection<String> bindings = manager.getBindingNames(window, name);

               // Don't store the URI as a page scoped public render parameter but window scoped
               // Also for explicit and parameter with no bindings
               if (CoreConstants.JBOSS_PORTAL_CONTENT_URI.equals(name)
                   || (!implicitMode && bindings.size() == 0))
               {
                  if (value.length > 0)
                  {
                     setWindowPublicNavigationalState(windowName, name, value);
                  }
                  else
                  {
                     setWindowPublicNavigationalState(windowName, name, REMOVAL);
                  }
               }
               else
               {
                  if (implicitMode)
                  {
                     if (value.length > 0)
                     {
                        setPublicNavigationalState(name, value);
                     }
                     else
                     {
                        removePublicNavigationalState(name);
                     }
                  }

                  //
                  for (String binding : bindings)
                  {
                     setPublicNavigationalState(new QName(XMLConstants.DEFAULT_NS_PREFIX, binding), value);
                  }
               }
            }
         }
      }
   }

   /**
    * 
    */
   public Set<QName> getPublicNames()
   {
      if (pageUpdates == null)
      {
         return Collections.emptySet();
      }

      //
      return pageUpdates.keySet();
   }

   /**
    * 
    */
   public String[] getPublicNavigationalState(QName name) throws IllegalArgumentException
   {
      String[] value = null;

      //
      if (pageUpdates != null)
      {
         value = pageUpdates.get(name);
      }

      //
      if (value == null)
      {
         org.jboss.portal.core.model.portal.navstate.PageNavigationalState storedPNS = navigationalStateContext.getPageNavigationalState(controllerContext.getPageId());

         //
         if (storedPNS != null)
         {
            value = storedPNS.getParameter(name);
         }
      }

      //
      return value != null && value.length > 0 ? value : null;
   }

   /**
    * 
    */
   public void setPublicNavigationalState(QName name, String[] value) throws IllegalArgumentException, IllegalStateException
   {
      if (!mutable)
      {
         throw new IllegalStateException();
      }

      //
      if (pageUpdates == null)
      {
         pageUpdates = new HashMap<QName, String[]>();
      }

      //
      pageUpdates.put(name, value);
   }

   /**
    * 
    */
   public void removePublicNavigationalState(QName name) throws IllegalArgumentException, IllegalStateException
   {
      if (!mutable)
      {
         throw new IllegalStateException();
      }

      //
      if (pageUpdates == null)
      {
         pageUpdates = new HashMap<QName, String[]>();
      }

      //
      pageUpdates.put(name, REMOVAL);
   }

   /**
    * 
    */
   public HashMap<QName, String[]> getWindowPublicContentStateParameters(String windowName)
   {
      HashMap<QName, String[]> params = new HashMap<QName, String[]>();

      //
      Window window = getWindow(windowName);

      //
      org.jboss.portal.core.model.portal.navstate.WindowNavigationalState wns = navigationalStateContext.getWindowNavigationalState(window.getId().toString());

      if (wns != null)
      {

         ParametersStateString pss = (ParametersStateString)wns.getPublicContentState();

         if (pss != null)
         {
            for (Map.Entry<String, String[]> entry : pss.getParameters().entrySet())
            {
               params.put(QName.valueOf(entry.getKey()), entry.getValue());
            }
         }
      }
      
      return params;
   }

   /**
    * @throws IllegalStateException if the public navigational state of the window is already initialized
    */
   private void initiateWindowPublicNavigationalStateUpdate() throws IllegalStateException
   {
      if (windowPublicNavigationalStateUpdate != null)
      {
         throw new IllegalStateException("Was called with a non null windowPublicNavigationalStateUpdate field");
      }

      //v1.0.23 : suppression hashmap (synchronisations)
      windowPublicNavigationalStateUpdate = new Hashtable<String, HashMap<QName, String[]>>();
      //windowPublicNavigationalStateUpdate = new HashMap<String, HashMap<QName, String[]>>();

      // Initial state for all windows on this page
      for (String windowName : controllerContext.getWindowNames())
      {
         HashMap<QName, String[]> publicContentStateParams = getWindowPublicContentStateParameters(windowName);
         windowPublicNavigationalStateUpdate.put(windowName, publicContentStateParams);
      }
   }

   public Window getWindow(String windowName)
   {
      return controllerContext.getWindow(windowName);
   }

   public CoordinationManager getCoordinationManager()
   {
      return controllerContext.getControllerContext().getController().getCoordinationManager();
   }



}

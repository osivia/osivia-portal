
package org.jboss.portal.core.model.portal.command.view;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.info.ViewCommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.common.util.ParameterMap;

import javax.xml.namespace.QName;
import javax.xml.XMLConstants;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 13858 $
 */
public class ViewPageCommand extends PageCommand
{

   /** . */
   private static final CommandInfo info = new ViewCommandInfo();

   /** . */
   private static final Map<String,String[]> EMPTY_PARAMETERS = Collections.emptyMap();

   /** . */
   private Map<String, String[]> parameters;
   
   private boolean keepPNState = true;

   public ViewPageCommand(PortalObjectId pageId, Map<String, String[]> parameters)
   {
      super(pageId);

      //
      if (parameters == null)
      {
         throw new IllegalArgumentException("No null parameters accepted");
      }

      //
      this.parameters = parameters;
   }

   public ViewPageCommand(PortalObjectId pageId, Map<String, String[]> parameters, boolean keepPNState)
   {
      this(pageId, parameters);
      this.keepPNState = keepPNState;
   }

   public ViewPageCommand(PortalObjectId pageId)
   {
      this(pageId, EMPTY_PARAMETERS);
   }

   protected Page initPage()
   {
      return (Page)getTarget();
   }

   public CommandInfo getInfo()
   {
      return info;
   }

   public Map<String, String[]> getParameters()
   {
      return parameters;
   }

   public ControllerResponse execute() throws ControllerException
   {
      if (parameters.size() > 0)
      {
         NavigationalStateContext nsContext = (NavigationalStateContext)context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

         //
         String pageId = getPage().getId().toString();

         //
         PageNavigationalState previousPNS = nsContext.getPageNavigationalState(pageId);

         //
         Map<QName, String[]> state = new HashMap<QName, String[]>();

         if (keepPNState)
         {
            // Clone the previous state if needed
            if (previousPNS != null)
            {
               state.putAll(previousPNS.getParameters());
            }
         }
            
         //
         for (Map.Entry<String, String[]> entry : parameters.entrySet())
         {
            state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), entry.getValue());
         }

         //
         nsContext.setPageNavigationalState(pageId, new PageNavigationalState(state));
      }

      //
      return new UpdatePageResponse(page.getId());
   }
}

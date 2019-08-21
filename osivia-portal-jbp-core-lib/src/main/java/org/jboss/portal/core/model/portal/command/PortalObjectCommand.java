package org.jboss.portal.core.model.portal.command;

import org.jboss.portal.core.controller.AccessDeniedException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.SecurityException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;



public abstract class PortalObjectCommand extends ControllerCommand
{

   /** . */
   protected final PortalObjectId targetId;

   /** . */
   protected PortalObject target;

   /** . */
   protected boolean dashboard;

   protected PortalObjectCommand(PortalObjectId targetId)
   {
      if (targetId == null)
      {
         throw new IllegalArgumentException();
      }
      this.targetId = targetId;
      this.dashboard = "dashboard".equals(targetId.getNamespace());
   }

   public final PortalObjectId getTargetId()
   {
      return targetId;
   }

   public void acquireResources() throws NoSuchResourceException
   {
      // Get portal object
      target = context.getController().getPortalObjectContainer().getObject(targetId);

      //
      if (target == null)
      {
         throw new NoSuchResourceException(targetId.toString());
      }
   }

   /**
    * Enforce the security on this command using the provided portal authorization manager.
    * 
    * Modification osivia sur les templates
    *
    * @param pam the portal authorization manager
    * @throws org.jboss.portal.core.controller.SecurityException
    *          if the access is not granted
    */
   public void enforceSecurity(PortalAuthorizationManager pam) throws SecurityException
   {
	   PortalObject target = getTarget();
	   
	   if (  target instanceof ITemplatePortalObject)	{
		   /*
		   // Dans le cas du template, il faut regarder les droits posées sur le template
		   //d'origine
		   // De plus, il n'y a pas de personnalisation
		   target  =  ((ITemplatePortalObject) target).getTemplate();
		   */
		   // Dans le cas de page dynamiques, on consièdere que les droits sont hérités du parent
		   
		   do	{
			   target = target.getParent();
		   } while (target instanceof ITemplatePortalObject);
	   }

	   

      PortalObjectId id = target.getId();
      PortalObjectPermission perm = new PortalObjectPermission(id, PortalObjectPermission.VIEW_MASK);
      if (!pam.checkPermission(perm))
      {
         throw new AccessDeniedException(id.toString(), "View permission not granted");
      }
   }

   /**
    * Return the target portal object of this command.
    *
    * @return the target portal object
    */
   public final PortalObject getTarget()
   {
      return target;
   }

   /**
    * Return true if the command is in a dashboard context.
    */
   public boolean isDashboard()
   {
      return dashboard;
   }
}

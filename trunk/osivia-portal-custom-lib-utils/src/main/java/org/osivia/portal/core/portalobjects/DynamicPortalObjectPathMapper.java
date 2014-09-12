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

package org.osivia.portal.core.portalobjects;

import org.jboss.portal.common.text.FastURLEncoder;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.mapping.PortalObjectPathMapper;
import org.jboss.portal.server.servlet.PathMapping;
import org.jboss.portal.server.servlet.PathMappingResult;
import org.jboss.portal.server.servlet.PathParser;

import java.util.Iterator;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8811 $
 */
public class DynamicPortalObjectPathMapper implements PortalObjectPathMapper
{

   /** . */
   protected String namespace;

   /** . */
   protected PortalObjectContainer container;

   /** . */
   protected PathMapping mapping;

   /** . */
   protected PathParser pathParser = new PathParser();

   /** . */
   protected String effectiveNamespace;

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public PortalObjectContainer getContainer()
   {
      return container;
   }

   public void setContainer(PortalObjectContainer container)
   {
      this.container = container;
   }

   public void start()
   {
      effectiveNamespace = namespace == null ? "" : namespace;
      mapping = new PathMapping()
      {
         public Object getRoot()
         {
            return container.getContext(namespace != null ? namespace : "");
         }

         public Object getChild(Object parent, String name)
         {
            PortalObject po = (PortalObject)parent;
            
            // PIA : conversion en page dynamique
            if( po instanceof Page)
            	po = container.getObject(po.getId());
            
            return po.getChild(name);
         }
      };
   }

   public void stop()
   {
      mapping = null;
   }

   public PortalObject getTarget(ControllerContext controllerContext, String path)
   {
      if (path.length() == 0 || "/".equals(path))
      {
         return container.getContext(effectiveNamespace);
      }
      else
      {
         PathMappingResult result = pathParser.map(mapping, path);
         return (PortalObject)result.getTarget();
      }
   }

   public PortalObject getDefaultTarget()
   {
      return container.getContext(namespace);
   }

   public PathMapping createPathMapper(ControllerContext controllerContext)
   {
      return mapping;
   }

   public void appendPath(StringBuffer buffer, PortalObjectId id)
   {
      for (Iterator i = id.getPath().names(); i.hasNext();)
      {
         String name = (String)i.next();
         name = FastURLEncoder.getUTF8Instance().encode(name);
         buffer.append('/').append(name);
      }
   }
}

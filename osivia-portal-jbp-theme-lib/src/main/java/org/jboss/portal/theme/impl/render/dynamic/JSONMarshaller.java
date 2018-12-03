/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.theme.impl.render.dynamic;

import org.jboss.portal.theme.impl.render.dynamic.json.JSONException;
import org.jboss.portal.theme.impl.render.dynamic.json.JSONWriter;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageLocationResponse;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageStateResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JSONMarshaller
{

   public void write(DynaResponse response, Writer w) throws IOException
   {
      JSONWriter writer = new JSONWriter(w);

      try
      {
         if (response instanceof UpdatePageStateResponse)
         {
            UpdatePageStateResponse umr = (UpdatePageStateResponse)response;

            //
            writer.object();

            //
            writer.key("type").value("update_markup");
            
            writer.key("url").value(umr.getUrl());

            //
            if (umr.getViewState() != null)
            {
               writer.key("view_state").value(umr.getViewState());
            }

            //
            writer.key("fragments").object();
            for (Iterator i = umr.getFragments().entrySet().iterator(); i.hasNext();)
            {
               Map.Entry entry = (Map.Entry)i.next();
               String id = (String)entry.getKey();
               String fragment = (String)entry.getValue();
               writer.key(id).value(fragment);
            }
            writer.endObject();

            //
            writer.endObject();
         }
         else if (response instanceof UpdatePageLocationResponse)
         {
            UpdatePageLocationResponse upr = (UpdatePageLocationResponse)response;
            writer.object();
            writer.key("type").value("update_page");
            writer.key("location").value(upr.getLocation());
            writer.endObject();
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      catch (JSONException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof IOException)
         {
            throw (IOException)cause;
         }
         else
         {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
         }
      }
   }
}

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
package org.jboss.portal.theme.page;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.theme.ThemeTools;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.jboss.portal.theme.render.renderer.PortletRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.io.Serializable;
import java.util.Map;

/**
 * A WindowContext represents a portlet window on a page in the scope of one
 * request.
 * <p>
 * It allows the layout gy for instance, to change the position of the window on
 * the page (region, order) without persising the change. The change will only
 * be valid for the term of the current request.
 * </p>
 * <p>
 * This context implements the <code>Comparable</code> interface to allow
 * natural sorting of the windows in one region, based on their order.
 * </p>
 * 
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public final class WindowContext implements Comparable, Serializable,
		WindowRendererContext {

	/** The serialVersionUID */
	private static final long serialVersionUID = -225656969004976637L;

	/** . */
	private final String id;

	/** . */
	private final DecorationRendererContextImpl decoration = new DecorationRendererContextImpl(
			this);

	/** . */
	private final PortletRendererContextImpl portlet = new PortletRendererContextImpl(
			this);

	// Mutable properties

	/** . */
	private String regionName;

	/** . */
	private String order;

	/** . */
	final WindowResult result;

	/** . */
	private Boolean regionCms;

	public WindowContext(String id, String regionName, String order,
			WindowResult result) {
		this.id = id;
		this.regionName = regionName;
		this.order = order;
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public String getOrder() {
		return order;
	}

	public String getRegionName() {
		return regionName;
	}

	public WindowState getWindowState() {
		return result.getWindowState();
	}

	public Mode getMode() {
		return result.getMode();
	}

	public DecorationRendererContext getDecoration() {
		return decoration;
	}

	public PortletRendererContext getPortlet() {
		return portlet;
	}

	public String getProperty(String name) {
		return (String) result.getProperties().get(name);
	}

	public Map getProperties() {
		return result.getProperties();
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public WindowResult getResult() {
		return result;
	}

	/**
	 * @return the regionCms
	 */
	public Boolean getRegionCms() {
		return regionCms;
	}

	/**
	 * @param regionCms
	 *            the regionCms to set
	 */
	public void setRegionCms(Boolean regionCms) {
		this.regionCms = regionCms;
	}

	public int compareTo(Object o) {
		WindowContext that = (WindowContext) o;
		return ThemeTools.compareWindowOrder(this.order, this.id, that.order,
				that.id);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final WindowContext that = (WindowContext) o;

		return id.equals(that.id);
	}

	public int hashCode() {
		return id.hashCode();
	}

	public String toString() {
		return "WindowContext[id=" + id + ",region=" + regionName + ",order="
				+ order + "]";
	}
}

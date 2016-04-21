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
package org.osivia.portal.core.formatters;

import java.io.IOException;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.cms.CMSServiceCtx;

/**
 * Formatter interface.
 * Can be accessed from JSP pages.
 */
public interface IFormatter {

    String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception;

    String formatRequestFilteringPolicyList(PortalObject po, String policyName, String selectedPolicy) throws Exception;

    String formatDisplayLiveVersionList(CMSServiceCtx ctx, PortalObject po, String scopeName, String selectedVersion) throws Exception;

    String formatContextualization(PortalObject po, String selectedScope) throws Exception;

    /**
     * Format portal object ID into HTML-safe identifier.
     *
     * @param id portal object ID
     * @return HTML-safe format
     * @throws IOException
     */
    String formatHtmlSafeEncodingId(PortalObjectId id) throws IOException;

    /**
     * Format portlets list into HTML fancybox data.
     *
     * @param context controller context, which contains locales and URL generation data
     * @return HTML fancybox data
     * @throws IOException
     */
    String formatHtmlPortletsList(ControllerContext context) throws IOException;

}

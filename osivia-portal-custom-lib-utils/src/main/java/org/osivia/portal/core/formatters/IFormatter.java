package org.osivia.portal.core.formatters;

import java.io.IOException;
import java.util.List;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
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
     * Format hierarchical tree models into HTML data, with UL and LI nodes
     * 
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHTMLTreeModels(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

    /**
     * Format hierarchical tree pages (without templates) into HTML data, with UL and LI nodes
     * 
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHTMLTreePageParent(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

    /**
     * Format hierarchical tree templates into HTML data, with UL and LI nodes
     *
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHTMLTreeTemplateParent(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

    /**
     * Format hierarchical tree alphabetically movable pages objects into HTML data, with UL and LI nodes
     *
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHTMLTreePortalObjectsMove(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

    /**
     * Format hierarchical tree alphabetically ordered pages objects into HTML data, with UL and LI nodes
     *
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHTMLTreePortalObjectsAlphaOrder(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

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

    /**
     * Format windows settings into HTML fancyboxes data.
     *
     * @param currentPage current page
     * @param windows current page windows list
     * @param context controller context, which contains locales and URL generation data
     * @return HTML fancyboxes data
     * @throws IOException
     */
    String formatHtmlWindowsSettings(Page currentPage, List<Window> windows, ControllerContext context) throws IOException;

}

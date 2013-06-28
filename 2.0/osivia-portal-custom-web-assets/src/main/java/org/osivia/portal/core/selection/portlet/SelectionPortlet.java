package org.osivia.portal.core.selection.portlet;

import java.io.IOException;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.net.media.MediaType;
import org.osivia.portal.api.selection.ISelectionService;
import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;

/**
 * Selection Portlet.
 * 
 * @see GenericPortlet
 * @author Cédric Krommenhoek
 */
public class SelectionPortlet extends GenericPortlet {

    /** Selection service name. */
    private static final String SERVICE_NAME = "SelectionService";
    /** Admin page path constant. */
    private static final String PATH_PAGE_ADMIN = "/WEB-INF/jsp/selection/admin.jsp";
    /** View page path constant. */
    private static final String PATH_PAGE_VIEW = "/WEB-INF/jsp/selection/view.jsp";
    /** Portlet mode "admin" constant. */
    private static final String PORTLET_MODE_ADMIN = "admin";
    /** Portlet mode "view" constant. */
    private static final String PORTLET_MODE_VIEW = "view";
    /** Selection request attribute. */
    private static final String REQUEST_ATTRIBUTE_SELECTION = "selection";
    /** Selection identifier request attribute. */
    private static final String REQUEST_ATTRIBUTE_SELECTION_ID = "selectionId";
    /** Action request parameter. */
    private static final String REQUEST_PARAMETER_ACTION = "action";
    /** Cancel request parameter. */
    private static final String REQUEST_PARAMETER_CANCEL = "cancel";
    /** Delete request parameter. */
    private static final String REQUEST_PARAMETER_DELETE = "delete";
    /** Delete all request parameter. */
    private static final String REQUEST_PARAMETER_DELETE_ALL = "deleteAll";
    /** Item identifier request parameter. */
    private static final String REQUEST_PARAMETER_ITEM_ID = "id";
    /** Save request parameter. */
    private static final String REQUEST_PARAMETER_SAVE = "save";
    /** Selection identifier window property. */
    private static final String WINDOW_PROPERTY_SELECTION_ID = "osivia.selection.id";

    /** Logger. */
    protected static final Log logger = LogFactory.getLog(SelectionPortlet.class);

    /** Selection service. */
    private ISelectionService selectionService;

    /**
     * Default contructor.
     */
    public SelectionPortlet() {
        super();
    }

    /**
     * Selection service access. There is only one instance per portlet.
     * 
     * @return the selection service.
     */
    protected ISelectionService getSelectionService() {
        if (this.selectionService == null) {
            this.selectionService = (ISelectionService) this.getPortletContext().getAttribute(SERVICE_NAME);
        }
        return this.selectionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        if (PORTLET_MODE_ADMIN.equals(request.getPortletMode().toString()) && (request.getParameter(REQUEST_PARAMETER_SAVE) != null)) {
            PortalWindow window = WindowFactory.getWindow(request);
            window.setProperty(WINDOW_PROPERTY_SELECTION_ID, request.getParameter(REQUEST_ATTRIBUTE_SELECTION_ID));

            response.setPortletMode(PortletMode.VIEW);
            response.setWindowState(WindowState.NORMAL);
        }

        if (PORTLET_MODE_ADMIN.equals(request.getPortletMode().toString()) && (request.getParameter(REQUEST_PARAMETER_CANCEL) != null)) {
            response.setPortletMode(PortletMode.VIEW);
            response.setWindowState(WindowState.NORMAL);
        }

        if (PORTLET_MODE_VIEW.equals(request.getPortletMode().toString()) && REQUEST_PARAMETER_DELETE.equals(request.getParameter(REQUEST_PARAMETER_ACTION))) {
            String selectionId = this.getSelectionId(request);
            String itemId = request.getParameter(REQUEST_PARAMETER_ITEM_ID);
            this.getSelectionService().removeItem(request, selectionId, itemId);
        }

        if (PORTLET_MODE_VIEW.equals(request.getPortletMode().toString())
                && REQUEST_PARAMETER_DELETE_ALL.equals(request.getParameter(REQUEST_PARAMETER_ACTION))) {
            String selectionId = this.getSelectionId(request);
            this.getSelectionService().deleteSelection(request, selectionId);
        }
    }

    /**
     * Admin view display.
     * 
     * @param request request
     * @param response response
     * @throws PortletException
     * @throws IOException
     */
    @RenderMode(name = PORTLET_MODE_ADMIN)
    public void doAdmin(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String selectionId = this.getSelectionId(request);
        request.setAttribute(REQUEST_ATTRIBUTE_SELECTION_ID, selectionId);

        response.setContentType(MediaType.TEXT_HTML.getValue());
        this.getPortletContext().getRequestDispatcher(PATH_PAGE_ADMIN).include(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String selectionId = this.getSelectionId(request);
        Set<SelectionItem> selectionItemsSet = this.getSelectionService().getSelectionItems(request, selectionId);
        request.setAttribute(REQUEST_ATTRIBUTE_SELECTION, selectionItemsSet);

        response.setContentType(MediaType.TEXT_HTML.getValue());
        this.getPortletContext().getRequestDispatcher(PATH_PAGE_VIEW).include(request, response);
    }

    /**
     * Utility method to access the current selection id.
     * 
     * @param request generated request
     * @return the current selection id
     */
    private String getSelectionId(PortletRequest request) {
        PortalWindow window = WindowFactory.getWindow(request);
        String selectionId = window.getProperty(WINDOW_PROPERTY_SELECTION_ID);

        if (selectionId == null) {
            selectionId = StringUtils.EMPTY;
        }

        return selectionId;
    }

}

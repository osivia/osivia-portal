package org.osivia.portal.core.selection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.selection.ISelectionService;
import org.osivia.portal.api.selection.SelectionItem;

/**
 * Selection service implementation.
 * 
 * @see ISelectionService
 * @author Cédric Krommenhoek
 */
public class SelectionService implements ISelectionService {

    /** Selections map attribute. */
    public static final String ATTR_SELECTIONS_MAP = "osivia.selections";
    
    public static final String ATTR_SELECTIONS_TIMESTAMP = "osivia.selections.ts";

    /** Selection scope property prefixe. */
    private static final String PREFIXE_SELECTION_SCOPE = "osivia.selection.";
    /** Selection scope property suffixe. */
    private static final String SUFFIXE_SELECTION_SCOPE = ".scope";

    /** Logger. */
    protected static Log logger = LogFactory.getLog(SelectionService.class);

    /**
     * {@inheritDoc}
     */
    public boolean addItem(Object request, String selectionId, SelectionItem selectionItem) {
        // Debug log
        if (logger.isDebugEnabled()) {
            logger.debug("addItem");
        }

        if (request instanceof PortletRequest) {
            PortletRequest portletRequest = (PortletRequest) request;

            Set<SelectionItem> selectionItemsSet = this.getSelectionItemsSet(portletRequest, selectionId);
            boolean isAdded = selectionItemsSet.add(selectionItem);

            if (isAdded) {
                // Save into session
                this.setSelectionItemSet(portletRequest, selectionId, selectionItemsSet);
            }

            return isAdded;
        }

        // Cannot add item
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean removeItem(Object request, String selectionId, String itemId) {
        // Debug log
        if (logger.isDebugEnabled()) {
            logger.debug("removeItem");
        }

        if (request instanceof PortletRequest) {
            PortletRequest portletRequest = (PortletRequest) request;

            Set<SelectionItem> selectionItemsSet = this.getSelectionItemsSet(portletRequest, selectionId);
            SelectionItem selectionItem = new SelectionItem(itemId, null, null);
            boolean isRemoved = selectionItemsSet.remove(selectionItem);

            if (isRemoved) {
                // Save into session
                this.setSelectionItemSet(portletRequest, selectionId, selectionItemsSet);
            }

            return isRemoved;
        }

        // Cannot remove item
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<SelectionItem> getSelectionItems(Object request, String selectionId) {
        // Debug log
        if (logger.isDebugEnabled()) {
            logger.debug("getSelectionItemsSet");
        }

        if (request instanceof PortletRequest) {
            PortletRequest portletRequest = (PortletRequest) request;
            return this.getSelectionItemsSet(portletRequest, selectionId);
        }

        // Cannot access selection item set
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSelection(Object request, String selectionId) {
        // Debug log
        if (logger.isDebugEnabled()) {
            logger.debug("deleteSelection");
        }

        if (request instanceof PortletRequest) {
            PortletRequest portletRequest = (PortletRequest) request;
            ControllerContext context = this.getContext(portletRequest);
            Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = this.getSelectionsMap(context);
            SelectionMapIdentifiers selectionIdentifiers = this.getSelectionMapIdentifiers(portletRequest, selectionId);
            selectionsMap.remove(selectionIdentifiers);
            context.setAttribute(Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_MAP, selectionsMap);
            
            // Portal notification timestamp
            context.setAttribute(Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_TIMESTAMP, System.currentTimeMillis());

        }
    }

    /**
     * Utility method used to access the current context.
     * 
     * @param request generated request
     * @return the current context
     */
    private ControllerContext getContext(PortletRequest portletRequest) {
        ControllerContext context = (ControllerContext) portletRequest.getAttribute("osivia.controller");
        return context;
    }

    /**
     * Utility method used to access the session selection items set.
     * 
     * @param httpSession session
     * @param selectionId specified selection identifier
     * @return the selection items set
     */
    private Set<SelectionItem> getSelectionItemsSet(PortletRequest request, String selectionId) {
        ControllerContext context = this.getContext(request);
        Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = this.getSelectionsMap(context);
        SelectionMapIdentifiers selectionIdentifiers = this.getSelectionMapIdentifiers(request, selectionId);
        Set<SelectionItem> selectionItemsSet;
        if (selectionsMap.containsKey(selectionIdentifiers)) {
            selectionItemsSet = selectionsMap.get(selectionIdentifiers);
        } else {
            selectionItemsSet = new LinkedHashSet<SelectionItem>();
            selectionsMap.put(selectionIdentifiers, selectionItemsSet);
            context.setAttribute(Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_MAP, selectionsMap);
            if (logger.isDebugEnabled()) {
                logger.info("Selection item set initialized (id = " + selectionId + ").");
            }
        }
        return selectionItemsSet;
    }

    /**
     * Utility method used to save the selection items set.
     * 
     * @param context context
     * @param selectionId specified selection identifier
     * @param selectionItemsSet selection item set to save
     */
    private void setSelectionItemSet(PortletRequest request, String selectionId, Set<SelectionItem> selectionItemsSet) {
        ControllerContext context = this.getContext(request);
        Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = this.getSelectionsMap(context);
        SelectionMapIdentifiers selectionIdentifiers = this.getSelectionMapIdentifiers(request, selectionId);
        selectionsMap.put(selectionIdentifiers, selectionItemsSet);
        context.setAttribute(Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_MAP, selectionsMap);
        
        // Portal notification timestamp
        context.setAttribute(Scope.PRINCIPAL_SCOPE,ATTR_SELECTIONS_TIMESTAMP, System.currentTimeMillis());
    }


    /**
     * Utility method used to access the selections map.
     * 
     * @param context context
     * @return the selections map
     */
    @SuppressWarnings("unchecked")
    private Map<SelectionMapIdentifiers, Set<SelectionItem>> getSelectionsMap(ControllerContext context) {
        // Map loader from session
        Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = (Map<SelectionMapIdentifiers, Set<SelectionItem>>) context.getAttribute(
                Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_MAP);

        // If null, initialization
        if (selectionsMap == null) {
            selectionsMap = new HashMap<SelectionMapIdentifiers, Set<SelectionItem>>();
            context.setAttribute(Scope.PRINCIPAL_SCOPE, ATTR_SELECTIONS_MAP, selectionsMap);
            if (logger.isDebugEnabled()) {
                logger.debug("Selections map initialized.");
            }
        }

        return selectionsMap;
    }

    /**
     * Utility method used to generate selection map identifiers.
     * 
     * @param request generated request
     * @param selectionId selection identifier
     * @return selection map identifiers
     */
    private SelectionMapIdentifiers getSelectionMapIdentifiers(PortletRequest request, String selectionId) {
        Window window = (Window) request.getAttribute("osivia.window");
        Page page = window.getPage();
        String scopeProperty = page.getDeclaredProperty(PREFIXE_SELECTION_SCOPE + selectionId + SUFFIXE_SELECTION_SCOPE);
        SelectionScope scope = SelectionScope.fromScopeName(scopeProperty);

        PortalObjectId pageId = null;
        if (SelectionScope.SCOPE_PAGE.equals(scope)) {
            pageId = page.getId();
        }

        SelectionMapIdentifiers selectionIdentifiers = new SelectionMapIdentifiers(selectionId, scope, pageId);
        return selectionIdentifiers;
    }

}

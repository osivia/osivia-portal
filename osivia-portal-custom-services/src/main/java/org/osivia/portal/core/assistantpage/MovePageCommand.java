package org.osivia.portal.core.assistantpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;


/**
 * Move page command.
 * 
 * @author Cédric Krommenhoek
 * @see AssistantCommand
 */
public class MovePageCommand extends AssistantCommand {

    private String pageId;
    private String destinationPageId;


    /**
     * Default constructor.
     */
    public MovePageCommand() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param pageId
     * @param destinationPageId
     */
    public MovePageCommand(String pageId, String destinationPageId) {
        super();
        this.pageId = pageId;
        this.destinationPageId = destinationPageId;
    }


    /**
     * Command execution
     * 
     * @return response
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Pages recuperation
        PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(pagePortalObjectId);
        PortalObject destinationPage = null;
        if (!StringUtils.endsWith(destinationPageId, IFormatter.SUFFIX_VIRTUAL_END_NODES_ID)) {
            PortalObjectId destinationPortalObjectId = PortalObjectId.parse(this.destinationPageId, PortalObjectPath.SAFEST_FORMAT);
            destinationPage = this.getControllerContext().getController().getPortalObjectContainer().getObject(destinationPortalObjectId);
        }

        if (page.equals(destinationPage)) {
            // Do nothing
            return new UpdatePageResponse(page.getId());
        }        

        // Check parents
        PortalObject parentPage = page.getParent();
        PortalObject parentDestination;
        if (destinationPage == null) {
            if (StringUtils.endsWith(this.destinationPageId, IFormatter.SUFFIX_VIRTUAL_END_NODES_ID)) {
                String parentDestinationId = StringUtils.removeEnd(this.destinationPageId, IFormatter.SUFFIX_VIRTUAL_END_NODES_ID);
                PortalObjectId parentDestinationPortalObjectId = PortalObjectId.parse(parentDestinationId, PortalObjectPath.SAFEST_FORMAT);
                parentDestination = this.getControllerContext().getController().getPortalObjectContainer().getObject(parentDestinationPortalObjectId);
            } else {
                // Unknow destination page
                throw new IllegalArgumentException(); // TODO : Error management
            }
        } else {
            parentDestination = destinationPage.getParent();
        }

        if (page.equals(parentDestination) || PortalObjectUtils.isAncestor(page, parentDestination)) {
            // Destination page cannot be a descendant of the current page
            throw new IllegalArgumentException(); // TODO : Error management
        }
        
        // Move
        if (!parentPage.equals(parentDestination)) {
            String canonicalId;
            
            // Save security bindings
            canonicalId = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
            DomainConfigurator domainConfigurator = this.getControllerContext().getController().getPortalObjectContainer().getAuthorizationDomain().getConfigurator();
            Set<RoleSecurityBinding> securityBindings = domainConfigurator.getSecurityBindings(canonicalId);
            
            String oldName = page.getName();
            page = page.copy(parentDestination, oldName, true);
            parentPage.destroyChild(oldName);
            
            // Restore security bindings
            canonicalId = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
            domainConfigurator.setSecurityBindings(canonicalId, securityBindings);
        }

        // Pages order access
        SortedSet<Page> pages = new TreeSet<Page>(PageUtils.orderComparator);
        Collection<PortalObject> siblings = parentDestination.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject sibling : siblings) {
            Page siblingPage = (Page) sibling;
            if (!siblingPage.equals(page)) {
                pages.add(siblingPage);
            }
        }
        List<Page> sortedPages = new ArrayList<Page>(pages);

        // Change order        
        int orderValue = 1;
        for (Page reorderedPage : sortedPages) {
            if (reorderedPage.equals(destinationPage)) {
                page.setDeclaredProperty(PageUtils.TAB_ORDER, String.valueOf(orderValue++));
            }
            reorderedPage.setDeclaredProperty(PageUtils.TAB_ORDER, String.valueOf(orderValue++));
        }
        if (destinationPage == null) {
            page.setDeclaredProperty(PageUtils.TAB_ORDER, String.valueOf(orderValue++));
        }

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        return new UpdatePageResponse(page.getId());
    }

}

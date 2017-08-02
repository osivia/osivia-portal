/**
 *
 */
package org.osivia.portal.core.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;


/**
 * Advanced search command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class AdvancedSearchCommand extends ControllerCommand {

    /** Generic command action value. */
    public static final String COMMAND_ACTION_VALUE = "advancedSearch";
    /** Current page identifier parameter name. */
    public static final String PAGE_ID_PARAMETER_NAME = "pageId";
    /** Search keywords parameter name. */
    public static final String SEARCH_PARAMETER_NAME = "search";
    /** Advanced search indicator parameter name. */
    public static final String ADVANCED_SEARCH_PARAMETER_NAME = "advancedSearch";


    /** Search keywords selector identifier. */
    private static final String KEYWORDS_SELECTOR_ID = "keywords";
    /** Dummy selector identifier. */
    private static final String DUMMY_SELECTOR_ID = "dummy-selector";
    /** Advanced search page name. */
    private static final String PAGE_NAME = "AdvancedSearch";
    /** Advanced search template path. */
    private static final String TEMPLATE_PATH = "/default/templates/search";

    /** Command info. */
    private static final CommandInfo commandInfo = new ActionCommandInfo(false);

    /** Search content. */
    private final String search;
    /** Advanced search indicator. */
    private final boolean advancedSearch;


    /**
     * Constructor.
     *
     * @param search search content
     * @param advancedSearch advanced search indicator
     */
    public AdvancedSearchCommand(String search, boolean advancedSearch) {
        this.search = search;
        this.advancedSearch = advancedSearch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Portal identifier
        Portal portal = PortalObjectUtils.getPortal(context);
        
        String portalId = portal.getId().toString(PortalObjectPath.SAFEST_FORMAT);

        
        
        // Search keywords
        Map<String, List<String>> selectors = new HashMap<String, List<String>>();
        selectors.put(KEYWORDS_SELECTOR_ID, Arrays.asList(StringUtils.split(this.search)));

        // Dummy selector
        if (this.advancedSearch) {
            selectors.put(DUMMY_SELECTOR_ID, Arrays.asList(new String[]{DUMMY_SELECTOR_ID}));
        }

        // Template identifier
        PortalObjectId templatePortalObjectId = PortalObjectId.parse(TEMPLATE_PATH, PortalObjectPath.CANONICAL_FORMAT);
        String templateId = templatePortalObjectId.toString(PortalObjectPath.SAFEST_FORMAT);

        // Properties
        Map<String, String> properties = new HashMap<String, String>(0);

        // Parameters
        Map<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("selectors", PageParametersEncoder.encodeProperties(selectors));

        // Command execution
        StartDynamicPageCommand command = new StartDynamicPageCommand(portalId, PAGE_NAME, null, templateId, properties, parameters);
        return this.getControllerContext().execute(command);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return commandInfo;
    }


    /**
     * Getter for search.
     *
     * @return the search
     */
    public String getSearch() {
        return this.search;
    }

    /**
     * Getter for advancedSearch.
     *
     * @return the advancedSearch
     */
    public boolean isAdvancedSearch() {
        return this.advancedSearch;
    }

}

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
    /** Advanced search indicator parameter name. */
    public static final String SELECTORS_PARAMETER_NAME = "selectors";

    /** Search keywords selector identifier. */
    private static final String KEYWORDS_SELECTOR_ID = "search";
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
    
    private Map<String, List<String>> selectors;


    /**
     * Constructor.
     */
    public AdvancedSearchCommand() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param advancedSearch advanced search indicator
     */
    public AdvancedSearchCommand(boolean advancedSearch) {
        this(StringUtils.EMPTY, advancedSearch);
    }

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
        Portal portal = PortalObjectUtils.getPortal(this.context);
        String portalId = portal.getId().toString(PortalObjectPath.SAFEST_FORMAT);

        // Selectors
        //Map<String, List<String>> selectors = new HashMap<String, List<String>>();
        if(selectors == null) {
        	selectors = new HashMap<String, List<String>>();
        }

        // Search keywords
        if (StringUtils.isNotEmpty(this.search)) {
            selectors.put(KEYWORDS_SELECTOR_ID, Arrays.asList(StringUtils.split(this.search)));
        }

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

    /**
     * Getter for selectors.
     * @return
     */
	public Map<String, List<String>> getSelectors() {
		return selectors;
	}

	/**
     * Setter for selectors.
	 * @param selectors
	 */
	public void setSelectors(Map<String, List<String>> selectors) {
		this.selectors = selectors;
	}
    
    

}

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
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;


/**
 * Advanced search command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class AdvancedSearchCommand extends ControllerCommand {

    /** Search keywords identifier. */
    private static final String KEYWORDS_ID = "search";
    /** Advanced search page name. */
    private static final String PAGE_NAME = "AdvancedSearch";
    /** Advanced search template path. */
    private static final String TEMPLATE_PATH = "/default/templates/advancedSearch";

    /** Command info. */
    private static final CommandInfo commandInfo = new ActionCommandInfo(false);

    /** Current page identifier. */
    private final String pageId;
    /** Search content. */
    private final String search;


    /**
     * Constructor.
     *
     * @param pageId current page identifier
     * @param search search content
     */
    public AdvancedSearchCommand(String pageId, String search) {
        this.pageId = pageId;
        this.search = search;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Portal identifier
        PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pagePortalObjectId);
        String portalId = page.getPortal().getId().toString(PortalObjectPath.SAFEST_FORMAT);

        // Search keywords
        Map<String, List<String>> keywords = new HashMap<String, List<String>>();
        keywords.put(KEYWORDS_ID, Arrays.asList(StringUtils.split(this.search)));

        // Template identifier
        PortalObjectId templatePortalObjectId = PortalObjectId.parse(TEMPLATE_PATH, PortalObjectPath.CANONICAL_FORMAT);
        String templateId = templatePortalObjectId.toString(PortalObjectPath.SAFEST_FORMAT);

        // Properties
        Map<String, String> properties = new HashMap<String, String>(0);

        // Parameters
        Map<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("selectors", PageParametersEncoder.encodeProperties(keywords));

        // Command execution
        StartDynamicPageCommand command = new StartDynamicPageCommand(portalId, PAGE_NAME, null, templateId, properties,
                parameters);
        return this.getControllerContext().execute(command);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return commandInfo;
    }

}

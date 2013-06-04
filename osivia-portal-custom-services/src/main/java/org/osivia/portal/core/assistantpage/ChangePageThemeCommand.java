/**
 * 
 */
package org.osivia.portal.core.assistantpage;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;


/**
 * Change page theme command.
 * @author Cédric Krommenhoek
 * @see AssistantCommand
 */
public class ChangePageThemeCommand extends AssistantCommand {

    /** Current page ID. */
    private String pageId;
    /** Current page new theme. */
    private String theme;
 
    
    /**
     * Default constructor.
     */
    public ChangePageThemeCommand() {
        super();
    }

    /**
     * Constructor.
     * @param pageId current page ID
     * @param theme current page new theme
     */
    public ChangePageThemeCommand(String pageId, String theme) {
        super();
        this.pageId = pageId;
        this.theme = theme;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ControllerResponse executeAssistantCommand() throws Exception {
        // Get current page object
        PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(pagePortalObjectId);
        
        // Change theme
        if (StringUtils.isEmpty(this.theme)) {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, null);
        } else {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, this.theme);
        }
        
        return new UpdatePageResponse(pagePortalObjectId);
    }

}

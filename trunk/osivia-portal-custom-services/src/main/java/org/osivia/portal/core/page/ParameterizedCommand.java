package org.osivia.portal.core.page;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Parameterized command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class ParameterizedCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "parameterized";

    /** CMS path parameter name. */
    public static final String CMS_PATH_PARAMETER = "path";
    /** Template parameter name. */
    public static final String TEMPLATE_PARAMETER = "template";
    /** Renderset parameter name. */
    public static final String RENDERSET_PARAMETER = "renderset";
    /** Layout state parameter name. */
    public static final String LAYOUT_STATE_PARAMETER = "layoutState";
    /** Permalinks indicator parameter name. */
    public static final String PERMALINKS_PARAMETER = "permalinks";

    /** Command info. */
    private final CommandInfo info;
    /** CMS path. */
    private final String cmsPath;
    /** Template. */
    private final String template;
    /** Renderset. */
    private final String renderset;
    /** Layout state. */
    private final String layoutState;
    /** Permalinks indicator. */
    private final Boolean permalinks;


    /**
     * Constructor.
     *
     * @param cmsPath CMS path
     * @param template template
     * @param renderset renderset
     * @param layoutState layout state
     * @param permalinks permalinks indicator
     */
    public ParameterizedCommand(String cmsPath, String template, String renderset, String layoutState, Boolean permalinks) {
        super();
        this.info = new ActionCommandInfo(false);
        this.cmsPath = cmsPath;
        this.template = template;
        this.renderset = renderset;
        this.layoutState = layoutState;
        this.permalinks = permalinks;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Template
        if (StringUtils.isNotEmpty(this.template)) {
            this.context.setAttribute(REQUEST_SCOPE, InternalConstants.PARAMETERIZED_TEMPLATE_ATTRIBUTE, this.template);
        }

        // Renderset
        if (StringUtils.isNotEmpty(this.renderset)) {
            this.context.setAttribute(REQUEST_SCOPE, InternalConstants.PARAMETERIZED_RENDERSET_ATTRIBUTE, this.renderset);
        }

        // Layout state
        if (StringUtils.isNotEmpty(this.layoutState)) {
            this.context.setAttribute(REQUEST_SCOPE, InternalConstants.PARAMETERIZED_LAYOUT_STATE_ATTRIBUTE, this.layoutState);
        }

        // Authenticated indicator
        if (this.permalinks != null) {
            this.context.setAttribute(REQUEST_SCOPE, InternalConstants.PARAMETERIZED_PERMALINKS_ATTRIBUTE, this.permalinks);
        }

        CmsCommand cmsCommand = new CmsCommand(null, this.cmsPath, null, null, null, null, null, null, null, null, null);
        return this.context.execute(cmsCommand);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParameterizedCommand [");
        if (this.info != null) {
            builder.append("info=");
            builder.append(this.info);
            builder.append(", ");
        }
        if (this.cmsPath != null) {
            builder.append("cmsPath=");
            builder.append(this.cmsPath);
            builder.append(", ");
        }
        if (this.template != null) {
            builder.append("template=");
            builder.append(this.template);
            builder.append(", ");
        }
        if (this.renderset != null) {
            builder.append("renderset=");
            builder.append(this.renderset);
            builder.append(", ");
        }
        if (this.layoutState != null) {
            builder.append("layoutState=");
            builder.append(this.layoutState);
            builder.append(", ");
        }
        if (this.permalinks != null) {
            builder.append("permalinks=");
            builder.append(this.permalinks);
        }
        builder.append("]");
        return builder.toString();
    }


    /**
     * Getter for cmsPath.
     *
     * @return the cmsPath
     */
    public String getCmsPath() {
        return this.cmsPath;
    }

    /**
     * Getter for template.
     *
     * @return the template
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * Getter for renderset.
     *
     * @return the renderset
     */
    public String getRenderset() {
        return this.renderset;
    }

    /**
     * Getter for layoutState.
     *
     * @return the layoutState
     */
    public String getLayoutState() {
        return this.layoutState;
    }

    /**
     * Getter for permalinks.
     *
     * @return the permalinks
     */
    public Boolean getPermalinks() {
        return this.permalinks;
    }

}

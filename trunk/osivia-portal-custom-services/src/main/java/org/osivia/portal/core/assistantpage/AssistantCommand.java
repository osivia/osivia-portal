package org.osivia.portal.core.assistantpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.internationalization.InternationalizationUtils;

/**
 * Custom commands super class.
 *
 * @see ControllerCommand
 */
public abstract class AssistantCommand extends ControllerCommand {

    /** Command info. */
    private static final CommandInfo info = new ActionCommandInfo(false);
    /** Logger. */
    private static final Log logger = LogFactory.getLog(AssistantCommand.class);
    /** Admin portal object identifier. */
    private static final PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

    /** Bundle factory. */
    private final IBundleFactory bundleFactory;


    /**
     * Default constructor.
     */
    public AssistantCommand() {
        IInternationalizationService internationalizationService = InternationalizationUtils.getInternationalizationService();
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }


    /**
     * Command execution.
     *
     * @return controller response
     * @throws Exception
     */
    protected abstract ControllerResponse executeAssistantCommand() throws Exception;


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            // Check permission
            PortalObjectPermission perm = new PortalObjectPermission(adminPortalId, PortalObjectPermission.VIEW_MASK);
            if (!this.getControllerContext().getController().getPortalAuthorizationManagerFactory().getManager().checkPermission(perm)) {
                throw new SecurityException("Commande interdite");
            }

            ControllerResponse res = this.executeAssistantCommand();

            if (res instanceof UpdatePageResponse) {
                // On transforme en redirection pour commiter la transaction pour que les threads associés aux windows voient les données directement dès
                // l'affichage de la page
                PageURL url = new PageURL(((UpdatePageResponse) res).getPageId(), this.getControllerContext());
                res = new RedirectionResponse(url.toString() + "?init-state=true");
            }
            return res;
        } catch (Exception e) {
            if (!(e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw (ControllerException) e;
            }
        }
    }


    /**
     * Utility method used to create a localized string map with other locales values.
     *
     * @param locale current locale
     * @param displayName new display name
     * @param name name
     * @return the localized string map
     */
    protected static Map<Locale, String> createLocalizedStringMap(Locale locale, LocalizedString displayName, String name) {
        Map<Locale, String> map = new HashMap<Locale, String>();
        if (displayName != null) {
            Map<Locale, LocalizedString.Value> oldMap = displayName.getValues();
            Collection<LocalizedString.Value> values = oldMap.values();
            for (LocalizedString.Value value : values) {
                map.put(value.getLocale(), value.getString());
            }
        }
        map.put(locale, name);
        return map;
    }


    /**
     * Getter for logger.
     *
     * @return the logger
     */
    protected Log getLogger() {
        return logger;
    }

    /**
     * Getter for bundleFactory.
     *
     * @return the bundleFactory
     */
    protected IBundleFactory getBundleFactory() {
        return this.bundleFactory;
    }

}

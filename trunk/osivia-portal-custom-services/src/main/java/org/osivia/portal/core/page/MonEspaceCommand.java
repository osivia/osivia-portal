/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;


public class MonEspaceCommand extends ControllerCommand {

    /** . */
    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

    /** . */
    private String portalName;

    public MonEspaceCommand() {
        this(null);
    }

    public MonEspaceCommand(String portalName) {
        this.portalName = portalName;
    }

    @Override
    public CommandInfo getInfo() {
        return info;
    }

    public String getPortalName() {
        return this.portalName;
    }

    @Override
    public ControllerResponse execute() throws ControllerException {
        try {

            Portal portal = null;

            String portalName = this.getPortalName();

            if (portalName != null) {
                portal = this.getControllerContext().getController().getPortalObjectContainer().getContext().getPortal(
                        portalName);
            } else {
                portal = this.getControllerContext().getController().getPortalObjectContainer().getContext()
                        .getDefaultPortal();
            }

            IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");

            /* Calcul de la page d'accueil personnalisée */



            ProfilBean profil = profilManager.getProfilPrincipalUtilisateur();

            if (profil == null) {
                return new SecurityErrorResponse("Vous devez être connecté", SecurityErrorResponse.NOT_AUTHORIZED,
                        false);
            }


            // Accès page profil
            if( profil.getDefaultPageName().length() == 0){
                // Pas de profil
                PageURL url = new PageURL(portal.getId(), this.getControllerContext());
                url.setRelative(false);
                return new RedirectionResponse(url.toString() + "?init-state=true&redirect=true");
            }

            PortalObject child = portal.getChild(profil.getDefaultPageName());



            if (child != null) {
                PageURL url = new PageURL(child.getId(), this.getControllerContext());

                logger.debug("Redirection page : " + url.toString());
                // Redirection
                return new RedirectionResponse(url.toString() + "?init-state=true&redirect=true");
            } else {
                // Page inexistante, on redirige vers la page par defaut du
                // portail

                logger.error(" Page : " + profil.getDefaultPageName() + "inexistante");
                PageURL url = new PageURL(portal.getId(), this.getControllerContext());
                return new RedirectionResponse(url.toString() + "?init-state=true&redirect=true");
            }

        } catch (Exception e) {
            if (!(e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw (ControllerException) e;
            }
        }

    }
}

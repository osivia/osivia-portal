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
 */
package org.osivia.portal.core.profils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpSession;

import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.impl.jacc.JACCPortalPrincipal;
import org.osivia.portal.api.Constants;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.tracker.ITracker;


public class ProfilManager implements IProfilManager {

    public static final String ATTRIBUTE_PROFILE_NAME = "osivia.profil";
    public static final String DEFAULT_PROFIL_NAME = "osivia.default_profil";

    private ITracker tracker;
    private IdentityServiceController identityServiceController;
    private RoleModule roleModule;
    protected PortalObjectContainer portalObjectContainer;
    protected ICacheService cacheService;

    public ICacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(ICacheService cacheService) {
        this.cacheService = cacheService;
    }

    private Map<String, List<ProfilBean>> listeProfilsCache = null;
    private long cacheCount = 0;

    private Portal getDefaultPortal() {
        return getPortalObjectContainer().getContext().getDefaultPortal();
    }

    /**
     * Renvoie la liste des rôles filtrée, ordonnée avec en plus les roles
     * unchecked et Authenticated
     * 
     * @return
     */

    public List<Role> getFilteredRoles() {

        List<Role> filteredRoles = new ArrayList<Role>();

        Set<String> rolesInserted = new HashSet<String>();


        for (ProfilBean profil : getListeProfils()) {
            if (!rolesInserted.contains(profil.getRoleName())) {
                rolesInserted.add(profil.getRoleName());
                String roleDisplayName = "Role " + profil.getRoleName();
                try {
                    Role role = getRoleModule().findRoleByName(profil.getRoleName());
                    roleDisplayName = role.getDisplayName();

                } catch (Exception e) {
                    // Role non accessible, on ne fait rien
                    // Sera affichée avec le nom du role (pas le displayName)
                }
                filteredRoles.add(new FilteredRole(profil.getRoleName(), roleDisplayName));
            }
        }

        filteredRoles.add(new FilteredRole(SecurityConstants.UNCHECKED_ROLE_NAME, "Utilisateurs anonymes"));

        if ("1".equals(System.getProperty("sso.undeclared-user")))
            filteredRoles.add(new FilteredRole("undeclared-user", "Utilisateurs non déclarés"));

        return filteredRoles;

    }

    public List<ProfilBean> getListeProfils(Portal portal) {

        XMLSerializer serializer = new XMLSerializer();

        String encodedList = portal.getDeclaredProperty("osivia.profils");
        List<ProfilBean> profils = serializer.decodeAll(encodedList);
        if (profils == null)
            profils = new ArrayList<ProfilBean>();

        return profils;

    }

    public ProfilBean getProfil(String name) {

        List<ProfilBean> profils = getListeProfils();

        for (ProfilBean profil : profils) {
            if (profil.getName().equals(name))
                return profil;
        }

        return null;
    }


    public List<ProfilBean> getListeProfils() {

        if (listeProfilsCache != null) {

            long newCacheCount = getCacheService().getProfilsCount();

            if (cacheCount < newCacheCount)
                initListeProfils();

            if (cacheCount > newCacheCount) {
                // Peut arriver si les gestionnaire de cache (jboss cache) a planté
                // On remet à jour le cache centralisé qui porte la valeur de référence

                do {
                    getCacheService().incrementProfilsCount();
                } while (cacheCount > getCacheService().getProfilsCount());
            }
        }

        if (listeProfilsCache == null) {
            initListeProfils();
        }

        // v2.MS Get current portal name
        // TODO factoriser dans un portal manager


        /*
         * String portalName = null;
         * 
         * try {
         * portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
         * if (portalName == null)
         * portalName = getPortalObjectContainer().getContext().getDefaultPortal().getName();
         * } catch( Exception e){
         * portalName = "default";
         * }
         */
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);


        List<ProfilBean> profils = listeProfilsCache.get(portalName);
        // if( profils == null || profils.size() == 0)
        // profils = listeProfilsCache.get("default");

        return profils;
    }


    private synchronized void initListeProfils() {

        long newCacheCount = getCacheService().getProfilsCount();

        if (listeProfilsCache == null || cacheCount < newCacheCount) {

            listeProfilsCache = new HashMap<String, List<ProfilBean>>();

            for (PortalObject po : getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK)) {

                List<ProfilBean> profilByPortal = new ArrayList<ProfilBean>();
                listeProfilsCache.put(((Portal) po).getName(), profilByPortal);

                for (ProfilBean profilPortail : getListeProfils((Portal) po)) {


                    if (!profilByPortal.contains(profilPortail))
                        profilByPortal.add(profilPortail);
                }
            }

            cacheCount = getCacheService().getProfilsCount();

        }

    }

    public void setListeProfils(Portal portal, List<ProfilBean> profils) {

        XMLSerializer serializer = new XMLSerializer();

        String encodedList = serializer.encodeAll(profils);
        portal.setDeclaredProperty("osivia.profils", encodedList);


        /* Réinitialiser les caches de profils (cluster ) */
        getCacheService().incrementProfilsCount();

    }

    private ProfilBean creerProfilPrincipal(HttpSession session, Portal portal) {

        /* Récupération des rôles */

        Subject subject;

        try {
            subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (PolicyContextException e) {
            throw new RuntimeException(e);
        }

        if (subject == null) {
            // Resource binaries
            subject = PageProperties.getProperties().getBinarySubject();
        }


        if (subject != null) {
            // utilisation mapping standard du portail
            JACCPortalPrincipal pp = new JACCPortalPrincipal(subject);

            /* On parcourt les espaces pour voir celui qui correspond au profil */

            for (ProfilBean profil : getListeProfils()) {
                Iterator iter = pp.getRoles().iterator();
                while (iter.hasNext()) {
                    Principal principal = (Principal) iter.next();
                    if (principal.getName().equals(profil.getRoleName()) && profil.getDefaultPageName().length() > 0) {

                        session.setAttribute(ATTRIBUTE_PROFILE_NAME, profil);
                        return profil;
                    }
                }
            }


            /* Aucun profil trouve, création d'un profil par défaut */

            String pageAccueilConnecte = portal.getDeclaredProperty("osivia.unprofiled_home_page");
            if (pageAccueilConnecte == null)
                pageAccueilConnecte = portal.getDefaultPage().getName();

            ProfilBean profilDefaut = new ProfilBean(DEFAULT_PROFIL_NAME, "default", pageAccueilConnecte, "");
            session.setAttribute(ATTRIBUTE_PROFILE_NAME, profilDefaut);

            return profilDefaut;
        }

        return null;

    }

    /**
     * 
     * Vérifie si l'utilisateur a le profil
     * 
     * @param name
     * @return
     */
    public boolean verifierProfilUtilisateur(String name) {

        /*
         * if(true)
         * return true;
         */

        /* Récupération des rôles */

        Subject subject;

        try {
            subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (PolicyContextException e) {
            throw new RuntimeException(e);
        }

        // Cas des ressources binaires
        if (subject == null) {
            // Resource binaries
            subject = PageProperties.getProperties().getBinarySubject();
        }

        if (subject == null)
            return false;

        // utilisation mapping standard du portail
        JACCPortalPrincipal pp = new JACCPortalPrincipal(subject);

        boolean check = false;

        ProfilBean profil = getProfil(name);

        if (profil != null) {
            Iterator iter = pp.getRoles().iterator();
            while (iter.hasNext()) {
                Principal principal = (Principal) iter.next();
                if (principal.getName().equals(profil.getRoleName())) {
                    check = true;
                }
            }
        }

        return check;
    }


    public ProfilBean getProfilPrincipalUtilisateur() {

        HttpSession session = getTracker().getHttpSession();

        ProfilBean profil = (ProfilBean) session.getAttribute(ATTRIBUTE_PROFILE_NAME);
        if (profil == null) {
            profil = creerProfilPrincipal(session, getDefaultPortal());
        }
        return profil;

    }

    public IdentityServiceController getIdentityServiceController() {
        return identityServiceController;
    }

    public void setIdentityServiceController(IdentityServiceController identityServiceController) {
        this.identityServiceController = identityServiceController;
    }

    public ITracker getTracker() {
        return tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }

    public RoleModule getRoleModule() throws Exception {
        if (roleModule == null) {
            roleModule = (RoleModule) getIdentityServiceController().getIdentityContext().getObject(IdentityContext.TYPE_ROLE_MODULE);
        }
        return roleModule;
    }

    public PortalObjectContainer getPortalObjectContainer() {
        return portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

}

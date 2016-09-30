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
package org.osivia.portal.administration.util;

/**
 * Administration constants.
 *
 * @author Cédric Krommenhoek
 */
public final class AdministrationConstants {

    /** Portal object container name. */
    public static final String PORTAL_OBJECT_CONTAINER_NAME = "PortalObjectContainer";
    /** Dynamic object container name. */
    public static final String DYNAMIC_OBJECT_CONTAINER_NAME = "DynamicObjectContainer";
    /** Authorization domain registry name. */
    public static final String AUTHORIZATION_DOMAIN_REGISTRY_NAME = "AuthorizationDomainRegistry";
    /** Config deployer name. */
    public static final String CONFIG_DEPLOYER_NAME = "ConfigDeployer";
    /** Theme service name. */
    public static final String THEME_SERVICE_NAME = "ThemeService";
    /** Layout service name. */
    public static final String LAYOUT_SERVICE_NAME = "LayoutService";
    /** Profile manager name. */
    public static final String PROFILE_MANAGER_NAME = "ProfileManager";
    /** Cache service name. */
    public static final String CACHE_SERVICE_NAME = "CacheService";
    /** Internationalization service name. */
    public static final String INTERNATIONALIZATION_SERVICE_NAME = "InternationalizationService";

    /** Portal identifier attribute name. */
    public static final String PORTAL_ID_ATTRIBUTE_NAME = "osivia.portal.administration.portalId";
    /** Admin privileges indicator attribute name. */
    public static final String ADMIN_PRIVILEGES_ATTRIBUTE_NAME = "osivia.portal.administration.isAdmin";

    /** Page identifier request parameter name. */
    public static final String PAGE_ID_PARAMETER_NAME = "pageId";
    /** Export filter request parameter name. */
    public static final String EXPORT_FILTER_PARAMETER_NAME = "filter";


    /**
     * Private constructor : prevent instantiation.
     */
    private AdministrationConstants() {
        throw new AssertionError();
    }

}

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
package org.osivia.portal.core.cms;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.editor.EditorService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.statistics.IStatisticsService;
import org.osivia.portal.api.user.IUserPreferencesService;
import org.osivia.portal.core.cms.spi.ICMSIntegration;


/**
 * This listener listens to the main portal session events to notify the CMS
 * system of the end of the user session
 */
public class SessionListener implements HttpSessionListener {

    public static final String ACTIVE_SESSION_SYNC = "activeSessionSync";


    public static long activeSessions = 0;


    /**
     * Nuxeo service.
     */
    private final ICMSIntegration nuxeoService;
    /**
     * Statistics service.
     */
    private final IStatisticsService statisticsService;
    /**
     * Preferences service.
     */
    private final IUserPreferencesService preferencesService;
    /**
     * Editor service.
     */
    private final EditorService editorService;


    /**
     * Constructor.
     */
    public SessionListener() {
        super();

        // Nuxeo service
        this.nuxeoService = Locator.findMBean(ICMSIntegration.class, "osivia:service=NuxeoService");
        // Statistics service
        this.statisticsService = Locator.findMBean(IStatisticsService.class, IStatisticsService.MBEAN_NAME);
        // User Preferences service
        this.preferencesService = Locator.findMBean(IUserPreferencesService.class, IUserPreferencesService.MBEAN_NAME);
        // Editor service
        this.editorService = Locator.findMBean(EditorService.class, EditorService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        synchronized (ACTIVE_SESSION_SYNC) {
            activeSessions++;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        synchronized (ACTIVE_SESSION_SYNC) {
            activeSessions--;
        }

        this.nuxeoService.sessionDestroyed(sessionEvent);

        // HTTP session
        HttpSession httpSession = sessionEvent.getSession();

        try {
            this.statisticsService.aggregateUserStatistics(httpSession);
            this.preferencesService.updateUserPreferences(httpSession);
            this.editorService.clearAllTemporaryAttachedPictures(httpSession);
        } catch (PortalException e) {
            // Do nothing
        }
    }

}

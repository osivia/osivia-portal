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
package org.osivia.portal.administration.ejb;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.core.cms.SessionListener;

/**
 * System bean.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("systemBean")
@Scope(ScopeType.EVENT)
public class SystemBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Cache service. */
    private ICacheService cacheService;
    /** Sessions count. */
    private long sessionsCount = -1;


    /**
     * Default constructor.
     */
    public SystemBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
        this.cacheService = (ICacheService) this.getPortletContext().getAttribute(AdministrationConstants.CACHE_SERVICE_NAME);
    }


    /**
     * Get sessions count.
     *
     * @return sessions count
     */
    public long getSessionsCount() {
        if (this.sessionsCount == -1) {
            this.sessionsCount = SessionListener.activeSessions;
        }
        return this.sessionsCount;
    }


    /**
     * Refresh sessions count.
     */
    public void refresh() {
        this.sessionsCount = -1;
    }


    /**
     * Reload portal parameters.
     *
     * @throws Exception
     */
    public void reloadPortalParameters() throws Exception {
        this.cacheService.initPortalParameters();
        this.setMessages("Vocabulaires réinitialisés");
    }


    /**
     * Setter for sessionsCount.
     *
     * @param sessionsCount the sessionsCount to set
     */
    public void setSessionsCount(long sessionsCount) {
        this.sessionsCount = sessionsCount;
    }

}

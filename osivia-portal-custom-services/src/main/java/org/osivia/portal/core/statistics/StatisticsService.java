package org.osivia.portal.core.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.statistics.IStatisticsService;
import org.osivia.portal.api.statistics.SpaceStatistics;
import org.osivia.portal.api.statistics.SpaceVisits;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

/**
 * Statistics service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IStatisticsService
 */
public class StatisticsService implements IStatisticsService {

    /** Statistics session attribute. */
    private static final String STATISTICS_SESSION_ATTRIBUTE = "osivia.statistics";


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * {@inheritDoc}
     */
    public StatisticsService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementsUserStatistics(PortalControllerContext portalControllerContext, String path) throws PortalException {
        // Root space path
        String rootPath = this.getRootPath(portalControllerContext, path);

        if (StringUtils.isNotEmpty(rootPath)) {
            // HTTP session
            HttpSession httpSession = portalControllerContext.getHttpServletRequest().getSession();

            // User session statistics
            UserSessionStatistics userStatistics = this.getUserSessionStatistics(httpSession);

            userStatistics.increments(rootPath);
        }
    }


    /**
     * Get root space path.
     *
     * @param portalControllerContext portal controller context
     * @param currentPath current path
     * @return path
     * @throws PortalException
     */
    private String getRootPath(PortalControllerContext portalControllerContext, String currentPath) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Loop path
        String path = currentPath;
        // Root space path
        String rootPath = null;

        while (StringUtils.isNotEmpty(path) && StringUtils.isEmpty(rootPath)) {
            // Space config
            CMSItem spaceConfig;

            try {
                spaceConfig = cmsService.getSpaceConfig(cmsContext, path);
            } catch (CMSException e) {
                spaceConfig = null;
            }

            if (spaceConfig != null) {
                // Space type
                DocumentType spaceType = spaceConfig.getType();

                if ((spaceType != null) && spaceType.isRoot()) {
                    rootPath = spaceConfig.getPath();
                }
            }

            // Parent path
            PortalObjectPath objectPath = PortalObjectPath.parse(path, PortalObjectPath.CANONICAL_FORMAT);
            PortalObjectPath parentObjectPath = objectPath.getParent();
            if (parentObjectPath == null) {
                path = null;
            } else {
                path = parentObjectPath.toString(PortalObjectPath.CANONICAL_FORMAT);
            }
        }

        return rootPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void aggregateUserStatistics(HttpSession httpSession) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();

        // User session statistics
        UserSessionStatistics userStatistics = this.getUserSessionStatistics(httpSession);

        // Hits
        Map<String, Integer> hits = userStatistics.getHits();

        if (MapUtils.isNotEmpty(hits)) {
            // Space paths
            Set<String> paths = hits.keySet();

            // Current user
            String user = (String) httpSession.getAttribute("PRINCIPAL_TOKEN");

            try {
                List<SpaceStatistics> spaceStatistics = cmsService.getSpaceStatistics(cmsContext, paths);

                for (SpaceStatistics statistics : spaceStatistics) {
                    // Update statistics
                    this.updateSpaceStatistics(statistics);

                    // Current day visits
                    SpaceVisits currentDayVisits = statistics.getCurrentDayVisits();
                    // Current month visits
                    SpaceVisits currentMonthVisits = statistics.getCurrentMonthVisits();

                    // Update hits
                    Integer spaceHits = hits.get(statistics.getPath());
                    currentDayVisits.setHits(currentDayVisits.getHits() + spaceHits);
                    currentMonthVisits.setHits(currentMonthVisits.getHits() + spaceHits);

                    if (StringUtils.isEmpty(user)) {
                        // Add anonymous visit
                        currentDayVisits.setAnonymousVisitors(currentDayVisits.getAnonymousVisitors() + 1);
                        currentMonthVisits.setAnonymousVisitors(currentMonthVisits.getAnonymousVisitors() + 1);
                    } else {
                        // Add identified visit
                        currentDayVisits.getVisitors().add(user);
                        currentMonthVisits.getVisitors().add(user);
                    }

                    statistics.setPreviousUpdate(statistics.getLastUpdate());
                    statistics.setLastUpdate(new Date());
                }

                cmsService.updateStatistics(cmsContext, httpSession, spaceStatistics);
            } catch (CMSException e) {
                throw new PortalException(e);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SpaceStatistics getSpaceStatistics(PortalControllerContext portalControllerContext, String path) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Paths
        Set<String> paths = new HashSet<>(1);
        paths.add(path);

        // Statistics
        SpaceStatistics statistics;
        try {
            List<SpaceStatistics> list = cmsService.getSpaceStatistics(cmsContext, paths);

            if (CollectionUtils.isNotEmpty(list) && (list.size() == 1)) {
                statistics = list.get(0);
            } else {
                throw new CMSException(CMSException.ERROR_NOTFOUND);
            }
        } catch (CMSException e) {
            throw new PortalException(e);
        }

        // Update statistics
        this.updateSpaceStatistics(statistics);

        return statistics;
    }


    /**
     * Get user session statistics.
     *
     * @param httpSession HTTP session
     * @return user session statistics
     */
    private UserSessionStatistics getUserSessionStatistics(HttpSession httpSession) {
        // Session attribute
        Object attribute = httpSession.getAttribute(STATISTICS_SESSION_ATTRIBUTE);

        // User session statistics
        UserSessionStatistics userStatistics;
        if ((attribute == null) || !(attribute instanceof UserSessionStatistics)) {
            userStatistics = new UserSessionStatistics();
            httpSession.setAttribute(STATISTICS_SESSION_ATTRIBUTE, userStatistics);
        } else {
            userStatistics = (UserSessionStatistics) attribute;
        }

        return userStatistics;
    }


    /**
     * Update space statistics.
     * 
     * @param statistics space statistics
     * @throws PortalException
     */
    private void updateSpaceStatistics(SpaceStatistics statistics) throws PortalException {
        // Last update
        Date lastUpdate = statistics.getLastUpdate();

        if (lastUpdate != null) {
            // Today
            Date today = new Date();

            if (!DateUtils.isSameDay(lastUpdate, today)) {
                // Current day visits
                SpaceVisits currentDayVisits = statistics.getCurrentDayVisits();
                // Historized days visits
                Map<Date, SpaceVisits> historizedDaysVisits = statistics.getHistorizedDaysVisits();


                // Update historized days
                Date historizedDay = DateUtils.truncate(lastUpdate, Calendar.DAY_OF_MONTH);
                currentDayVisits.setUniqueVisitors(currentDayVisits.getAnonymousVisitors() + currentDayVisits.getVisitors().size());
                historizedDaysVisits.put(historizedDay, currentDayVisits);

                currentDayVisits = new SpaceVisits();
                statistics.setCurrentDayVisits(currentDayVisits);


                if (!DateUtils.truncatedEquals(lastUpdate, today, Calendar.MONTH)) {
                    // Current month visits
                    SpaceVisits currentMonthVisits = statistics.getCurrentMonthVisits();
                    // Historized months visits
                    Map<Date, SpaceVisits> historizedMonthsVisits = statistics.getHistorizedMonthsVisits();


                    // Update historized months
                    Date historizedMonth = DateUtils.truncate(lastUpdate, Calendar.MONTH);
                    currentMonthVisits.setUniqueVisitors(currentMonthVisits.getAnonymousVisitors() + currentMonthVisits.getVisitors().size());
                    historizedMonthsVisits.put(historizedMonth, currentMonthVisits);

                    currentMonthVisits = new SpaceVisits();
                    statistics.setCurrentMonthVisits(currentMonthVisits);
                }
            }
        }
    }


    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}

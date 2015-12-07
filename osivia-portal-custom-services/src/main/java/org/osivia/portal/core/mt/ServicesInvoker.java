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

package org.osivia.portal.core.mt;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.core.theme.WindowContextFactory;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.security.SecurityAssociation;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.tracker.ITracker;


/**
 *
 * Lancement de n threads (un par service) en passant par un pool de threads puis attente de toutes les reponses.
 * Les donnees envoyees et recues sont stockees dans une Mapc.
 *
 * @author jeanseb
 */
public class ServicesInvoker {

    int nbWindows = 0;
    Page page;
    ControllerContext context;
    Collection<Window> windows;
    PortalLayout layout;
    PortalTheme theme;
    PageService pageService;
    ControllerPageNavigationalState pageNavigationalState;
    ITracker tracker;
    IProfilerService profiler;
    boolean parallelisationExpired = false;


    // Timeout par défaut en secondes
    private static final int DEFAULT_TIMEOUT_THREAD = 1200;

    List<Reponse> renditions = new ArrayList<Reponse>();

    protected static final Log logger = LogFactory.getLog(ServicesInvoker.class);

    private static List<String> excludedPortlets;

    protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");


    public ServicesInvoker(Page page, ControllerContext context, Collection<?> windows, PortalLayout layout, PortalTheme theme, PageService pageService,
            ControllerPageNavigationalState pageNavigationalState, ITracker tracker, IProfilerService profiler) throws Exception {
        this.page = page;
        this.context = context;

        /* Filtrage des windows virtuelles ()regin="virtual" qui ne sont pas en MAXIMIZED */

        List<Window> filteredWindows = new ArrayList<Window>();
        for (Object po : windows) {
            boolean selectThisWindow = true;
            Window origWindow = (Window) po;

            if ("virtual".equals(origWindow.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION))) {

                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, origWindow.getId());

                WindowNavigationalState windowNavState = (WindowNavigationalState) context.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                // On regarde si la fenêtre est en vue MAXIMIZED

                if ((windowNavState != null) && WindowState.NORMAL.equals(windowNavState.getWindowState())) {
                    selectThisWindow = false;
                }
            }

            if (selectThisWindow) {
                filteredWindows.add(origWindow);
            }
        }


        this.windows = filteredWindows;


        this.layout = layout;
        this.theme = theme;
        this.pageService = pageService;
        this.pageNavigationalState = pageNavigationalState;
        this.tracker = tracker;
        this.profiler = profiler;

    }


    public ControllerResponse render() throws Exception {
        // Mode anonyme
        HttpServletRequest request = this.context.getServerInvocation().getServerContext().getClientRequest();

        Map<String, String> pageProperties = new HashMap<String, String>(this.page.getProperties());

        // Parameterized renderset
        Object parameterizedRenderset = this.context.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.PARAMETERIZED_RENDERSET_ATTRIBUTE);
        if (parameterizedRenderset != null) {
            pageProperties.put(ThemeConstants.PORTAL_PROP_RENDERSET, parameterizedRenderset.toString());
        }

        // Call the portlet container to create the markup fragment(s) for each portlet that needs to render itself
        PageResult pageResult = new PageResult(this.page.getName(), pageProperties);

        // Parameterized layout state
        Object parameterizedLayoutState = this.context.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.PARAMETERIZED_LAYOUT_STATE_ATTRIBUTE);
        if (parameterizedLayoutState != null) {
            pageResult.setLayoutState(parameterizedLayoutState.toString());
        }


        // The window context factory
        WindowContextFactory wcFactory = new WindowContextFactory(this.context);

        String policyCtxId = PolicyContext.getContextID();


        // Création de l'action
        Subject subject = SecurityAssociation.getSubject();
        Principal principal = SecurityAssociation.getPrincipal();
        Object credential = SecurityAssociation.getCredential();


        // Map<PortalObjectId, Future> pageFutures = new HashMap<PortalObjectId, Future>();
        // v 1.0.16
        Map<PortalObjectId, Future<?>> pageFutures = new Hashtable<PortalObjectId, Future<?>>();

        int nbThreads = 0;

        // Render the windows
        for (Window window2 : this.windows) {
            PortalObject o = window2;
            if (o instanceof Window) {
                Window window = (Window) o;

                if (windowlogger.isDebugEnabled()) {

                    windowlogger.debug("-------------- DEBUT DUMP ServicesInvoker " + this.context + " w:" + window.getId());


                    HttpSession session = this.context.getServerInvocation().getServerContext().getClientRequest().getSession(false);


                    windowlogger.debug("request " + request + " session " + session);

                    /*
                     * NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
                     *
                     *
                     *
                     * Object attr = session.getAttribute("portal.principaladmin"+ window.getId());
                     * windowlogger.debug("attr " + window.getId() + " = " +attr );
                     *
                     * WindowNavigationalState ws = (WindowNavigationalState) context.getAttribute(
                     * ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                     *
                     * if (ws != null) {
                     *
                     * windowlogger.debug("   window :" + window.getName());
                     * windowlogger.debug("      state :" + ws.getWindowState());
                     * if (ws.getContentState() != null) {
                     * if (ws.getContentState().decodeOpaqueValue(ws.getContentState().getStringValue()).size() > 0)
                     * windowlogger.debug("      content state :" + ws.getContentState());
                     * }
                     * if (ws.getPublicContentState() != null) {
                     * if (ws.getPublicContentState()
                     * .decodeOpaqueValue(ws.getPublicContentState().getStringValue()).size() > 0)
                     * windowlogger.debug("      public state :" + ws.getPublicContentState());
                     * }
                     *
                     * }
                     */

                    windowlogger.debug("-------------- FIN DUMP ServiceThread");
                }


                RenderWindowCommand renderCmd = new RenderWindowCommand(this.pageNavigationalState, window.getId());


                //

                //
                if (renderCmd != null) {
                    // Les portlets en cache sont gérés en synchrone pour éviter
                    // La création d'un thread inutile

                    NavigationalStateContext ctx = (NavigationalStateContext) this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);


                    Map<String, String[]> pageNavigationalState = null;

                    PageNavigationalState pns = ctx.getPageNavigationalState(this.page.getId().toString());

                    if (pns != null) {
                        Map<QName, String[]> qNameMap = pns.getParameters();
                        if ((qNameMap != null) && !qNameMap.isEmpty()) {
                            pageNavigationalState = new HashMap<String, String[]>(qNameMap.size());

                            for (Map.Entry<QName, String[]> entry : qNameMap.entrySet()) {
                                pageNavigationalState.put(entry.getKey().toString(), entry.getValue());
                            }
                        }
                    }

                    WindowNavigationalState windowState = ctx.getWindowNavigationalState(window.getId().toString());


                    if (ThreadCacheManager.isPresumedCached(this.context, pageNavigationalState, window, windowState)
                            || StringUtils.isNotBlank(window.getDeclaredProperty("osivia.sequence.priority"))) {
                        // if( ThreadCacheManager.isPresumedCached(context, window) || (true)) {

                        WindowRendition rendition = renderCmd.render(this.context);

                        this.finRequete(window, rendition);

                    } else {
                        // Les portlets JSF (entre autres ...) ont besoin de plus te temps pour charger le contexte
                        // TODO : faire ca plus proprement
                        // - détecter qu'il s'agit du premier appel au portlet
                        // - ne faire le traitement de timeout qu'au premier appel
                        // Cela impose de rentrer dans le container ...


                        if (excludedPortlets == null) {
                            String excludedPortletsProp = System.getProperty("portlets.maxExecution.excludedPortlets");
                            if (excludedPortletsProp != null) {
                                excludedPortlets = Arrays.asList(excludedPortletsProp.split("\\|"));
                            } else {
                                excludedPortlets = new ArrayList<String>();
                            }
                        }

                        if (excludedPortlets.contains(window.getContent().getURI())) {


                            WindowRendition rendition = renderCmd.render(this.context);


                            this.finRequete(window, rendition);


                        } else {


                            ServiceThread thread = new ServiceThread(this, renderCmd, this.context, window, policyCtxId, subject, principal, credential,
                                    this.tracker, this.tracker.getInternalBean(), PageProperties.getProperties(), this.profiler);

                            // profiler.logEvent("THREAD_LAUNCH1", window.getId().toString(), 0, false);

                            Future<?> future = ThreadsPool.getInstance().execute(thread);

                            // profiler.logEvent("THREAD_LAUNCH2", window.getId().toString(), 0, false);

                            pageFutures.put(window.getId(), future);


                            nbThreads++;
                        }
                    }

                    this.nbWindows++;
                }
            }
        }


        logger.debug("nbThreads :" + nbThreads);

        // Boucle jusqu'à la fin ou + de 5s.
        long debut = System.currentTimeMillis();

        int timeout = DEFAULT_TIMEOUT_THREAD;
        String maxDelai = System.getProperty("portlets.maxExecutionDelay");
        if (maxDelai != null) {
            timeout = Integer.parseInt(maxDelai);
        }


        while ((this.estTermine() == false) && ((System.currentTimeMillis() - debut) < (timeout * 1000))) {
            ;
        }


        this.parallelisationExpired = true;


        logger.debug("fin timeout");

        // Gestion des time-out

        if (this.renditions.size() < this.windows.size()) {

            List<Window> timeOutWindows = new ArrayList<Window>();

            for (Window window : this.windows) {
                timeOutWindows.add(window);
            }

            for (Reponse reponse : this.renditions) {

                Window window = reponse.getWindow();
                if (timeOutWindows.contains(window)) {
                    timeOutWindows.remove(window);
                }
            }

            for (Object element : timeOutWindows) {
                Window timeOutWindow = (Window) element;

                RenderWindowCommand renderCmd = new RenderWindowCommand(this.pageNavigationalState, timeOutWindow.getId());


                //
                if (renderCmd != null) {
                    try {

                        // Stop the thread
                        KillerThread thread = new KillerThread(timeOutWindow.getId().toString(), pageFutures.get(timeOutWindow.getId()),
                                this.currentThreads.get(timeOutWindow.getId()));
                        KillersThreadsPool.getInstance().execute(thread);


                        // Affichage du message utilisateur
                        this.context.setAttribute(Scope.REQUEST_SCOPE, "osivia.timeout", "1");
                        WindowRendition rendition = renderCmd.render(this.context);
                        this.ajouterRequete(timeOutWindow, rendition);

                    } finally {
                        this.context.setAttribute(Scope.REQUEST_SCOPE, "osivia.timeout", null);
                    }
                }


                logger.error("timeout window " + timeOutWindow.getName());
            }
        }


        // Render the windows
        for (Reponse reponse : this.renditions) {

            Window window = reponse.getWindow();
            WindowRendition rendition = reponse.getRendition();

            // Parameterized renderset
            if (parameterizedRenderset != null) {
                rendition.getProperties().put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, parameterizedRenderset.toString());
                rendition.getProperties().put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, parameterizedRenderset.toString());
                rendition.getProperties().put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, parameterizedRenderset.toString());
            }


            // We ignore null result objects
            if (rendition != null) {
                // Get the controller response
                ControllerResponse responseWnd = rendition.getControllerResponse();

                // Null means we skip the window
                if (responseWnd != null) {
                    if (responseWnd instanceof MarkupResponse) {
                        // If this is a markup response we aggregate it
                        pageResult.addWindowContext(wcFactory.createWindowContext(window, rendition));
                    } else if (responseWnd != null) {
                        // Otherwise we return it
                        return responseWnd;
                    }
                }
            }
        }


        return new PageRendition(this.layout, this.theme, pageResult, this.pageService);
    }


    protected boolean hasParallelisationExpired() {
        return this.parallelisationExpired;
    }

    private synchronized void ajouterRequete(Window window, WindowRendition rendition) {
        this.renditions.add(new Reponse(window, rendition));
    }


    protected synchronized void finRequete(Window window, WindowRendition rendition) {
        if (!this.hasParallelisationExpired()) {
            this.renditions.add(new Reponse(window, rendition));
        }
    }


    private synchronized boolean estTermine() throws Exception {

        boolean res = (this.nbWindows == this.renditions.size());

        if (res == false) {
            // wait( 100);
            this.wait(10);
        }
        return res;

    }

    // Map<PortalObjectId, ServiceThread> currentThreads = new HashMap<PortalObjectId, ServiceThread>();
    // v 1.0.16
    Map<PortalObjectId, ServiceThread> currentThreads = new Hashtable<PortalObjectId, ServiceThread>();

    protected synchronized void registerThread(PortalObjectId id, ServiceThread serviceThread) {
        this.currentThreads.put(id, serviceThread);
    }

}
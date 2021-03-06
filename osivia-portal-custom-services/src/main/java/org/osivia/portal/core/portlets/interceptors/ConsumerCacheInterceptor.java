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

package org.osivia.portal.core.portlets.interceptors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.portlet.cache.CacheScope;
import org.jboss.portal.portlet.invocation.ActionInvocation;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.ResponseProperties;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.jboss.portal.portlet.spi.UserContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.mt.CacheEntry;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.DynamicPersistentWindow;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.tracker.ITracker;

/**
 * Cache markup on the portal.
 *
 */
public class ConsumerCacheInterceptor extends PortletInvokerInterceptor {

    private ITracker tracker;
    private org.osivia.portal.api.cache.services.ICacheService cacheService;

    public ITracker getTracker() {
        return this.tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }

    public ICacheService getServicesCacheService() {
        return this.cacheService;
    }

    public void setServicesCacheService(ICacheService cacheService) {
        this.cacheService = cacheService;
    }


    public static Map<String, CacheEntry> globalWindowCaches = new Hashtable<String, CacheEntry>();


    public static String computedCacheID(String cacheID, Window window, Map<String, String[]> publicNavigationalState) {

        String computedPath = "";


        // Si le cache est relatif, on préfixe par le path de l'espace de publication
        // ce qui permet de partager au sein d'un espace

        if (!(cacheID.charAt(0) == '/')) {

            String spacePath = window.getPage().getProperty("osivia.cms.basePath");

            if (spacePath != null) {
                computedPath += spacePath + "/";
            }
        }

        computedPath += cacheID;

        return computedPath;

    }


    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
        // Compute the cache key
        String scopeKey = "cached_markup." + invocation.getWindowContext().getId();


        // We use the principal scope to avoid security issues like a user loggedout seeing a cached entry
        // by a previous logged in user
        UserContext userContext = invocation.getUserContext();

        ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");


        // v2.0-SP1 : cache init on action
        if (invocation instanceof ActionInvocation) {
            userContext.setAttribute(scopeKey, null);


            // JSS 20130319 : shared cache initialization
            if (invocation.getWindowContext() instanceof WindowContextImpl) {
                String windowId = invocation.getWindowContext().getId();
                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
                Window window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
                String sharedCacheID = window.getDeclaredProperty("osivia.cacheID");

                if ((window != null) && (sharedCacheID != null)) {
                    Map<String, String[]> publicNavigationalState = invocation.getPublicNavigationalState();
                    sharedCacheID = computedCacheID(sharedCacheID, window, publicNavigationalState);
                    userContext.setAttribute("sharedcache." + sharedCacheID, null);
                }
            }
         }

        //
        if (invocation instanceof RenderInvocation) {


            // Affichage timeout
            if ((ctx != null) && "1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.timeout"))) {

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                pw.println("<p align=\"center\">");
                pw.println("	Délai expiré <br/> [<a href=\"javascript:location.reload(true)\">Recharger</a>]");
                pw.println("</p>");


                return new FragmentResponse(null, new HashMap<String, Object>(), "text/plain", null, sw.toString(), null, new CacheControl(0,
                        CacheScope.PRIVATE, null), null);
            }


            RenderInvocation renderInvocation = (RenderInvocation) invocation;

            String windowCreationPageMarker = null;

            // Correction JSS 06932012 v1.0.6 : test du type de contexte

            Window window = null;

            if (invocation.getWindowContext() instanceof WindowContextImpl) {

                // In case of dynamicWindows with same name,
                // we must make sure it is the same window
                // before serving cache

                String windowId = invocation.getWindowContext().getId();
                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);
                window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);


                if (window instanceof DynamicWindow) {
                    DynamicWindow dynaWIndow = ((DynamicWindow) window);
                    if (dynaWIndow.isSessionWindow()) {
                        windowCreationPageMarker = ((DynamicWindow) window).getDynamicWindowBean().getInitialPageMarker();
                    }
                }
            }


            //
            StateString navigationalState = renderInvocation.getNavigationalState();
            Map<String, String[]> publicNavigationalState = renderInvocation.getPublicNavigationalState();
            WindowState windowState = renderInvocation.getWindowState();
            Mode mode = renderInvocation.getMode();
            ParametersStateString additionnalState = null;

            if (window != null) {
                additionnalState = ContributionService.getWindowStatesMap(ctx, window.getId());
            }


            //
            CacheEntry cachedEntry = null;


            // v2.0.2 -JSS20130318
            // Shared user's cache
            // pour plus de cohérence, le cache partagé est priorisé par rapport au cache portlet

            boolean skipNavigationCheck = false;

            boolean sharedCache = false;

            if (window != null) {
                String sharedCacheID = window.getDeclaredProperty("osivia.cacheID");

                if (sharedCacheID != null) {

                    // On controle que l'état permet une lecture depuis le cache partagé

                    if (((navigationalState == null) || (((ParametersStateString) navigationalState).getSize() == 0))
                            && ((windowState == null) || WindowState.NORMAL.equals(windowState)) && ((mode == null) || Mode.VIEW.equals(mode))) {
                        sharedCacheID = computedCacheID(sharedCacheID, window, publicNavigationalState);
                        cachedEntry = (CacheEntry) userContext.getAttribute("sharedcache." + sharedCacheID);
                        if( cachedEntry != null) {
                            
                            // If space Data is refreshed, all shared cache relative to space are refreshed,
                            // even they are not in the page
                            // -> 'ex:quota'        

                            boolean isExpiring = false;
                            
                            String lastupdateKey = getLastRefreshSpaceDataKey(window);
                            if( lastupdateKey != null)  {
                                Long lastupdateTs = (Long) userContext.getAttribute(lastupdateKey);
                                if (lastupdateTs != null) {
                                    if (cachedEntry.creationTimeMillis < lastupdateTs)
                                        isExpiring = true;
                                }
                            }
                            
                            if( isExpiring == false) {
                                skipNavigationCheck = true;
                                sharedCache = true;
                            }
                        }
                    }
                }

                if (cachedEntry == null) {
                    cachedEntry = (CacheEntry) userContext.getAttribute(scopeKey);
                }

            }


            boolean globalCache = false;


            // v 1.0.13 : Cache anonyme sur la page d'accueil
            if ((cachedEntry == null) && ((ctx != null) && "1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.useGlobalWindowCaches")))) {
                cachedEntry = globalWindowCaches.get(invocation.getWindowContext().getId());
                globalCache = true;
            }


            // v2.0.8 : gestion des evenements de selection

            long selectionTs = -1;
            long sequencingTs = -1;

            if ((cachedEntry != null) && (window != null)) {
                if ("1".equals(window.getProperty("osivia.cacheDeactivation"))) {
                    cachedEntry = null;
                }
                
                if ((cachedEntry != null) &&"selection".equals(window.getProperty("osivia.cacheEvents"))) {
                    // Le cache est-il bien conforme à la selection
                    Long timestamp = (Long) ctx.getAttribute(Scope.PRINCIPAL_SCOPE, AttributesStorage.SELECTION.getTimestampAttributeName());
                    if (timestamp != null) {
                        selectionTs = timestamp.longValue();
                        if (cachedEntry.selectionTs != selectionTs) {
                            cachedEntry = null;
                        }
                    }
                }

                if ((cachedEntry != null) && StringUtils.isNotBlank(window.getProperty("osivia.sequence.priority"))) {
                	if (PageMarkerUtils.isCurrentPageMarker(ctx)) {
	                	Long timestamp = (Long) ctx.getAttribute(Scope.PRINCIPAL_SCOPE, AttributesStorage.PORTLET_SEQUENCING.getTimestampAttributeName());
	                    if (timestamp != null) {
	                        sequencingTs = timestamp.longValue();
	                        if (cachedEntry.sequencingTs != sequencingTs) {
	                            cachedEntry = null;
	                        }
	                    }
                	} else {
                		cachedEntry = null;
                	}
                }
            }


            // gestion des fermetures de popup

            if ((cachedEntry != null) && (window != null) && (ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosed") != null)) {
                // Test sur windowID

                String closedWindowID = (String) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosedWindowID");
                PortalObjectId closedWindowOjectID = PortalObjectId.parse(closedWindowID, PortalObjectPath.SAFEST_FORMAT);
                if (window.getId().equals(closedWindowOjectID)) {
                    cachedEntry = null;
                }
            }

            //
            if ((cachedEntry != null) && (skipNavigationCheck == false)) {
                // Check time validity for fragment
                boolean useEntry = false;
                StateString entryNavigationalState = cachedEntry.navigationalState;
                Map<String, String[]> entryPublicNavigationalState = cachedEntry.publicNavigationalState;

                // Then check nav state equality
                if (navigationalState == null) {
                    if (entryNavigationalState == null) {
                        useEntry = true;
                    } else if (entryNavigationalState instanceof ParametersStateString) {
                        // We consider a parameters state string empty equivalent to a null value
                        useEntry = ((ParametersStateString) entryNavigationalState).getSize() == 0;
                    }
                } else if (entryNavigationalState == null) {
                    if (navigationalState instanceof ParametersStateString) {
                        useEntry = ((ParametersStateString) navigationalState).getSize() == 0;
                    }
                } else {
                    useEntry = navigationalState.equals(entryNavigationalState);
                }

                // Check public nav state equality
                if (useEntry) {
                    if (publicNavigationalState == null) {
                        if (entryPublicNavigationalState == null) {
                            //
                        } else {
                            useEntry = entryPublicNavigationalState.size() == 0;
                        }
                    } else if (entryPublicNavigationalState == null) {
                        useEntry = publicNavigationalState.size() == 0;
                    } else {
                        ParameterMap publicPM = ParameterMap.wrap(publicNavigationalState);
                        ParameterMap entryPM = ParameterMap.wrap(entryPublicNavigationalState);
                        useEntry = publicPM.equals(entryPM);
                    }
                }


                // Check additional state
                // Then check nav state equality
                if (useEntry && (window != null)) {
                    if (additionnalState == null) {
                        if (cachedEntry.additionalState == null) {
                            useEntry = true;
                        } else if (cachedEntry.additionalState instanceof ParametersStateString) {
                            // We consider a parameters state string empty equivalent to a null value
                            useEntry = ((ParametersStateString) cachedEntry.additionalState).getSize() == 0;
                        }
                    } else if (cachedEntry.additionalState == null) {
                        if (additionnalState instanceof ParametersStateString) {
                            useEntry = additionnalState.getSize() == 0;
                        }
                    } else {
                        useEntry = additionnalState.equals(cachedEntry.additionalState);
                    }
                }


                if (useEntry) {
                    // Avoid dynamic windows with same name
                    if (windowCreationPageMarker != null) {
                        if (!windowCreationPageMarker.equals(cachedEntry.creationPageMarker)) {
                            useEntry = false;
                        }
                    }
                }

                // Then check window state equality
                useEntry &= windowState.equals(cachedEntry.windowState);

                // Then check mode equality
                useEntry &= mode.equals(cachedEntry.mode);

                // Clean if it is null
                if (!useEntry) {
                    cachedEntry = null;
                    userContext.setAttribute(scopeKey, null);
                }
            }


            boolean refresh = PageProperties.getProperties().isRefreshingPage();


            ContentResponse fragment = cachedEntry != null ? cachedEntry.contentRef.getContent() : null;

            // If no valid fragment we must invoke
            if ((fragment == null) || (cachedEntry.expirationTimeMillis < System.currentTimeMillis())
                    || (!this.getServicesCacheService().checkIfPortalParametersReloaded(cachedEntry.creationTimeMillis)) || refresh) {
                // Set validation token for revalidation only we have have a fragment
                if (fragment != null) {
                    renderInvocation.setValidationToken(cachedEntry.validationToken);
                }

                // Invoke
                PortletInvocationResponse response = super.invoke(invocation);

                // Try to cache any fragment result
                CacheControl control = null;
                if (response instanceof ContentResponse) {
                    fragment = (ContentResponse) response;
                    control = fragment.getCacheControl();
                } else if (response instanceof RevalidateMarkupResponse) {
                    RevalidateMarkupResponse revalidate = (RevalidateMarkupResponse) response;
                    control = revalidate.getCacheControl();
                }

                // Compute expiration time, i.e when it will expire
                long expirationTimeMillis = 0;
                String validationToken = null;
                if (control != null) {
                    if (control.getExpirationSecs() == -1) {
                        expirationTimeMillis = Long.MAX_VALUE;
                    } else if (control.getExpirationSecs() > 0) {
                        expirationTimeMillis = System.currentTimeMillis() + (control.getExpirationSecs() * 1000);
                    }
                    if (control.getValidationToken() != null) {
                        validationToken = control.getValidationToken();
                    } else if (cachedEntry != null) {
                        validationToken = cachedEntry.validationToken;
                    }
                }


                
                // Invalid Shared user's cache
				if (fragment instanceof FragmentResponse) {
					FragmentResponse orig = (FragmentResponse) fragment;
					if ("1".equals(orig.getAttributes().get("osivia.invalidateSharedCache")) && (window != null)
							&& (window.getDeclaredProperty("osivia.cacheID") != null)) {

						
						String sharedID = window.getDeclaredProperty("osivia.cacheID");
                        sharedID = computedCacheID(sharedID, window, publicNavigationalState);
						userContext.setAttribute("sharedcache." + sharedID, null);
						// No cache, even for current portlet
						expirationTimeMillis = 0;
					}
				}
 
                 
  

				
				
                // Cache if we can
                if (expirationTimeMillis > 0) {

                    ContentResponse cacheFragment = fragment;

                    if (fragment instanceof FragmentResponse) {


                        FragmentResponse orig = (FragmentResponse) fragment;

                        Map<String, Object> filterAttributes = new HashMap<String, Object>();

                        // Filtre des atttributs devant etre persistés dans le cache

                        filterAttributes.put("osivia.emptyResponse", orig.getAttributes().get("osivia.emptyResponse"));
                        filterAttributes.put(Constants.PORTLET_ATTR_MENU_BAR, orig.getAttributes().get(Constants.PORTLET_ATTR_MENU_BAR));
                        filterAttributes.put("osivia.portletPath", orig.getAttributes().get("osivia.portletPath"));
                        filterAttributes.put("osivia.popupCallbackUrl", orig.getAttributes().get("osivia.popupCallbackUrl"));
                        filterAttributes.put(Constants.REQUEST_ATTR_URI, orig.getAttributes().get(Constants.REQUEST_ATTR_URI));

                        cacheFragment = new FragmentResponse(orig.getProperties(), filterAttributes, orig.getContentType(), orig.getBytes(), orig.getChars(),
                                orig.getTitle(), orig.getCacheControl(), orig.getNextModes());
                    }


                    StateString cacheAdditionnalState = ParametersStateString.create(additionnalState);

                    CacheEntry cacheEntry = new CacheEntry(navigationalState, publicNavigationalState, windowState, cacheAdditionnalState, mode, cacheFragment,
                            expirationTimeMillis, validationToken, windowCreationPageMarker, selectionTs, sequencingTs, null);


                    userContext.setAttribute(scopeKey, cacheEntry);


                    // v2.0.2 -JSS20130318
                    // Shared user's cache
                    if ((expirationTimeMillis > 0) && (window != null) && (window.getDeclaredProperty("osivia.cacheID") != null)) {

                        String sharedID = window.getDeclaredProperty("osivia.cacheID");

                        // On controle que l'état permet une mise dans le cache global

                        if (((navigationalState == null) || (((ParametersStateString) navigationalState).getSize() == 0))
                                && ((windowState == null) || WindowState.NORMAL.equals(windowState)) && ((mode == null) || Mode.VIEW.equals(mode))) {
                            sharedID = computedCacheID(sharedID, window, publicNavigationalState);

                            CacheEntry sharedCacheEntry = new CacheEntry(null, null, null, null, null, fragment, expirationTimeMillis, null, null, selectionTs,
                                    sequencingTs, invocation.getWindowContext().getId());
                            userContext.setAttribute("sharedcache." + sharedID, sharedCacheEntry);
                        }
                    }


                    // For other users
                    if ("1".equals(ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.useGlobalWindowCaches"))) {

                        HttpServletRequest request = ctx.getServerInvocation().getServerContext().getClientRequest();

                        Boolean skipNavigationControls = "1".equals(ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.skipCacheNavigationControl"));

                        boolean storeCache = true;

                        if (!skipNavigationControls) {
                            storeCache = false;

                            // On controle que l'état permet une mise dans le cache global

                            if ((navigationalState == null) && ((publicNavigationalState == null) || (publicNavigationalState.size() == 0))
                                    && ((windowState == null) || WindowState.NORMAL.equals(windowState)) && ((mode == null) || Mode.VIEW.equals(mode))
                                    && ((window != null) && (window instanceof DynamicPersistentWindow))
                                    // Pas de cache sur les deconnexions
                                    && (request.getCookies() == null)) {
                                storeCache = true;
                            }
                        }


                        if (storeCache) {


                            CacheEntry initCacheEntry = new CacheEntry(navigationalState, publicNavigationalState, windowState, cacheAdditionnalState, mode,
                                    fragment, System.currentTimeMillis() + (30 * 1000), // 10 sec.
                                    null, null, selectionTs, sequencingTs, null);
                            // v2.0.2 -JSS20130318 - déja fait !!!
                            // userContext.setAttribute(scopeKey, cacheEntry);


                            globalWindowCaches.put(invocation.getWindowContext().getId(), initCacheEntry);
                        }
                    }


                }

                //
                return response;
            } else {
                // Use the cached fragment
                if (fragment instanceof FragmentResponse) {


                    FragmentResponse fr = (FragmentResponse) fragment;

                    String updatedFragment = fr.getChars();
                    ResponseProperties updateProperties = fr.getProperties();


                    boolean fragmentUpdated = false;

                    if (fr.getChars() != null) {
                        // Gestion du cache partagé

                        if (globalCache) {
                            HttpServletRequest request = ctx.getServerInvocation().getServerContext().getClientRequest();
                            if (request.getSession() != null) {
                                if (request.getCookies() == null) {
                                    // Premier affichage : on remplace le portalsessionid
                                    updatedFragment = updatedFragment.replaceAll(";portalsessionid=([a-zA-Z0-9.]*)", ";portalsessionid="
                                            + request.getSession().getId());
                                    fragmentUpdated = true;
                                } else {
                                    // Déconnexion : on onleve le portalsessionid
                                    updatedFragment = updatedFragment.replaceAll(";portalsessionid=([a-zA-Z0-9.]*)", "");
                                    fragmentUpdated = true;
                                }
                            }

                        }

                        if (sharedCache) {

                            // update navigation path

                            NavigationalStateContext nsContext = (NavigationalStateContext) ctx
                                    .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                            PageNavigationalState pageState = nsContext.getPageNavigationalState(window.getPage().getId().toString());

                            String navigationPath = "";
                            String contentPath = "";
                            String itemRelPath = "";

                            String sPath[] = null;
                            if (pageState != null) {
                                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
                                if ((sPath != null) && (sPath.length > 0)) {
                                    navigationPath = sPath[0];
                                }

                                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"));
                                if ((sPath != null) && (sPath.length > 0)) {
                                    contentPath = sPath[0];
                                }


                                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.itemRelPath"));
                                if ((sPath != null) && (sPath.length > 0)) {
                                    itemRelPath = sPath[0];
                                }
                            }


                            updatedFragment = updatedFragment.replaceAll("osivia.cms.path=([a-zA-Z0-9%\\-.]*)", "osivia.cms.path=" + navigationPath);
                            updatedFragment = updatedFragment.replaceAll("osivia.cms.contentPath=([a-zA-Z0-9%\\-.]*)", "osivia.cms.contentPath=" + contentPath);
                            updatedFragment = updatedFragment.replaceAll("osivia.cms.itemRelPath=([a-zA-Z0-9%\\-.]*)", "osivia.cms.itemRelPath=" + itemRelPath);

                            // JSS 2.0.22-RC6 : mise à jour des windows sur les caches partagés
                            updatedFragment = updatedFragment.replace(cachedEntry.originalWindowID + "?", invocation.getWindowContext().getId() + "?");
                            fragmentUpdated = true;


                        }

                        // Actualisation des markers de page

                        if (fr.getChars().indexOf("/pagemarker/") != -1) {
                            // String pageMarker = (String) ctx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");
                            String pageMarker = PageMarkerUtils.getCurrentPageMarker(ctx);

                            updatedFragment = updatedFragment.replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + pageMarker + "/");
                            fragmentUpdated = true;

                        }

                        if (fr.getChars().indexOf(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER + "=") != -1) {
                            String pageMarker = PageMarkerUtils.getCurrentPageMarker(ctx);

                            updatedFragment = updatedFragment.replaceAll(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER + "=([0-9]*)",
                                    InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER + "=" + pageMarker);
                            fragmentUpdated = true;
                        }


                        if (fragmentUpdated) {
                            return new FragmentResponse(updateProperties, fr.getAttributes(), fr.getContentType(), fr.getBytes(), updatedFragment,
                                    fr.getTitle(), fr.getCacheControl(), fr.getNextModes());
                        }

                    }
                }

                return fragment;
            }
        }
        /*
         * else
         * {
         * // Invalidate
         * userContext.setAttribute(scopeKey, null);
         *
         * // Invoke
         * return super.invoke(invocation);
         * }
         */

        return super.invoke(invocation);
    }

    public static String getLastRefreshSpaceDataKey(Window window) {
        String spacePath = window.getPage().getProperty("osivia.cms.basePath");
        String lastupdatePath = null;
        if( spacePath != null)
            lastupdatePath = "lastUpdateContent." + spacePath;
        return lastupdatePath;
    }


}

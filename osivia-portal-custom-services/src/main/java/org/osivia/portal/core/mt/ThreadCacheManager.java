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

package org.osivia.portal.core.mt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.osivia.portal.core.attributes.AttributesStorage;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portlets.interceptors.ConsumerCacheInterceptor;


/**
 * Cache markup on thread
 *
 * Permet d'éviter  la création d'un thread dans le cas où le cache est valide
 *
 */
public class ThreadCacheManager extends PortletInvokerInterceptor
{

	protected static boolean isPresumedCached( ControllerContext context,Map<String, String[]> pageNavigationalState, Window window, WindowNavigationalState wsState)	{

		// Desactivation du multi-thread
//		if (true)
//			return true;

		if(PageProperties.getProperties().isRefreshingPage()) {
            return false;
        }


		// Caches portlets

		String scopeKey = "cached_markup." + window.getId();

		AttributeResolver resolver = context.getAttributeResolver(Scope.PRINCIPAL_SCOPE);



		// JSS 20130319 : caches partages
        // pour plus de cohérence, le cache partagé est priorisé par rapport au cache portlet


			String sharedCacheID = window.getDeclaredProperty("osivia.cacheID");

			if ((window != null) && (sharedCacheID != null)) {

				// On controle que l'état permet une lecture depuis le cache
				// partagé : pas d'état

				if ((wsState == null) || (wsState.getContentState() == null) || (((ParametersStateString) wsState.getContentState())
						.getSize() == 0)) {
					sharedCacheID = ConsumerCacheInterceptor.computedCacheID(sharedCacheID, window, pageNavigationalState);
					CacheEntry cachedEntry =  (CacheEntry) resolver.getAttribute("sharedcache." + sharedCacheID);
					if( cachedEntry != null){
						if (cachedEntry.expirationTimeMillis > (System.currentTimeMillis() + 10)) {
                            return true;
                        }

					}
				}
			}










		CacheEntry cachedEntry = (CacheEntry) resolver.getAttribute(scopeKey);



	     // v2.0.8 : gestion des evenements de selection

		if ((cachedEntry != null) && (window != null)) {
            if ("selection".equals(window.getProperty("osivia.cacheEvents"))) {
                Long timestamp = (Long) context.getAttribute(Scope.PRINCIPAL_SCOPE, AttributesStorage.SELECTION.getTimestampAttributeName());
                if ((timestamp != null) && (cachedEntry.creationTimeMillis < timestamp.longValue())) {
                    cachedEntry = null;
				}
			}

            if ((cachedEntry != null) && StringUtils.isNotBlank(window.getProperty("osivia.sequence.priority"))) {
            	if (PageMarkerUtils.isCurrentPageMarker(context)) {
	            	Long timestamp = (Long) context.getAttribute(Scope.PRINCIPAL_SCOPE, AttributesStorage.PORTLET_SEQUENCING.getTimestampAttributeName());
	                if ((timestamp != null) && (cachedEntry.creationTimeMillis < timestamp.longValue())) {
	                    cachedEntry = null;
	                }
            	} else {
            		cachedEntry = null;
            	}
            }
		}



		if (cachedEntry != null) {



			   // Controle des parametres publics de la page
			   // Si ils ont été modifiés on présume que le cache est invalide

		       Map<String, String[]> entryNavigationalState = cachedEntry.publicNavigationalState;


     			// Si le path CMS de la page est modifié, ne pas rendre dans le cache
           	    // TODO : il faudrait généraliser à tous les parametres publics, mais il faudrait connaitre
           	    // le nom associé au paramètre public dans le portlet

		       String entrycmsPath = null;
		       if( entryNavigationalState != null)	{
		    	   String[] state =  entryNavigationalState.get("osivia.cms.path");
		    	   if( (state != null) && (state.length == 1)) {
                    entrycmsPath = state [0];
                }
		       }


		       String pagecmsPath = null;
		       if( pageNavigationalState != null)	{
		    	    String[] state =  pageNavigationalState.get("osivia.cms.path");
		    	    if( (state != null) && (state.length == 1)) {
                        pagecmsPath = state [0];
                    }
				}


		       boolean useEntry = false;

	           // Then check nav state equality
	            if (pagecmsPath == null)
	            {
	               if (entrycmsPath == null) {
                    useEntry = true;
                }
	            } else {
                    useEntry =  pagecmsPath.equals(entrycmsPath);
                }


		       if( useEntry == false) {
                return false;
            }





			// on réserve 10 ms
			if (cachedEntry.expirationTimeMillis > (System.currentTimeMillis() + 10)) {
                return true;
            }
		}

		// Accès anonyme à la page d'accueil
		if ("1".equals(context.getAttribute(Scope.REQUEST_SCOPE, "osivia.useGlobalWindowCaches"))) {
			CacheEntry globalCacheEntry = ConsumerCacheInterceptor.globalWindowCaches.get(window.getId());
			if (globalCacheEntry != null) {
                if (globalCacheEntry.expirationTimeMillis > (System.currentTimeMillis() + 10)) {
                    return true;
                }
            }
		}

		return false;
	}



}

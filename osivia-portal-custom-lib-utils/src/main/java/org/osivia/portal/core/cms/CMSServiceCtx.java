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

import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.context.ControllerContextAdapter;

/**
 * CMS service context.
 */
public class CMSServiceCtx {


	private ControllerContext controllerContext;
	private ServerInvocation serverInvocation;
	private String scope;
	/**
	 * Variable indiquant si le résultat de la commande
	 * effectuée avec ce contexte doit être mise à jour
	 * en cache de façon asynchrone.
	 */
	private boolean isAsyncCacheRefreshing = false;
	private String displayLiveVersion;
	private String hideMetaDatas;
	private String displayContext;
	private String contextualizationBasePath;

    private String creationType;
    private String creationPath;

    private String forcedLivePath;
    
    private String satelliteName;


    /**
	 * @return the satelliteName
	 */
	public String getSatelliteName() {
		return satelliteName;
	}


	/**
	 * @param satelliteName the satelliteName to set
	 */
	public void setSatelliteName(String satelliteName) {
		this.satelliteName = satelliteName;
	}


	/**
     * Constructor.
     */
    public CMSServiceCtx() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.contextualizationBasePath == null) ? 0 : this.contextualizationBasePath.hashCode());
        result = (prime * result) + ((this.creationPath == null) ? 0 : this.creationPath.hashCode());
        result = (prime * result) + ((this.creationType == null) ? 0 : this.creationType.hashCode());
        result = (prime * result) + ((this.displayContext == null) ? 0 : this.displayContext.hashCode());
        result = (prime * result) + ((this.displayLiveVersion == null) ? 0 : this.displayLiveVersion.hashCode());
        result = (prime * result) + ((this.forcePublicationInfosScope == null) ? 0 : this.forcePublicationInfosScope.hashCode());
        result = (prime * result) + ((this.forcedLivePath == null) ? 0 : this.forcedLivePath.hashCode());
        result = (prime * result) + ((this.hideMetaDatas == null) ? 0 : this.hideMetaDatas.hashCode());
        result = (prime * result) + (this.isAsyncCacheRefreshing ? 1231 : 1237);
        result = (prime * result) + ((this.scope == null) ? 0 : this.scope.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CMSServiceCtx other = (CMSServiceCtx) obj;
        if (this.contextualizationBasePath == null) {
            if (other.contextualizationBasePath != null) {
                return false;
            }
        } else if (!this.contextualizationBasePath.equals(other.contextualizationBasePath)) {
            return false;
        }
        if (this.creationPath == null) {
            if (other.creationPath != null) {
                return false;
            }
        } else if (!this.creationPath.equals(other.creationPath)) {
            return false;
        }
        if (this.creationType == null) {
            if (other.creationType != null) {
                return false;
            }
        } else if (!this.creationType.equals(other.creationType)) {
            return false;
        }
        if (this.displayContext == null) {
            if (other.displayContext != null) {
                return false;
            }
        } else if (!this.displayContext.equals(other.displayContext)) {
            return false;
        }
        if (this.displayLiveVersion == null) {
            if (other.displayLiveVersion != null) {
                return false;
            }
        } else if (!this.displayLiveVersion.equals(other.displayLiveVersion)) {
            return false;
        }
        if (this.forcePublicationInfosScope == null) {
            if (other.forcePublicationInfosScope != null) {
                return false;
            }
        } else if (!this.forcePublicationInfosScope.equals(other.forcePublicationInfosScope)) {
            return false;
        }
        if (this.forcedLivePath == null) {
            if (other.forcedLivePath != null) {
                return false;
            }
        } else if (!this.forcedLivePath.equals(other.forcedLivePath)) {
            return false;
        }
        if (this.hideMetaDatas == null) {
            if (other.hideMetaDatas != null) {
                return false;
            }
        } else if (!this.hideMetaDatas.equals(other.hideMetaDatas)) {
            return false;
        }
        if (this.isAsyncCacheRefreshing != other.isAsyncCacheRefreshing) {
            return false;
        }
        if (this.scope == null) {
            if (other.scope != null) {
                return false;
            }
        } else if (!this.scope.equals(other.scope)) {
            return false;
        }
        return true;
    }

    public String getForcedLivePath() {
        return this.forcedLivePath;
    }

    public void setForcedLivePath(String forcedLivePath) {
        this.forcedLivePath = forcedLivePath;
    }


    public String getCreationPath() {
        return this.creationPath;
    }



    public void setCreationPath(String creationPath) {
        this.creationPath = creationPath;
    }


    public String getCreationType() {
        return this.creationType;
    }


    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    private PortletRequest request;
    private HttpServletRequest servletRequest;

    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }




    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    private PortletContext portletCtx;
    private MimeResponse response;
	private String pageId;
	private Object doc;


    private boolean streamingSupport = false;


    public boolean isStreamingSupport() {
        return this.streamingSupport;
    }


    public void setStreamingSupport(boolean streamingSupport) {
        this.streamingSupport = streamingSupport;
    }

	/** if 'true', indicate to don't access the cache. load the latest data */
	private boolean forceReload = false;

	/**
	 * @return the forceReload
	 */
	public boolean isForceReload() {
		return this.forceReload;
	}

	/**
	 * @param forceReload the forceReload to set
	 */
	public void setForceReload(boolean forceReload) {
		this.forceReload = forceReload;
	}

	/**
	 * Variable permettant de forcer le scope
	 * de mise en cache de l'objet de retour
	 * de la méthode getPublicationInfos (dans CMSService)
	 */
	private String forcePublicationInfosScope;


    /**
     * Set portal controller context to initialized controller context, server invocation, request, response and portlet context.
     *
     * @param portalControllerContext portal controller context
     */
    public void setPortalControllerContext(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        this.setControllerContext(controllerContext);
        // Request
        this.setRequest(portalControllerContext.getRequest());
        // Response
        if (portalControllerContext.getResponse() instanceof MimeResponse) {
            MimeResponse response = (MimeResponse) portalControllerContext.getResponse();
            this.setResponse(response);
        }
        // Portlet context
        this.setPortletCtx(portalControllerContext.getPortletCtx());
    }


	public ControllerContext getControllerContext() {
		return this.controllerContext;
	}


	public void setControllerContext(ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
		if(controllerContext != null) {
			this.serverInvocation = controllerContext.getServerInvocation();
			if (this.serverInvocation != null) {
				this.servletRequest = this.serverInvocation.getServerContext().getClientRequest();
			}
		}
	}



	public ServerInvocation getServerInvocation() {
		return this.serverInvocation;
	}

	public void setServerInvocation(ServerInvocation invocation) {
		this.serverInvocation = invocation;
	}

	public String getContextualizationBasePath() {
		return this.contextualizationBasePath;
	}

	public void setContextualizationBasePath(String contextualizationBasePath) {
		this.contextualizationBasePath = contextualizationBasePath;
	}


	public String getDisplayContext() {
		return this.displayContext;
	}

	public void setDisplayContext(String displayContext) {
		this.displayContext = displayContext;
	}
	public PortletContext getPortletCtx() {
		return this.portletCtx;
	}

	public void setPortletCtx(PortletContext portletCtx) {
		this.portletCtx = portletCtx;
	}


	public String getDisplayLiveVersion() {
		return this.displayLiveVersion;
	}

	public void setDisplayLiveVersion(String displayLiveVersion) {
		this.displayLiveVersion = displayLiveVersion;
	}

	public PortletRequest getRequest() {
		return this.request;
	}

	public void setRequest(PortletRequest request) {
		this.request = request;
	}

    public MimeResponse getResponse() {
		return this.response;
	}

    public void setResponse(MimeResponse response) {
		this.response = response;
	}

	public String getPageId() {
		return this.pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public Object getDoc() {
		return this.doc;
	}

	public void setDoc(Object doc) {
		this.doc = doc;
	}

	public String getHideMetaDatas() {
		return this.hideMetaDatas;
	}

	public void setHideMetaDatas(String hideMetaDatas) {
		this.hideMetaDatas = hideMetaDatas;
	}

	public String getScope() {
		return this.scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getForcePublicationInfosScope() {
		return this.forcePublicationInfosScope;
	}

	public void setForcePublicationInfosScope(String forcePublicationInfosScope) {
		this.forcePublicationInfosScope = forcePublicationInfosScope;
	}

	public boolean isAsyncCacheRefreshing() {
		return this.isAsyncCacheRefreshing;
	}

	public void setAsyncCacheRefreshing(boolean isAsyncCacheRefreshing) {
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}

}

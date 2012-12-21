package org.osivia.portal.core.cms.spi;

import java.util.List;

import javax.servlet.http.HttpSessionEvent;

import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSServiceCtx;



public interface ICMSIntegration {

	public void sessionDestroyed(HttpSessionEvent sessionEvent);

	public List<CMSPage> computeUserPreloadedPages(CMSServiceCtx cmsCtx)  throws Exception ;

}

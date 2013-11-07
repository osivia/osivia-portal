package org.osivia.portal.core.mt;

import java.util.Collection;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;

public interface IMultithreadService {
	   public ControllerResponse execute(Page page, ControllerContext context, Collection windows,  PortalLayout layout,PortalTheme theme,PageService pageService, ControllerPageNavigationalState pageNavigationalState) throws Exception;
}

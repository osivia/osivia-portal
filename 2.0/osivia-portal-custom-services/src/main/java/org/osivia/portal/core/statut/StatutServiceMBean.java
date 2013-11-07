/**
 * 
 */
package org.osivia.portal.core.statut;

import org.jboss.system.ServiceMBean;
import org.osivia.portal.api.statut.IStatutService;





/**
 * @author jss
 *
 */
public interface StatutServiceMBean extends ServiceMBean,IStatutService {

	public void startService()throws Exception;
	
	public void stopService()throws Exception;
}

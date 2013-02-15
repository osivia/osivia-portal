package org.osivia.portal.core.deploiement;

import java.io.File;

import javax.management.MBeanServer;

public interface IParametresPortailDeploymentManager {
	
	public void chargerParametres(File file, MBeanServer mbeanServer);

}

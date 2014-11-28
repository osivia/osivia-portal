package org.osivia.portal.api.deployment;

import java.util.Hashtable;
import java.util.Map;




/**
 * Permet de modifier à partir d'un portlet les paramètres de déploiement
 * 
 * @author Jean-Sébastien Steux
 *
 */
public class DeploymentContext {
    
    public static ThreadLocal<Hashtable<String, String>> ctx = new ThreadLocal<Hashtable<String, String>>();

    public static Hashtable<String, String> getContext()    {
        if( ctx.get() == null)  {
            ctx.set(new Hashtable<String, String>());
        }
    return ctx.get();
    }

}

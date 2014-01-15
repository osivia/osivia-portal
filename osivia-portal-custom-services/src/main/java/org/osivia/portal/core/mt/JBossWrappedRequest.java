package org.osivia.portal.core.mt;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.jboss.portal.portlet.invocation.PortletInvocation;

/**
 * Cette requete est spécifique à chaque thread.
 * 
 * Les attributs sont stockés dans cette requete et pas dans la requete parent
 */

public class JBossWrappedRequest extends HttpServletRequestWrapper {

    Object invocation = null;

    Map<String, Object> attributes = new HashMap<String, Object>();
    
    Object synchronizerBean = null;

    public JBossWrappedRequest(HttpServletRequest req, Object synchronizerBean) {
        super(req);
        this.synchronizerBean = synchronizerBean;

    }


    @Override
    public Object getAttribute(String name) {
        Object o = getInternalAttribute(name);

        /*
         * String value = "NULL";
         * if( o!= null)
         * value= o.toString();
         * 
         * System.out.println("JBossWrappedRequest getAttribute("+name+") = [" + value+"]");
         */

        return o;
    }


    public Object getInternalAttribute(String name) {

        Object att = attributes.get(name);
        if (att == null) {

            if (synchronizerBean != null) {
                synchronized (synchronizerBean) {
                    att = super.getAttribute(name);
                }
            } else
                // Mode mono-thread
                return super.getAttribute(name);
        }

        return (att);
    }


    @Override
    public void removeAttribute(String name) {
        // System.out.println("JBossWrappedRequest removeAttribute("+name+")" );
        // if( attributes.get(name) != null)
        if( attributes.get(name) != null)
            attributes.remove(name);
        else    {
             if (synchronizerBean != null) {
                synchronized (synchronizerBean) {
                    super.removeAttribute(name);
                }
            } else
                // Mode mono-thread
                super.removeAttribute(name);
        }
        // super.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        /*
         * String value = "NULL";
         * if( o!= null)
         * value= o.toString();
         * System.out.println("JBossWrappedRequest setAttribute("+name+") : " + value);
         */

        attributes.put(name, o);
    }

    // Cette méthode n'est pas thread safe

    public String getParameter(String name) {

//        Object synchronizerBean = getAttribute(ContextDispatcherWrapperInterceptor.REQ_SYNCHRONIZER);
        if (synchronizerBean != null) {
            synchronized (synchronizerBean) {
                return super.getParameter(name);
            }

        } else {
            // Mode mono-thread
            return super.getParameter(name);
        }
    }


}

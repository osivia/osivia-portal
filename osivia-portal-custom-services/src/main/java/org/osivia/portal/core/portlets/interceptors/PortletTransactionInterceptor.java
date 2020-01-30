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

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.ActionInvocation;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.transaction.ITransactionService;


/**
 * Close pending transactions
 *
 * @see PortletInvokerInterceptor
 */
public class PortletTransactionInterceptor extends PortletInvokerInterceptor {
    
    /** Menubar service. */
    private ITransactionService transactionService;

 
    /**
     * Constructor.
     */
    public PortletTransactionInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
        PortletInvocationResponse response;
        boolean implicitTransactions = false;
        
        try { 
            if( "debug".equals(System.getProperty("osivia.transactionMode")) && (invocation instanceof ActionInvocation))    {
                ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");
                
                String windowId = invocation.getWindowContext().getId();
                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

                Window window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
                
                if( ! "false".equals(window.getDeclaredProperty("osivia.transaction.implicitCommits"))) { 
                    implicitTransactions = true;
                }
            }
            
            if( implicitTransactions)
                transactionService.begin();
            
            response = super.invoke(invocation);
            
            if( implicitTransactions)   {
                if( transactionService.isStarted())
                    transactionService.commit();
            }
                           
        
        } 
        catch(Exception e)  {
            if( implicitTransactions)   {
                if( transactionService.isStarted())
                    transactionService.rollback();
            }
            throw e;
        }
        
        finally   {
             transactionService.cleanTransactionContext();
        }

        return response;
    }
    
    /**
     * Setter for transactionService.
     * @param transactionService the transactionService to set
     */
    public void setTransactionService(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

}

/*
 * (C) Copyright 2015 OSIVIA (http://www.osivia.com) 
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
package org.osivia.portal.api.ecm;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;


/**
 * Describe a command executed by the ECM
 * @author lbillon
 *
 */
public abstract class EcmCommand  {

	
	public enum ReloadAfterCommandStrategy {
		/** do nothing after the command- Not implemented ! */
		nothing(EMPTY),
		
		/** refresh page */
		refreshPage(EMPTY),
		
		/** refresh page and all navigation tree */
		refreshNavigation(EMPTY),
		
		/** redirect to parent and refresh navigation tree. Not implemented !  */
		moveToParent(EMPTY),
		
		/** redirect to child and refresh navigation tree. Not implemented !  */
		moveToChild(EMPTY);
		
		/** Redirection path after command. */
		private String redirectionPath;
		
		/** Contructor. */
		private ReloadAfterCommandStrategy(String redirectionPath){
		    this.redirectionPath = redirectionPath;
		}

        /** Getter for redirection path after command. */
		public String getRedirectionPathPath(){
		    return this.redirectionPath;
		}
		
		/** Setter for redirection path after command. */
		public void setRedirectionPathPath(String redirectionPath){
		    this.redirectionPath = redirectionPath;
		}
		
	}
	
	/** the command ID, can be an EcmCommonCommand */
	private final String commandName;
	
	/** the strategy of reloading. @see EcmCommand.ReloadAfterCommandStrategy */
	private final ReloadAfterCommandStrategy strategy;

	/** the concrete command called on the ECM */
	private final String realCommand;
	
	/** Additional static parameters for the command */
	private final Map<String, Object> realCommandParameters;

	/**
	 * @param commandName
	 * @param strategy
	 * @param realCommand
	 * @param realCommandParameters
	 */
	public EcmCommand(String commandName, ReloadAfterCommandStrategy strategy,
			String realCommand, Map<String, Object> realCommandParameters) {

		this.commandName = commandName;
		this.strategy = strategy;
		this.realCommand = realCommand;
		this.realCommandParameters = realCommandParameters;
	}
	
	/**
	 * @return the commandName
	 */
	public String getCommandName() {
		return commandName;
	}



	/**
	 * @return the strategy
	 */
	public ReloadAfterCommandStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @return the realCommand
	 */
	public String getRealCommand() {
		return realCommand;
	}

	/**
	 * @return the realCommandParameters
	 */
	public Map<String, Object> getRealCommandParameters() {
		return realCommandParameters;
	}

    public abstract void notifyAfterCommand(ControllerContext controllerContext);

	
	public EcmCommonCommands getCommonCommandName() {
		return EcmCommonCommands.valueOf(commandName);
	}
	
	

}

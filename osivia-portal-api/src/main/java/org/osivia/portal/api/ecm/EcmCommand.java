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

import java.util.Map;

import org.jboss.portal.core.controller.ControllerContext;


/**
 * Describe a command executed by the ECM.
 * 
 * @author lbillon
 */
public abstract class EcmCommand  {

    /** ECM command redirection path attribute name. */
    public static final String REDIRECTION_PATH_ATTRIBUTE = "osivia.ecm.command.redirectionPath";

	
    /**
     * Command callback reload strategies enumeration.
     * 
     * @author Cédric Krommenhoek
     */
	public enum ReloadAfterCommandStrategy {

        /** Do nothing after the command. Not implemented ! */
        NOTHING,
        /** Refresh page. */
        REFRESH_PAGE,
        /** Refresh page and all navigation tree. */
        REFRESH_NAVIGATION,
		/** redirect to parent and refresh navigation tree. Not implemented !  */
        MOVE_TO_PARENT,
		/** redirect to child and refresh navigation tree. Not implemented !  */
        MOVE_TO_CHILD;
		
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

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
package org.osivia.portal.core.ecm;

import java.util.HashMap;
import java.util.Map;

import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.ecm.IEcmCommandervice;

/**
 * The registry of ECM commands
 * @author lbillon
 *
 */
public class EcmCommandService implements IEcmCommandervice {


    /** Liste des commandes disponibles */
    private Map<String, EcmCommand> commands = new HashMap<String, EcmCommand>();


    /**
     * Add a command in the registry
     * @param name
     * @param command
     */
    public void addCommand(EcmCommonCommands name, EcmCommand command) {
    	this.commands.put(name.name(), command);
    }
    
    /**
     * Add a command in the registry
     *
     */
    protected void addExtraCommand(String name, EcmCommand command) {
        this.commands.put(name, command);
    }
    
    /**
     * Get command
     */
    public EcmCommand getCommand(EcmCommonCommands name) {
        return this.commands.get(name);
    }
    
    /**
     * Get command
     */
    public EcmCommand getCommand(String name) {
        return this.commands.get(name);
    }

	/* (non-Javadoc)
	 * @see org.osivia.portal.api.ecm.IEcmCommandervice#getAllCommands()
	 */
	public Map<String, EcmCommand> getAllCommands() {
		return commands;
	}
	
	/* (non-Javadoc)
	 * @see org.osivia.portal.api.ecm.IEcmCommandervice#registerCommand(org.osivia.portal.api.ecm.EcmCommand)
	 */
	public void registerCommand(String key, EcmCommand command) {
		commands.put(key, command);
	}




}

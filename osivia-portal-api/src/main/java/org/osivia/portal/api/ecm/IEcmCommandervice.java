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


/**
 * This service register all known commands of the ECM
 * @author lbillon
 *
 */
public interface IEcmCommandervice {

    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=EcmCommandService";

	/**
	 * Getter
	 * @param name
	 * @return
	 */
	EcmCommand getCommand(EcmCommonCommands name);

	/**
	 * Getter
	 * @param name
	 * @return
	 */
	EcmCommand getCommand(String name);
    
	/**
	 * List all commands
	 * @return
	 */
    Map<String, EcmCommand> getAllCommands();
    
    
    /**
     * Register a new command
     * @param command
     */
    void registerCommand(String key, EcmCommand command);
}

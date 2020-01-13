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
 *
 */
package org.osivia.portal.api.tasks;

import java.util.Map;
import java.util.Set;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;

/**
 * Custom task provider interface
 *
 * @author Jean-Sébastien Steux
 */
public interface ITasksProvider {

    /** Regions attributes bundles customizer identifier. */
    static final String CUSTOMIZER_ID = "osivia.customizer.taskProvider.id";
    /** Regions attributes bundles customizer name attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_TASKS_LIST = "osivia.customizer.taskProvider.tasks";



}

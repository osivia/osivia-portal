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
package org.osivia.portal.api.ecm;

/**
 * Main command views on the ecm
 * @author lbi
 *
 */
public enum EcmViews {

    /** summary view of a doc */
    viewSummary,

    /** create a document */
    createDocument,

    /** create a webpage (mode webpage) */
    createPage,

    /** edit a document */
    editDocument,

    /** edit a webpage (mode webpage) */
    editPage,

    /** edit a page's attachments */
    editAttachments,

    /** create a fragment in the top of the region (mode webpage) */
    createFgtInRegion,

    /** create a fragment below an other window (mode webpage) */
    createFgtBelowWindow,

    /** edit a fragment (mode webpage) */
    editFgt,

    /** Go to the current media library in back office */
    gotoMediaLibrary,

    /** Send a document to poeple inteerested in */
    shareDocument,

    /** Start a validation workflow. */
    startValidationWf,

    /** See current validation workflow task. */
    followWfValidation,

    /** Remote publishing. */
    remotePublishing,

    /** Remote Publishing validation. */
    validateRemotePublishing,

    /** Global administration */
    globalAdministration;

}

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
package org.osivia.portal.core.cms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * Service optimisé pour renvoyer toutes les informations de contenu liées à la
 * publication
 * 
 * @author jeanseb
 * 
 */
/**
 * @author David Chevrier
 */
public class CMSPublicationInfos {

	public static final int ERROR_CONTENT_NOT_FOUND = 1;
	public static final int ERROR_CONTENT_FORBIDDEN = 2;
	public static final int ERROR_PUBLISH_SPACE_NOT_FOUND = 3;
	public static final int ERROR_PUBLISH_SPACE_FORBIDDEN = 4;
	public static final int ERROR_WORKSPACE_NOT_FOUND = 5;
	public static final int ERROR_WORKSPACE_FORBIDDEN = 6;

	private String documentPath = null;
	private String liveId = null;
	private boolean editableByUser = false;
	private boolean manageableByUser = false;
	private boolean deletableByUser = false;
	private boolean userCanValidate = false;
	private boolean anonymouslyReadable = false;
	/** Indicates if document is remote publishable. */
	private boolean isRemotePublishable = false;
	/** Indicates if document is already published. */
	private boolean isRemotePublished = false;	
	/** Published CMS item indicator. */
    private boolean published = false;
	/** Indicates if working version is different from published version. */
    private boolean beingModified;
    
	private boolean commentableByUser;

	private String spaceID = null;
	private String parentSpaceID = null;
	
	private Map<String, String> subTypes;

	private String publishSpaceType = null;
	private String publishSpacePath = null;
	private String publishSpaceDisplayName = null;

	private boolean isLiveSpace = false;
	
	/** Indicates if document is draft. */
	private boolean isDraft = false;
	/** Indicates if document has draft. */
	private boolean hasDraft = false;
	/** 
	 * Indicates if document (which is draft) has "published" document
	 * (in collaborative space way).
	 */
	private boolean isNotOrphanDraft = true;
	/** Draft path. */
	private String draftPath;
	/** Path of draft (portal) contextualization. */
	private String draftContextualizationPath;
	
	private List<Integer> errorCodes = new ArrayList<Integer>();
	
	/** Informations as flux. */
	protected JSONObject flux;

	public CMSPublicationInfos() {
		super();
	}

	public boolean isLiveSpace() {
		return isLiveSpace;
	}

	public void setLiveSpace(boolean isLiveSpace) {
		this.isLiveSpace = isLiveSpace;
	}

	public String getPublishSpacePath() {
		return publishSpacePath;
	}

	public void setPublishSpacePath(String publishSpacePath) {
		this.publishSpacePath = publishSpacePath;
	}

	public String getPublishSpaceDisplayName() {
		return publishSpaceDisplayName;
	}

	public void setPublishSpaceDisplayName(String publishSpaceDisplayName) {
		this.publishSpaceDisplayName = publishSpaceDisplayName;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getPublishSpaceType() {
		return publishSpaceType;
	}

	public void setPublishSpaceType(String publishSpaceType) {
		this.publishSpaceType = publishSpaceType;
	}

	public boolean isEditableByUser() {
		return editableByUser;
	}

	public void setEditableByUser(boolean editableByUser) {
		this.editableByUser = editableByUser;
	}
	
	

	/**
	 * @return the manageableByUser
	 */
	public boolean isManageableByUser() {
		return manageableByUser;
	}

	/**
	 * @param manageableByUser the manageableByUser to set
	 */
	public void setManageableByUser(boolean manageableByUser) {
		this.manageableByUser = manageableByUser;
	}

	public boolean isDeletableByUser() {
		return deletableByUser;
	}

	public void setDeletableByUser(boolean deletableByUser) {
		this.deletableByUser = deletableByUser;
	}
	
    public boolean isUserCanValidate() {
        return userCanValidate;
    }

    public void setUserCanValidate(boolean userCanValidate) {
        this.userCanValidate = userCanValidate;
    }

    public boolean isAnonymouslyReadable() {
		return anonymouslyReadable;
	}

	public void setAnonymouslyReadable(boolean anonymouslyReadable) {
		this.anonymouslyReadable = anonymouslyReadable;
	}
    
    /**
     * @return the isRemotePublishable
     */
    public boolean isRemotePublishable() {
        return isRemotePublishable;
    }

    /**
     * @param isRemotePublishable the isRemotePublishable to set
     */
    public void setRemotePublishable(boolean isRemotePublishable) {
        this.isRemotePublishable = isRemotePublishable;
    }
    
    /**
     * 
     * @return isRemotePublished
     */
    public boolean isRemotePublished() {
		return isRemotePublished;
	}

    /**
     * 
     * @param isRemotePublished
     */
	public void setRemotePublished(boolean isRemotePublished) {
		this.isRemotePublished = isRemotePublished;
	}

	/**
     * Getter for published.
     * @return the published
     */
    public boolean isPublished() {
        return published;
    }

    /**
     * Setter for published.
     * @param published the published to set
     */
    public void setPublished(boolean published) {
        this.published = published;
    }

    /**
     * Getter for beingModified.
     * @return the beingModified
     */
    public boolean isBeingModified() {
        return beingModified;
    }

    /**
     * Setter for beingModified.
     * @param beingModified the beingModified to set
     */
    public void setBeingModified(boolean beingModified) {
        this.beingModified = beingModified;
    }

    public boolean isCommentableByUser() {
		return commentableByUser;
	}

	public void setCommentableByUser(boolean commentableByUser) {
		this.commentableByUser = commentableByUser;
	}

	public String getSpaceID() {
		return spaceID;
	}

	public void setSpaceID(String spaceID) {
		this.spaceID = spaceID;
	}

	public String getParentSpaceID() {
		return parentSpaceID;
	}

	public void setParentSpaceID(String parentSpaceID) {
		this.parentSpaceID = parentSpaceID;
	}
	
	public Map<String, String> getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(Map<String, String> subTypes) {
		this.subTypes = subTypes;
	}

	public List<Integer> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<Integer> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public String getLiveId() {
		return liveId;
	}

	public void setLiveId(String liveId) {
		this.liveId = liveId;
	}

	/**
	 * @return the isDraft
	 */
	public boolean isDraft() {
		return isDraft;
	}

	/**
	 * @param isDraft the isDraft to set
	 */
	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

    
    /**
     * @return the draftPath
     */
    public String getDraftPath() {
        return draftPath;
    }

    
    /**
     * @param draftPath the draftPath to set
     */
    public void setDraftPath(String draftPath) {
        this.draftPath = draftPath;
    }

    
    /**
     * @return the draftContextualizationPath
     */
    public String getDraftContextualizationPath() {
        return draftContextualizationPath;
    }

    
    /**
     * @param draftContextualizationPath the draftContextualizationPath to set
     */
    public void setDraftContextualizationPath(String draftContextualizationPath) {
        this.draftContextualizationPath = draftContextualizationPath;
    }

    
    /**
     * @return the hasDraft
     */
    public boolean hasDraft() {
        return hasDraft;
    }

    
    /**
     * @param hasDraft the hasDraft to set
     */
    public void setHasDraft(boolean hasDraft) {
        this.hasDraft = hasDraft;
    }

    
    /**
     * @return the isNotOrphanDraft
     */
    public boolean isNotOrphanDraft() {
        return isNotOrphanDraft;
    }

    
    /**
     * @param isNotOrphanDraft the isNotOrphanDraft to set
     */
    public void setNotOrphanDraft(boolean isNotOrphanDraft) {
        this.isNotOrphanDraft = isNotOrphanDraft;
    }
    
    /**
     * Getter for provider infos property
     * having given key.
     * 
     * @param key
     * @return property
     */
    public Object get(String key){
        if(this.flux != null){
          if(this.flux.containsKey(key)){
              return flux.get(key);
          }
        }
        return new Object();
    }

}

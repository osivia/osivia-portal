package org.osivia.portal.core.cms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service optimisé pour renvoyer toutes les informations de contenu liées à la
 * publication
 * 
 * @author jeanseb
 * 
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
	// Modif FILEBROWSER-begin
	private boolean deletableByUser = false;
	// Modif FILEBROWSER-end
	private boolean anonymouslyReadable = false;
	private boolean published = false;
	// Modif COMMENTS-begin
	private boolean commentableByUser;
	// Modif COMMENTS-end
	
	// Modif SPACEID-begin
	private String spaceID = null;
	private String parentSpaceID = null;
	// Modif SPACEID-end
	
	// Modif SPACEID-begin
	private Map<String, String> subTypes;
	// Modif SPACEID-end

	private String publishSpaceType = null;
	private String publishSpacePath = null;
	private String publishSpaceDisplayName = null;

	private boolean isLiveSpace = false;
	
	private List<Integer> errorCodes = new ArrayList<Integer>();

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
	// Modif FILEBROWSER-begin
	public boolean isDeletableByUser() {
		return deletableByUser;
	}

	public void setDeletableByUser(boolean deletableByUser) {
		this.deletableByUser = deletableByUser;
	}

	// Modif FILEBROWSER-end
	public boolean isAnonymouslyReadable() {
		return anonymouslyReadable;
	}

	public void setAnonymouslyReadable(boolean anonymouslyReadable) {
		this.anonymouslyReadable = anonymouslyReadable;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
	// Modif COMMENTS-begin
	public boolean isCommentableByUser() {
		return commentableByUser;
	}

	public void setCommentableByUser(boolean commentableByUser) {
		this.commentableByUser = commentableByUser;
	}
	// Modif COMMENTS-end

	// Modif SPACEID-begin
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
	// Modif SPACEID-begin
	
	// Modif SUBTYPES-begin
	public Map<String, String> getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(Map<String, String> subTypes) {
		this.subTypes = subTypes;
	}
	// Modif SUBTYPES-end

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

}

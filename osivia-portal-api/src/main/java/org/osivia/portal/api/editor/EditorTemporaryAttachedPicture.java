package org.osivia.portal.api.editor;

import java.io.File;

/**
 * @author Cédric Krommenhoek
 */
public class EditorTemporaryAttachedPicture {

    /**
     * Source path.
     */
    private String sourcePath;
    /**
     * File.
     */
    private File file;
    /**
     * File name.
     */
    private String fileName;
    /**
     * File content type.
     */
    private String contentType;


    /**
     * Constructor.
     */
    public EditorTemporaryAttachedPicture() {
        super();
    }


    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

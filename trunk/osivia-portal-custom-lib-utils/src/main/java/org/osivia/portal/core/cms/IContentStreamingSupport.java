package org.osivia.portal.core.cms;

import java.io.InputStream;

/**
 * Content streaming support interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IContentStreamingSupport {

    /**
     * Get stream.
     * 
     * @return stream
     */
    InputStream getStream();


    /**
     * Set stream.
     * 
     * @param inputStream stream
     */
    void setStream(InputStream inputStream);

}

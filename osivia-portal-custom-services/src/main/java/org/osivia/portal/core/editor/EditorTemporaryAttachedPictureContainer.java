package org.osivia.portal.core.editor;

import org.osivia.portal.api.editor.EditorTemporaryAttachedPicture;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary attached picture container.
 *
 * @author Cédric Krommenhoek
 */
public class EditorTemporaryAttachedPictureContainer {

    private final Map<String, List<EditorTemporaryAttachedPicture>> map;


    /**
     * Constructor.
     */
    public EditorTemporaryAttachedPictureContainer() {
        super();
        this.map = new ConcurrentHashMap<>();
    }


    public Map<String, List<EditorTemporaryAttachedPicture>> getMap() {
        return map;
    }
}

package org.osivia.portal.api.directory;

import org.osivia.portal.api.directory.entity.DirectoryPerson;


public class DirectoryHelper {

    public static String getBestDisplayName(DirectoryPerson p) {

        String ret = p.getUid();

        if (p.getDisplayName() != null) {
            ret = p.getDisplayName();
        } else if (p.getAlias() != null) {
            ret = p.getAlias();
        } else if (p.getGivenName() != null) {
            ret = p.getGivenName();
        }
        return ret;
    }
}

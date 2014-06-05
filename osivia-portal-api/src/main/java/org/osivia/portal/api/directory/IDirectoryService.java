package org.osivia.portal.api.directory;

import org.osivia.portal.api.directory.entity.DirectoryPerson;


public interface IDirectoryService {

    DirectoryPerson getPerson(String username);

    <T extends ProxyDirectoryBean> T getDirectoryBean(String name, Class<T> requiredType);
}

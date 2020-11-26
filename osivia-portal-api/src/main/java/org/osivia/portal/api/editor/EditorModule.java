package org.osivia.portal.api.editor;

import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * Editor module java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class EditorModule {

    /**
     * Identifier.
     */
    private String id;
    /**
     * Portlet instance.
     */
    private String instance;
    /**
     * Internationalization key.
     */
    private String key;
    /**
     * Class loader.
     */
    private ClassLoader classLoader;
    /**
     * Application context.
     */
    private ApplicationContext applicationContext;
    /**
     * Parameters.
     */
    private Set<String> parameters;


    /**
     * Constructor.
     */
    public EditorModule() {
        super();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Set<String> getParameters() {
        return parameters;
    }

    public void setParameters(Set<String> parameters) {
        this.parameters = parameters;
    }
}

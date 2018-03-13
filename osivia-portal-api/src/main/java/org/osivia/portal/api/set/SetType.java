package org.osivia.portal.api.set;

public class SetType {

	/** Set id */
	private final String id;
	
	/** Set key */
	private String key;
	
    /** Set classLoader */
	private ClassLoader customizedClassLoader;

	/**
	 * Constructor
	 * @param id
	 */
	public SetType(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ClassLoader getCustomizedClassLoader() {
		return customizedClassLoader;
	}

	public void setCustomizedClassLoader(ClassLoader customizedClassLoader) {
		this.customizedClassLoader = customizedClassLoader;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetType other = (SetType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}

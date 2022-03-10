package org.osivia.portal.core.instances;


import org.jboss.portal.core.model.instance.Instance;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class MemoryInstanceSecurityBinding implements Serializable {
	 /** The serialVersionUID */
	   private static final long serialVersionUID = -2832148715381794267L;

	   /** The primary key. */
	   private Long key;

	   /** The role of this contraint. */
	   private String role;

	   /** The set of actions of this constraint. */
	   private Set actions;

	   /** The cached toString value. */
	   private transient String toString;

	   /** The cached hash code. */
	   private transient int hashCode;

	   /** The cached actions as a string. */
	   private transient String actionsAsString;

	   private Instance instance;

	   public MemoryInstanceSecurityBinding()
	   {
	      super();
	   }

	   /**
	    * Create a new constraint with the provided actions for the specified role.
	    *
	    * @param actions a comma separated list of allowed actions
	    * @param role    the role name
	    */
	   public MemoryInstanceSecurityBinding(String actions, String role)
	   {
	      if (role == null)
	      {
	         throw new IllegalArgumentException("Role cannot be null");
	      }
	      if (actions == null)
	      {
	         throw new IllegalArgumentException("Actions cannot be null");
	      }

	      //
	      StringTokenizer tokens = new StringTokenizer(actions, ",");
	      Set set = new HashSet();
	      while (tokens.hasMoreTokens())
	      {
	         set.add(tokens.nextToken().trim());
	      }

	      //
	      this.role = role;
	      this.actions = Collections.unmodifiableSet(set);
	   }

	   /**
	    * Create a new constraint with the provided actions and the specified role.
	    *
	    * @param actions the set of actions
	    * @param role    the role name
	    */
	   public MemoryInstanceSecurityBinding(Set actions, String role)
	   {
	      if (role == null)
	      {
	         throw new IllegalArgumentException("Role cannot be null");
	      }
	      if (actions == null)
	      {
	         throw new IllegalArgumentException("Actions cannot be null");
	      }

	      //
	      this.role = role;
	      this.actions = Collections.unmodifiableSet(new HashSet(actions));
	   }

	   /** Copy constructor. */
	   public MemoryInstanceSecurityBinding(MemoryInstanceSecurityBinding other)
	   {
	      if (other == null)
	      {
	         throw new IllegalArgumentException("The constraint to clone cannot be null");
	      }

	      //
	      this.role = other.role;
	      this.actions = other.actions;
	   }

	   /**
	    * Return a <code>java.util.Set<String></code> of allowed actions.
	    *
	    * @return the action set
	    */
	   public Set getActions()
	   {
	      return actions;
	   }

	   /**
	    * Return the role of this constraint
	    *
	    * @return the role
	    */
	   public String getRole()
	   {
	      return role;
	   }

	   /**
	    * Return a comma separated list of actions.
	    *
	    * @return the action string representation
	    */
	   public String getActionsAsString()
	   {
	      if (actionsAsString == null)
	      {
	         StringBuffer tmp = new StringBuffer();
	         for (Iterator i = actions.iterator(); i.hasNext();)
	         {
	            String action = (String)i.next();
	            if (i.hasNext())
	            {
	               tmp.append(", ");
	            }
	            tmp.append(action);
	         }
	         actionsAsString = tmp.toString();
	      }
	      return actionsAsString;
	   }

	   /** @see Object#toString */
	   public String toString()
	   {
	      if (toString == null)
	      {
	         StringBuffer tmp = new StringBuffer("SecurityConstraint: actions [");
	         for (Iterator i = actions.iterator(); i.hasNext();)
	         {
	            String action = (String)i.next();
	            if (i.hasNext())
	            {
	               tmp.append(", ");
	            }
	            tmp.append(action);
	         }
	         tmp.append("] role [").append(role).append("]");
	         toString = tmp.toString();
	      }
	      return toString;
	   }

	   public boolean equals(Object o)
	   {
	      if (this == o)
	      {
	         return true;
	      }
	      if (o instanceof MemoryInstanceSecurityBinding)
	      {
	    	  MemoryInstanceSecurityBinding that = (MemoryInstanceSecurityBinding)o;
	         return actions.equals(that.actions) && role.equals(that.role);
	      }
	      return false;
	   }

	   public int hashCode()
	   {
	      if (hashCode == 0)
	      {
	         int hashCode;
	         hashCode = actions.hashCode();
	         hashCode = 29 * hashCode + role.hashCode();
	         this.hashCode = hashCode;
	      }
	      return hashCode;
	   }

	   protected void setKey(Long k)
	   {
	      key = k;
	   }

	   protected Long getKey()
	   {
	      return key;
	   }

	   public void setActions(Set actions)
	   {
	      this.actions = actions;
	   }

	   public void setRole(String role)
	   {
	      this.role = role;
	   }

	   public Instance getInstance()
	   {
	      return instance;
	   }

	   public void setInstance(Instance instance)
	   {
	      this.instance = instance;
	   }	

}

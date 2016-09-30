/**
 * 
 */
package org.osivia.portal.api.trace;

import java.util.Date;



/**
 * Object to store in the traces db.
 * 
 * 
 * @author Loïc Billon
 *
 */
public class Trace {

	
	/** All about the actor of the action, could be a user, a number of user, a batch, a request */ 
	private Object actor;
	
	/** All about the action to log, could be a string */
	private Object action;
	
	/** All about the concern of the action to log, For exemple : a document, when a cms action is called */
	private Object about;
	
	/** The time, could be a date, a period */
	private Object time;

	/**
	 * @param actor
	 * @param action
	 * @param about
	 * @param time
	 */
	public Trace(Object actor, Object action, Object about) {
		this.actor = actor;
		this.action = action;
		this.about = about;
		this.time = new Date();
	}
	
	/**
	 * @param actor
	 * @param action
	 * @param about
	 * @param time
	 */
	public Trace(Object actor, Object action, Object about, Object time) {
		this.actor = actor;
		this.action = action;
		this.about = about;
		this.time = time;
	}

	/**
	 * @return the actor
	 */
	public Object getActor() {
		return actor;
	}

	/**
	 * @param actor the actor to set
	 */
	public void setActor(Object actor) {
		this.actor = actor;
	}

	/**
	 * @return the action
	 */
	public Object getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Object action) {
		this.action = action;
	}

	/**
	 * @return the about
	 */
	public Object getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(Object about) {
		this.about = about;
	}

	/**
	 * @return the time
	 */
	public Object getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Object time) {
		this.time = time;
	}

	
	
	

}

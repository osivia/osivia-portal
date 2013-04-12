package org.osivia.portal.core.tracker;

import java.util.Stack;

import org.jboss.portal.server.ServerInvocation;

/**
 * Utility functions to retrieve request scope datas
 *
 */
public class RequestContextUtil {

	public static ITracker currentTracker;

	public static ServerInvocation getServerInvocation() {

		if (currentTracker != null) {
			Stack stack = currentTracker.getStack();
			if (stack.size() > 0) {
				return (ServerInvocation) stack.get(0);
			}

			
		}
		
		return null;

	}
}

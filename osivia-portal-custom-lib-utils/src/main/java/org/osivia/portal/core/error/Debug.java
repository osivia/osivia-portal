
package org.osivia.portal.core.error;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Static usefull methods for debug.<br/>
 *
 */
public abstract class Debug {
	/**
	 * Returns the default <tt>Object.toString()</tt> result,
	 *  whether or not the given object's class overrides the <tt>toString()</tt> method.<br/>
	 * <br/>
	 * <p>
	 * Returns a string consisting of the name of the class of which the
	 *  object is an instance, the at-sign character '<code>@</code>', and
	 *  the unsigned hexadecimal representation of the value obtained by
	 *  converting the internal address of the object into an integer.
	 *  In other words, this method returns a string equal to the value of:
	 *      <blockquote>
	 *          <tt><pre>
	 *          getClass().getName() + '@' + Integer.toHexString( System.identityHashCode() )
	 *          </pre></tt>
	 *      </blockquote>
	 * </p>
	 * @param anObject the object ot display
	 * @return the default string representation of the object.
	 * @see java.lang.Object#toString()
	 * @see java.lang.System#identityHashCode(java.lang.Object)
	 */
	public static String toString( Object anObject ) {
		StringBuffer result = new StringBuffer();

		result.append( anObject.getClass().getName() );
		result.append( '@' );
		result.append( Integer.toHexString( System.identityHashCode( anObject ) ) );

		return result.toString();
	}

	/**
	 * Returns the class name of a specified object.<br/>
	 * The class name is only the name of the class (without package name).
	 * @param anObject
	 * @return the class name of the specified object
	 * @see #getAbsoluteClassName( Object )
	 */
	public static String getClassName( Object anObject ) {   
		if( anObject == null )
			return null;
		else
			return getSimpleName(anObject.getClass());
	}	
	
	/**
	 * Returns the absolute class name of a specified object.<br/>
	 * @param anObject
	 * @return the complete class name of the specified object (= "packageName.className").
	 * @see #getClassName( Object )
	 * @see java.lang.Class#getName()
	 */
	public static String getAbsoluteClassName( Object anObject ) {
		if( anObject == null )
			return null;
		else
			return anObject.getClass().getName();
	}
	
	/**
	 * Returns the simple name (without package prefix) of a specified class.
	 */
	public static String getSimpleName( Class<?> aClass ) {
		String className = null;
		
		// Java 1.5 or after
       	className = aClass.getSimpleName();
       	return className;
	}

	/**
	 * Returns, as a string, the stack trace of a specified Throwable.
	 * @param aCause the specified Throwable.
	 * @return the stack trace string representation of aCause.
	 */
	public static String stackTraceToString( Throwable aCause ) {
		String result = "";
		if( aCause != null ) {
			StringWriter writer = new StringWriter(); 
			aCause.printStackTrace( new PrintWriter( writer ) );
			result = writer.toString();
		}
		return result;
	}

	/**
	 * Print the current stack trace to System.err with a maximum depth.
	 * Do not display levels from this class (util.Debbug).
	 * @param aDepth the maximum depth
	 * @see #printStackTrace(int, java.io.PrintStream)
	 */
	public static void printStackTrace( int aDepth ) {
		printStackTrace( aDepth, System.err, 3 );
	}

	/**
	 * Print the current stack trace to the specified PrintStream with a maximum depth.
	 * Do not display levels from this class (util.Debbug).
	 * @param aDepth the maximum depth
	 * @param aPrintStream the stream to wrtite in
	 * @see #printStackTrace(int)
	 */
	public static void printStackTrace( int aDepth, PrintStream aPrintStream ) {
		printStackTrace(aDepth, aPrintStream, 3);
	}	
	
	/**
	 * Print the current stack trace to the specified PrintStream with a maximum depth.
	 * @param aDepth the maximum depth
	 * @param aPrintStream the stream to wrtite in
	 * @param skipLevel the levels to skip
	 * @see #printStackTrace(int)
	 */
	private static void printStackTrace( int aDepth, PrintStream aPrintStream, int skipLevel ) {
		Exception aCause = new Exception();

		synchronized( aPrintStream ) {
			StackTraceElement[] trace = aCause.getStackTrace();
			if ( trace != null ) {
				int length = trace.length;
				if ( length > skipLevel ) {
					aPrintStream.println( "" + trace[skipLevel-1] ); // skip first levels (== this)
					for ( int i = skipLevel; i < trace.length && i < aDepth + skipLevel; i++ ) {
						aPrintStream.println("\tat " + trace[i]);
					}
				}
				aPrintStream.println();
			}
		}
	}

	/**
	 * Converts a Throwable to a displayable string. 
	 * This method can be used in to dispay the error to end-users
	 *  (in an error panel with a 'details' button for example).
	 * @see #allMessagesToString(Throwable)
	 */
	public static String throwableToString( final Throwable aCause ) {
		final StringBuffer buff = new StringBuffer();
		buff.append( allMessagesToString( aCause ) );
		buff.append( "\n\n---------------------\n\n" );
		buff.append( Debug.stackTraceToString( aCause ) );
		return buff.toString();
	}

	/**
	 * Gets all the messages contained in the stack trace of the specified Throwable.
	 * Null messages are replaced by exception class names.
	 * @param aCause the specified Throwable
	 * @return All messages contained in the stack trace.
	 * @see #allMessagesToString(Throwable)
	 */
	public static String[] getAllMessages( final Throwable aCause ) {
		final List<String> messages = new LinkedList<String>();
		Throwable subCause = aCause;
		while( subCause != null ) {
			if( subCause.getMessage() != null && !subCause.getMessage().equals("") )			
				messages.add( subCause.getMessage() );			
			else			
				messages.add( subCause.getClass().getName() );			
			subCause = subCause.getCause();
		}
		return (String[]) messages.toArray( new String[0] );
	}

	/**
	 * Returns all messages contained in the stack trace of the specified Throwable as a single string.
	 * @param aCause the specified Throwable
	 * @return a String which contains all messages of the stack trace.
	 * @see #getAllMessages(Throwable)
	 */
	public static String allMessagesToString( final Throwable aCause ) {
		final StringBuffer buff = new StringBuffer();
		final String[] messages = getAllMessages( aCause );
		if( messages.length > 0 ) {
			buff.append( messages[0] );
			for ( int i = 1; i < messages.length; i++ ) {
				buff.append( "\nCause : " ).append( messages[i] );
			}
		}
		return buff.toString();
	}	
	
	/**
	 * Prints a specified array on the Console.
	 * This method uses the print() method each array item to do this.
	 * @param anArray to display
	 * @see #print(Object)
	 */
	public static void printArray( Object[] anArray ) {
		printArray( anArray, 0 );
	}
	
	/**
	 * Prints a specified collection on the Console.
	 * This method uses the print() method on each collection element to do this.
	 * @param aCollec a collection to display
	 * @see #print(Object)
	 */
	public static void printCollec( Collection<?> aCollec ) {
		printCollec( aCollec, 0 );
	}
	
	/**
	 * Prints a object to the console for Debug.
	 * If the object is null prints "null".
	 * If the object is a String prints it with surrounding quotes (to show white-spaces).
	 * If the object is an array or a collection prints all its items recursively.
	 * Otherwise uses the toString() method.
	 * @param anObj an object to diplay.
	 * @see #printArray(Object[])
	 * @see #printCollec(Collection)
	 * @see java.lang.Object#toString()
	 */
	public static void print( Object anObj ) {
		print( anObj, 0 );		
	}

	/**
	 * Prints a specified array on the Console.
	 * This method uses the print() method each array item to do this.
	 * @param anArray to display
	 * @param anIndentLevel the indentation level
	 * @see #print(Object, int)
	 */
	private static void printArray( Object[] anArray, int anIndentLevel ) {
		if( anArray == null )
			System.out.println("null");
		else {
			Class<?> itemsType = anArray.getClass().getComponentType();			
			System.out.print( getSimpleName(itemsType) + "[] = {");
			if( anArray.length > 0 ) {
				System.out.println();
				for ( int i = 0; i < anArray.length; i++ ) {					
					print( anArray[i], anIndentLevel + 1  );
				}
				indent( anIndentLevel );
			}			
			System.out.println("}");
		}
	}
	
	/**
	 * Prints a specified collection on the Console.
	 * This method uses the print() method on each collection element to do this.
	 * @param aCollec a collection to display
	 * @param anIndentLevel the indentation level
	 * @see #print(Object, int)
	 */
	private static void printCollec( Collection<?> aCollec, int anIndentLevel ) {
		if( aCollec == null )
			System.out.println("null");
		else {
			System.out.print(getClassName(aCollec) + " = {");
			if( !aCollec.isEmpty() ) {
				System.out.println();
				Iterator<?> it = aCollec.iterator();
				while( it.hasNext() ) {
					print( it.next(), anIndentLevel + 1 );
				}
				indent( anIndentLevel );
			}			
			System.out.println("}");
		}
	}
	
	/**
	 * Prints a object to the console for Debug.
	 * If the object is null prints "null".
	 * If the object is a String prints it with surrounding quotes (to show white-spaces).
	 * If the object is an array or a collection prints all its items recursively.
	 * Otherwise uses the toString() method.
	 * @param anObj an object to diplay.
	 * @param anIndentLevel the indentation level
	 * @see #printArray(Object[], int)
	 * @see #printCollec(Collection, int)
	 * @see java.lang.Object#toString()
	 */
	private static void print( Object anObj, int anIndentLevel ) {
		indent( anIndentLevel );
		if( anObj == null )
			System.out.println("null");
		else if( anObj instanceof String )
			System.out.println("\"" + anObj + "\"");		
		else if( anObj instanceof Object[] )
			printArray( (Object[]) anObj, anIndentLevel);		
		else if( anObj instanceof Collection )
			printCollec( (Collection<?>) anObj, anIndentLevel );		
		else 
			System.out.println(anObj.toString());
	}
	
	private static void indent (int aLevel) {
		for( int i = 0; i < aLevel; i++ ) {
			System.out.print("  ");
		}
	}

}//-- End of class : Debug ------
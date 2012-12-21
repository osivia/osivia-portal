package org.osivia.portal.administration.ejb;

import java.util.Set;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;

public class SetToListPropResolver extends PropertyResolver {

    private static String resolverWord = "settolist";
    
    private PropertyResolver originalResolver;
    
    public SetToListPropResolver(PropertyResolver propertyResolver) {
        this.originalResolver = propertyResolver;
    }
    
    public Object getValue(Object param1, Object param2) {
        if(param1 instanceof Set) {
            Set mySet = (Set) param1;
            
            if (resolverWord.equalsIgnoreCase((String) param2)) {
                return mySet.toArray();
            } 
            else {
                throw new PropertyNotFoundException();
            }
        }
        else {
            return originalResolver.getValue(param1, param2);
        }
    }

    public Object getValue(Object param1, int param2) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            throw new PropertyNotFoundException();
        }
        else {
            return originalResolver.getValue(param1, param2);
        }
    }

    public void setValue(Object param1, Object param2, Object param3) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            throw new PropertyNotFoundException();
        }
        else {
            originalResolver.setValue(param1, param2, param3);
        }
    }

    public void setValue(Object param1, int param2, Object param3) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            throw new PropertyNotFoundException();
        }
        else {
            originalResolver.setValue(param1, param2, param3);
        }
    }

    public boolean isReadOnly(Object param1, Object param2) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            return true;
        }
        else {
            return originalResolver.isReadOnly(param1, param2);
        }
    }

    public boolean isReadOnly(Object param1, int param2) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            return true;
        }
        else {
            return originalResolver.isReadOnly(param1, param2);
        }
    }

    public Class getType(Object param1, Object param2) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            throw new PropertyNotFoundException();
        }
        else {
            return originalResolver.getType(param1, param2);
        }
    }

    public Class getType(Object param1, int param2) throws EvaluationException, PropertyNotFoundException {
        if (param1 instanceof Set) {
            throw new PropertyNotFoundException();
        }
        else {
            return originalResolver.getType(param1, param2);
        }
    }
}

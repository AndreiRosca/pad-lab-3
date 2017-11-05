package md.utm.pad.labs.node.dsl;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Created by anrosca on Nov, 2017
 */
public class BeanComparator implements Comparator {
    private Method method;
    private boolean isAscending;
    private boolean isIgnoreCase;
    private boolean isNullsLast = true;

    public BeanComparator(Class<?> beanClass, String methodName) {
        this(beanClass, methodName, true);
    }

    public BeanComparator(Class<?> beanClass, String methodName, boolean isAscending) {
        this(beanClass, methodName, isAscending, true);
    }

    public BeanComparator(Class<?> beanClass, String methodName, boolean isAscending, boolean isIgnoreCase) {
        this.isAscending = isAscending;
        this.isIgnoreCase = isIgnoreCase;
        try {
            method = beanClass.getMethod(methodName, new Class<?>[] {});
        } catch(NoSuchMethodException nsme) {
            throw new IllegalArgumentException(methodName + "() method does not exist");
        }
        Class returnClass =	method.getReturnType();
        if (returnClass.getName().equals("void")) {
            throw new IllegalArgumentException(methodName + " has a void return type");
        }
    }

    public void setAscending(boolean isAscending) {
        this.isAscending = isAscending;
    }

    public void setIgnoreCase(boolean isIgnoreCase) {
        this.isIgnoreCase = isIgnoreCase;
    }

    public void setNullsLast(boolean isNullsLast) {
        this.isNullsLast = isNullsLast;
    }

    @SuppressWarnings("unchecked")
    public int compare(Object object1, Object object2) {
        Object field1 = null;
        Object field2 = null;
        try {
            field1 = method.invoke(object1, new Object[] {});
            field2 = method.invoke(object2, new Object[] {});
        } catch (Exception e) {
            throw new RuntimeException( e );
        }

        // Treat empty strings like nulls
        if (field1 instanceof String && ((String)field1).length() == 0) {
            field1 = null;
        }
        if (field2 instanceof String && ((String)field2).length() == 0) {
            field2 = null;
        }

        // Handle sorting of null values
        if (field1 == null && field2 == null)
            return 0;
        if (field1 == null)
            return isNullsLast ? 1 : -1;
        if (field2 == null)
            return isNullsLast ? -1 : 1;

        //  Compare objects
        Object c1;
        Object c2;

        if (isAscending) {
            c1 = field1;
            c2 = field2;
        }
        else {
            c1 = field2;
            c2 = field1;
        }
        if (c1 instanceof Comparable) {
            if (c1 instanceof String
                    &&  isIgnoreCase)
                return ((String)c1).compareToIgnoreCase((String)c2);
            else
                return ((Comparable)c1).compareTo(c2);
        } else {
            // Compare as a String
            if (isIgnoreCase)
                return c1.toString().compareToIgnoreCase(c2.toString());
            else
                return c1.toString().compareTo(c2.toString());
        }
    }
}
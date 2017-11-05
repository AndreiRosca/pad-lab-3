package md.utm.pad.labs.node.dsl;

import md.utm.pad.labs.node.dsl.util.ReflectionUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by anrosca on Nov, 2017
 */
public class DslParser {
    private static final Logger LOGGER = Logger.getLogger(DslParser.class);

    //Matches: from Student group by age, id order by name, age
    private static final Pattern DSL_PATTERN =
            Pattern.compile("from\\s*(?<entityName>\\w+)\\s*(where\\s*(?<where>\\w+\\s*(<|>|=|!=|>=|<=)\\s*(\\w+|'.+')))?\\s*(group by (?<groupBy>\\w+(,\\s*\\w+)*))?\\s*(order by (?<orderBy>\\w+(,\\s*\\w+)*))?");

    private static final Pattern WHERE_PATTERN = Pattern.compile("(?<firstOperand>\\w+)+\\s*(?<operation><|>|=|!=|>=|<=)\\s*(?<secondOperand>\\w+|'.+')");

    public <T> List<T> execute(String dslExpression, Map<String, List<T>> dataSet) throws InvalidDslException {
        Matcher dslMatcher = DSL_PATTERN.matcher(dslExpression);
        if (dslMatcher.find()) {
            String entityName = dslMatcher.group("entityName");
            String whereClause = dslMatcher.group("where");
            String groupByFields = dslMatcher.group("groupBy");
            String orderByFields = dslMatcher.group("orderBy");
            if (dataSet.containsKey(entityName)) {
                List<T> result = dataSet.get(entityName);
                if (shouldSort(orderByFields))
                    result.sort(makeComparatorFor(getEntityClass(result), orderByFields));
                if (shouldFilter(whereClause))
                    result = filterResult(result, getEntityClass(result), whereClause);
                return result;
            }
        }
        throw new InvalidDslException();
    }

    private <T> List<T> filterResult(List<T> result, Class<?> entityClass, String whereClause) {
        return result.stream()
                .filter(makePredicate(entityClass, whereClause))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <T> Predicate<? super T> makePredicate(Class<?> entityClass, String whereClause) {
        Matcher matcher = WHERE_PATTERN.matcher(whereClause);
        if (matcher.find()) {
            String fieldName = matcher.group("firstOperand");
            String operation = matcher.group("operation");
            String value = matcher.group("secondOperand");
            return new ComparablePredicate(entityClass, fieldName, operation, value);
        }
        throw new InvalidDslException("Invalid where clause.");
    }

    private boolean shouldFilter(String whereClause) {
        return whereClause != null;
    }

    private boolean shouldSort(String orderByFields) {
        return orderByFields != null;
    }

    private Class<?> getEntityClass(List<?> data) {
        return data.get(0).getClass();
    }

    @SuppressWarnings("unchecked")
    private <T> Comparator<? super T> makeComparatorFor(Class<?> entityClass, String orderByFields) {
        return new BeanComparator(entityClass, toGetterName(orderByFields));
    }

    private String toGetterName(String fieldName) {
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    private static class ComparablePredicate<T> implements Predicate<T> {
        private final String operation;
        private final String value;
        private final Field field;
        private final Class<?> entityClass;

        public ComparablePredicate(Class<?> entityClass, String fieldName, String operation, String value) {
            this.entityClass = entityClass;
            this.operation = operation;
            this.value = value;
            field = ReflectionUtil.getField(entityClass, fieldName);
        }

        public boolean test(T t) {
            Comparable fieldValue = (Comparable) ReflectionUtil.getFieldValue(t, field);
            if (isSecondOperandFieldName()) {
                Field secondField = ReflectionUtil.getField(entityClass, value);
                return compare(fieldValue, operation, ReflectionUtil.getFieldValue(t, secondField));
            }
            return compare(fieldValue, operation, parseValue());
        }

        private boolean isSecondOperandFieldName() {
            return !value.equalsIgnoreCase("null") && !value.matches("\\d+") &&
                    !value.matches("'.+'");
        }

        @SuppressWarnings("unchecked")
        private boolean compare(Comparable fieldValue, String operation, Object value) {
            switch (operation) {
                case ">":
                    return fieldValue.compareTo(value) > 0;
                case ">=":
                    return fieldValue.compareTo(value) >= 0;
                case "<":
                    return fieldValue.compareTo(value) < 0;
                case "<=":
                    return fieldValue.compareTo(value) <= 0;
                case "=":
                    return (value != null) ? fieldValue.equals(value) : value == fieldValue;
                case "!=":
                    return fieldValue.compareTo(value) != 0;
            }
            throw new InvalidDslException("Invalid operation in where clause.");
        }

        private Object parseValue() {
            if (value.matches("\\d+"))
                return Integer.valueOf(value);
            if (value.equalsIgnoreCase("null"))
                return null;
            if (value.matches("'.+'"))
                return value.substring(1, value.length() - 1);
            return value;
        }
    }

    public static class InvalidDslException extends RuntimeException {
        public InvalidDslException() {
        }

        public InvalidDslException(String message) {
            super(message);
        }
    }
}

package md.utm.pad.labs.node.dsl;

import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anrosca on Nov, 2017
 */
public class DslParser {
    private static final Logger LOGGER = Logger.getLogger(DslParser.class);
    //Matches: from Student group by age, id order by name, age
    private static final Pattern DSL_PATTERN =
            Pattern.compile("from\\s*(?<entityName>\\w+)\\s*(where\\s*(?<where>\\w+\\s*(<|>|=|!=|>=|<=)\\s*\\w+))?\\s*(group by (?<groupBy>\\w+(,\\s*\\w+)*))?\\s*(order by (?<orderBy>\\w+(,\\s*\\w+)*))?");

    public <T> List<T> execute(String dslExpression, Map<String, List<T>> dataSet) throws InvalidDslException {
        Matcher dslMatcher = DSL_PATTERN.matcher(dslExpression);
        if (dslMatcher.find()) {
            String entityName = dslMatcher.group("entityName");
            String groupByFields = dslMatcher.group("groupBy");
            String orderByFields = dslMatcher.group("orderBy");
            if (dataSet.containsKey(entityName)) {
                List<T> result = dataSet.get(entityName);
                if (shouldSort(orderByFields))
                    result.sort(makeComparatorFor(getEntityClass(result), orderByFields));
                return result;
            }
        }
        throw new InvalidDslException();
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

    public static class InvalidDslException extends RuntimeException {
    }
}

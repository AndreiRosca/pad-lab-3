package md.utm.pad.labs.node.dsl;

import md.utm.pad.labs.domain.Student;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by anrosca on Nov, 2017
 */
public class DslParserTest {
    private static final Map<String, List<Student>> dataSet = new HashMap<>();
    private DslParser parser = new DslParser();

    @BeforeClass
    public static void before() {
        dataSet.put(Student.class.getSimpleName(), makeStudentList());
    }

    private static List<Student> makeStudentList() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Mike Smith", 3, 20));
        students.add(new Student("John Doe", 2, 21));
        students.add(new Student("Denis Ritchie", 1, 20));
        return students;
    }

    @Test
    public void testGetAll() {
        List<Student> students = parser.execute("from Student", dataSet);
        assertEquals(dataSet.get("Student"), students);
    }

    @Test
    public void testGetAllSorted() {
        List<Student> students = parser.execute("from Student order by name", dataSet);
        List<Student> expected = dataSet.get("Student");
        expected.sort(Comparator.comparing(Student::getName));
        assertEquals(expected, students);
    }

    @Test
    public void testGetAllSortedByMultipleFields() {
        List<Student> students = parser.execute("from Student order by age, name", dataSet);
        List<Student> expected = dataSet.get("Student");
        expected.sort(Comparator.comparing(Student::getAge).thenComparing(Student::getName));
        assertEquals(expected, students);
    }

    @Test
    public void testGetAllFilteredGT() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent > 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() > 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredLT() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent < 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() < 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredEQ() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent = 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() == 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredGTE() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent >= 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() >= 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredLTE() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent <= 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() <= 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredNE() {
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent != 2", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() != 2)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredEQNull() {
        dataSet.get("Student").add(new Student(null, 1));
        List<Student> students = parser.execute("from Student where name = null", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getName() == null)
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredEQString() {
        List<Student> students = parser.execute("from Student where name = 'Mike Smith'", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getName().equals("Mike Smith"))
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredEQProperty() {
        dataSet.get("Student").add(new Student("Vasya", 1, 1));
        List<Student> students = parser.execute("from Student where numberOfReportsToPresent = age", dataSet);
        assertEquals(dataSet.get("Student").stream()
                .filter(s -> s.getNumberOfReportsToPresent() == s.getAge())
                .collect(Collectors.toList()), students);
    }

    @Test
    public void testGetAllFilteredEQProperty_WithIncompatibleTypes() {
        List<Student> students = parser.execute("from Student where name = age", dataSet);
        assertEquals(Collections.emptyList(), students);
    }

    @Ignore
    @Test(expected = DslParser.InvalidDslException.class)
    public void testGetAllFilteredWithInvalidOperation() {
        parser.execute("from Student where numberOfReportsToPresent ? 2", dataSet);
    }
}

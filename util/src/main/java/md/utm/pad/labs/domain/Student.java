package md.utm.pad.labs.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement
public class Student implements Comparable<Student> {
    private long id;
    private String name;
    private int numberOfReportsToPresent;
    private int age;

    public Student() {
    }

    public Student(String name, int numberOfReportsToPresent) {
        this.name = name;
        this.numberOfReportsToPresent = numberOfReportsToPresent;
    }

    public Student(String name, int numberOfReportsToPresent, int age) {
        this(name, numberOfReportsToPresent);
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfReportsToPresent() {
        return numberOfReportsToPresent;
    }

    public void setNumberOfReportsToPresent(int numberOfReportsToPresent) {
        this.numberOfReportsToPresent = numberOfReportsToPresent;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(Student other) {
        return Integer.compare(numberOfReportsToPresent, other.numberOfReportsToPresent);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfReportsToPresent=" + numberOfReportsToPresent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (numberOfReportsToPresent != student.numberOfReportsToPresent) return false;
        if (age != student.age) return false;
        return name != null ? name.equals(student.name) : student.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + numberOfReportsToPresent;
        result = 31 * result + age;
        return result;
    }

    public static Student fromCsvString(String csv) {
        Pattern pattern = Pattern.compile("(.+), (\\d+)");
        Matcher matcher = pattern.matcher(csv);
        if (matcher.find()) {
            Student student = new Student(matcher.group(1), Integer.valueOf(matcher.group(2)));
            return student;
        }
        return null;
    }
}

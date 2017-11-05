package md.utm.pad.labs.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Student implements Comparable<Student> {
    private Long id;
    private String name;
    private int numberOfReportsToPresent;

    public Student() {
    }

    public Student(String name, int numberOfReportsToPresent) {
        this.name = name;
        this.numberOfReportsToPresent = numberOfReportsToPresent;
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

package md.utm.pad.labs.repository;

import md.utm.pad.labs.domain.Student;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStudentRepository implements StudentRepository {
    private static final AtomicLong idGenerator = new AtomicLong();
    private static final Map<Long, Student> students = new ConcurrentHashMap<>();

    static {
        createStudent(new Student("Mike Smith"));
        createStudent(new Student("John Doe"));
        createStudent(new Student("Denis Ritchie"));
    }

    private static Student createStudent(Student student) {
        student.setId(idGenerator.incrementAndGet());
        students.put(student.getId(), student);
        return student;
    }

    @Override
    public Collection<? extends Student> findAll() {
        return students.values();
    }

    @Override
    public Student create(Student student) {
        return createStudent(student);
    }
}

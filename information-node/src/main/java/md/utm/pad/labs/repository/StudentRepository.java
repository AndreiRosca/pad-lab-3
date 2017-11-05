package md.utm.pad.labs.repository;

import md.utm.pad.labs.domain.Student;

import java.util.Collection;

public interface StudentRepository {
    Collection<? extends Student> findAll();
    Student create(Student student);
}

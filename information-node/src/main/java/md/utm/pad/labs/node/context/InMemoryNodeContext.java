package md.utm.pad.labs.node.context;

import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.repository.StudentRepository;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryNodeContext implements NodeContext {
    private final NodeConfiguration nodeConfiguration;
    private Set<Student> students = Collections.synchronizedSet(new TreeSet<>());

    public InMemoryNodeContext(StudentRepository repository, NodeConfiguration nodeConfiguration) {
        this.nodeConfiguration = nodeConfiguration;
        students.addAll(repository.findAll());
    }

    @Override
    public int getCollectionSize() {
        return students.size();
    }

    @Override
    public int getNumberOfConnections() {
        return nodeConfiguration.getPeerNodes().size();
    }
}

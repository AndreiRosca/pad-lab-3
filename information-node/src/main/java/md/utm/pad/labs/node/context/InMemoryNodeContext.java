package md.utm.pad.labs.node.context;

import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.node.config.NodeConfiguration;
import md.utm.pad.labs.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryNodeContext implements NodeContext {
    private final NodeConfiguration nodeConfiguration;
    private List<Student> students = Collections.synchronizedList(new ArrayList<>());

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

    @Override
    public int getNodePort() {
        return nodeConfiguration.getConsumerTcpPort();
    }

    @Override
    public List<Student> getAll() {
        return students;
    }
}

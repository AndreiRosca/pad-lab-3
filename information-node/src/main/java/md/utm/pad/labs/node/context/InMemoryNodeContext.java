package md.utm.pad.labs.node.context;

import md.utm.pad.labs.domain.Student;
import md.utm.pad.labs.node.config.NodeConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryNodeContext implements NodeContext {
    private final NodeConfiguration nodeConfiguration;
    private List<Student> students = Collections.synchronizedList(new ArrayList<>());

    public InMemoryNodeContext(NodeConfiguration nodeConfiguration) {
        this.nodeConfiguration = nodeConfiguration;
        loadData();
    }

    private void loadData() {
        String dataFile = nodeConfiguration.getDataFile();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(dataFile)))) {
            List<Student> students = reader.lines()
                    .map(Student::fromCsvString)
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
            this.students = Collections.synchronizedList(students);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public List<URI> getPeerNodes() {
        return nodeConfiguration.getPeerNodes();
    }
}

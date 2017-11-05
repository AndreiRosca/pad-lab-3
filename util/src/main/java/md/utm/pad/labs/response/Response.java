package md.utm.pad.labs.response;

public class Response implements Comparable<Response> {
    private String type;
    private int collectionSize;
    private int numberOfConnections;
    private String nodeAddress;
    private int nodePort;

    protected Response() {
    }

    public Response(ResponseType type, int collectionSize, int numberOfConnections) {
        this.type = type.toString();
        this.collectionSize = collectionSize;
        this.numberOfConnections = numberOfConnections;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCollectionSize() {
        return collectionSize;
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }

    public Integer getNumberOfConnections() {
        return numberOfConnections;
    }

    public void setNumberOfConnections(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;

        Response response = (Response) o;

        if (collectionSize != response.collectionSize) return false;
        if (numberOfConnections != response.numberOfConnections) return false;
        return type != null ? type.equals(response.type) : response.type == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + collectionSize;
        result = 31 * result + numberOfConnections;
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type='" + type + '\'' +
                ", collectionSize=" + collectionSize +
                ", numberOfConnections=" + numberOfConnections +
                '}';
    }

    @Override
    public int compareTo(Response other) {
        return -Integer.compare(numberOfConnections, other.numberOfConnections);
    }
}

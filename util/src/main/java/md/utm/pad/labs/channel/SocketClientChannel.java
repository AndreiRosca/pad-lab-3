package md.utm.pad.labs.channel;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientChannel implements ClientChannel {
    private static final Logger LOGGER = Logger.getLogger(SocketClientChannel.class);

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public SocketClientChannel(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(String data) {
        writer.println(data);
        writer.println();
        writer.flush();
    }

    @Override
    public void close() {
        closeResource(reader);
        closeResource(writer);
        closeResource(socket);
    }

    private void closeResource(AutoCloseable resource) {
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.error("Can't close the AutoCloseable", e);
        }
    }
}

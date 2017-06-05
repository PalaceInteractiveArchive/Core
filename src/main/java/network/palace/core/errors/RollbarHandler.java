package network.palace.core.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import network.palace.core.Core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Innectic
 * @since 6/4/2017
 */
@RequiredArgsConstructor
public class RollbarHandler {

    private final String accessToken;
    private final EnvironmentType environment;
    private String URL_STRING = "https://api.rollbar.com/api/1/item/";
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Watch for unhandled errors
     */
    public void watch() {
        Thread.getAllStackTraces().keySet().forEach(thread -> thread.setUncaughtExceptionHandler((t, e) -> {
            error(e);
        }));
    }

    public void info(String message) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("info", message, null)));
    }

    public void info(Throwable throwable) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("info", null, throwable)));
    }

    public void warning(String message) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("warning", message, null)));
    }

    public void warning(Throwable throwable) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("warning", null, throwable)));
    }

    public void error(String message) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("error", message, null)));
    }

    public void error(Throwable throwable) {
        Core.runTaskAsynchronously(() -> post(URL_STRING, build("error", null, throwable)));
    }

    private ObjectNode build(String level, String message, Throwable throwable) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        payload.put("access_token", this.accessToken);
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("environment", environment.getType());
        data.put("level", level);
        data.put("language", "Java");
        data.put("framework", "Java");
        data.put("timestamp", System.currentTimeMillis() / 1000);
        data.put("user_ip", getIP());
        data.set("body", getBody(message, throwable));
        payload.set("data", data);
        return payload;
    }

    private ObjectNode getBody(String message, Throwable original) {
        // Body node
        ObjectNode body = JsonNodeFactory.instance.objectNode();
        // Copy throwable
        Throwable throwable = original;
        // Loop through throwable and add it to trace chain
        if (throwable != null) {
            List<ObjectNode> traces = new ArrayList<>();
            do {
                traces.add(0, createTrace(throwable));
                throwable = throwable.getCause();
            } while (throwable != null);
            ArrayNode tracesArray = JsonNodeFactory.instance.arrayNode();
            traces.forEach(tracesArray::add);
            body.set("trace_chain", tracesArray);
        }
        // If throwable is null and message exist then add message to body
        if (original == null && message != null) {
            ObjectNode messageBody = JsonNodeFactory.instance.objectNode();
            messageBody.put("body", message);
            body.set("message", messageBody);
        }
        return body;
    }

    private ObjectNode createTrace(Throwable throwable) {
        ObjectNode trace = JsonNodeFactory.instance.objectNode();
        ArrayNode frames = JsonNodeFactory.instance.arrayNode();
        StackTraceElement[] elements = throwable.getStackTrace();
        for (int i = elements.length - 1; i >= 0; --i) {
            StackTraceElement element = elements[i];
            ObjectNode frame = JsonNodeFactory.instance.objectNode();
            frame.put("class_name", element.getClassName());
            frame.put("filename", element.getFileName());
            frame.put("method", element.getMethodName());
            if (element.getLineNumber() > 0) {
                frame.put("lineno", element.getLineNumber());
            }
            frames.add(frame);
        }
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArray);
            throwable.printStackTrace(printStream);
            printStream.close();
            byteArray.close();
            trace.put("raw", byteArray.toString("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObjectNode exceptionData = JsonNodeFactory.instance.objectNode();
        exceptionData.put("class", throwable.getClass().getName());
        exceptionData.put("message", throwable.getMessage());
        trace.set("frames", frames);
        trace.set("exception", exceptionData);
        return trace;
    }

    private void post(String url, Object data) {
        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            // Set
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11" );
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            // Write and send
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(mapper.writeValueAsBytes(data));
            dataOutputStream.flush();
            dataOutputStream.close();
            // Done
            connection.getInputStream();
        } catch (IOException ignored) {
        }
    }

    /**
     * Get the ip of the server
     *
     * @return the ip
     */
    private String getIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown IP";
        }
    }
}

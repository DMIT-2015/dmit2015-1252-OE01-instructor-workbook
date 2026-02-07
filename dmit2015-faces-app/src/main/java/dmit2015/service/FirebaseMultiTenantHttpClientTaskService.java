package dmit2015.service;

import dmit2015.faces.FirebaseAuthSignInSession;
import dmit2015.model.Task;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * This class implements the StudentService using the HttpClient library
 * to send Http Request to the Firebase Realtime Database REST API.
 * <p>
 * Verify that you have set up the following Rules in your Firebase Realtime Database
 * where the multi-tenant data is in a root path name "multi_tenant_data".
 * <p>
 * {
 * "rules": {
 * "multi_tenant_data": {
 * "Task": {
 * "$uid": {
 * // Allow only authenticated content owners access to their data
 * ".read": "auth !== null && auth.uid === $uid",
 * ".write": "auth !== null && auth.uid === $uid"
 * }
 * }
 * }
 * }
 * }
 */

@Named("firebaseMultiTenantHttpClientTaskService")
@ApplicationScoped
public class FirebaseMultiTenantHttpClientTaskService implements TaskService {
    /**
     * The base URL to the Firebase Realtime Database that is defined in
     * `src/main/resources/META-INF/microprofile-config.properties` file.
     *
     */
    @Inject
    @ConfigProperty(name = "firebase.rtdb.Task.base.url")
    private String firebaseRtdbBaseUrl;

    /**
     * The URL to the Firebase Realtime Database to access all data.
     */
    private String jsonAllDataPath;

    /**
     * HttpClient is native Java library for sending Http Request to a web server
     */
    private HttpClient httpClient;

    /**
     * Jsonb is used for converting Java objects to a JSON string or visa-versa
     */
    private Jsonb jsonb;

    @Inject
    private FirebaseAuthSignInSession firebaseAuthSignInSession;

    private static final String MULTI_TENANT_PATH_PREFIX = "multi_tenant_data";

    @PostConstruct
    private void init() {
        httpClient = HttpClient.newHttpClient();
        jsonb = JsonbBuilder.create();
    }

    /**
     * Pushing currentTask data to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public Task createTask(Task task) {

        // Get the Firebase Authenticated userId and token.
        String firebaseUserId = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        // Set the path in the database for content-owner access only data
        jsonAllDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                Task.class.getSimpleName(),
                firebaseUserId,
                firebaseToken);

        // Convert the Java object to a JSON string using JSONB
        String requestBodyJson = jsonb.toJson(task);

        // Create a Http Request for sending a Http POST request to push new data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(jsonAllDataPath))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();
        try {
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request response is successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                // Convert the JSON String to a JsonObject
                JsonObject responseJsonObject = jsonb.fromJson(responseBodyJson, JsonObject.class);
                // Set the unique key name for this object
                task.setId(responseJsonObject.getString("name"));

            } else {
                String errorMessage = String.format("Create was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        // Build the url path to object to access
        String firebaseUserId = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                Task.class.getSimpleName(),
                firebaseUserId,
                id,
                firebaseToken);

        try {
            // Create an GET Http Request to fetch all data
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(jsonSingleDataPath))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            // Send the GET Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request was successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                if (!responseBodyJson.equals("null")) {
                    // Convert the responseBodyJson to an LinkedHashMap<String, Task>
                    Task responseData = jsonb.fromJson(responseBodyJson, Task.class);
                    responseData.setId(id);
                    return Optional.of(responseData);
                } else {
                    throw new RuntimeException(String.format("Task with id of %s not found", id));
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> getAllTasks() {
        // Get the Firebase Authenticated userId and token.
        String firebaseUserId = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        // Set the path in the database for content-owner access only data
        jsonAllDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                Task.class.getSimpleName(),
                firebaseUserId,
                firebaseToken);

        // Create an GET Http Request to fetch all data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(jsonAllDataPath))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            // Send the GET Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request was successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                // Convert the responseBodyJson to an LinkedHashMap<String, Task>
                LinkedHashMap<String, Task> responseData = jsonb.fromJson(responseBodyJson, new LinkedHashMap<String, Task>() {
                }.getClass().getGenericSuperclass());
                if (responseData != null) {
                    // Convert the LinkedHashMap<String, Task> to List<Task>
                    return responseData.entrySet()
                            .stream()
                            .map(item -> {
                                var currentTask = new Task();
                                currentTask.setId(item.getKey());

                                currentTask.setDescription(item.getValue().getDescription());
                                 currentTask.setPriority(item.getValue().getPriority());
                                 currentTask.setDone(item.getValue().isDone());

                                return currentTask;
                            })
                            .toList();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    /**
     * Writing currentTask data to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public Task updateTask(Task task) {
        // Build the url path to object to access
        String firebaseUserId = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                Task.class.getSimpleName(),
                firebaseUserId,
                task.getId(),
                firebaseToken);

        // Convert the Java object to a JSON string using JSONB
        String requestBodyJson = jsonb.toJson(task);

        // Create and Http Request to send an HTTP PUT request to write over existing data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(jsonSingleDataPath))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();
        try {
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Response was successful
            if (httpResponse.statusCode() != 200) {
                String errorMessage = String.format("Update was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    /**
     * Remove data from Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public void deleteTaskById(String id) {
        // Build the url path to object to access
        String firebaseUserId = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                Task.class.getSimpleName(),
                firebaseUserId,
                id,
                firebaseToken);

        // Create an DELETE Http Request
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(jsonSingleDataPath))
                .DELETE()
                .build();
        try {
            // Send the DELETE Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Response was successful
            if (httpResponse.statusCode() != 200) {
                String errorMessage = String.format("Delete was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
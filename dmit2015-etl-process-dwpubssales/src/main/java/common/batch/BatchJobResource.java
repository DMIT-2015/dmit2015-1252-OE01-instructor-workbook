package common.batch;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.operations.JobStartException;
import jakarta.batch.operations.NoSuchJobException;
import jakarta.batch.operations.NoSuchJobExecutionException;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.batch.runtime.JobExecution;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@Path("batch-jobs")
@Produces(MediaType.APPLICATION_JSON)
public class BatchJobResource {

    private static final Logger LOG = Logger.getLogger(BatchJobResource.class.getName());

    private final JobOperator jobOperator = BatchRuntime.getJobOperator();

    @POST
    @Path("{filename}")
    public Response startBatchJob(@PathParam("filename") String jobXMLName, @Context UriInfo uriInfo) {
        // Basic validation
        if (jobXMLName == null || jobXMLName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Missing job name or filename in path."))
                    .build();
        }

        // Normalize: strip ".xml" if caller passed a filename
        String normalizedJobName = normalizeJobName(jobXMLName);

        // Check that the JSL actually exists on the classpath
        if (!jobXmlExists(normalizedJobName)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of(
                            "error", "Job XML not found",
                            "details", "/META-INF/batch-jobs/" + normalizedJobName + ".xml",
                            "hint", "Ensure the JSL file is packaged and the <job id> matches the name you are starting."
                    )).build();
        }

        // Start the job
        try {
            long jobId = jobOperator.start(normalizedJobName, null);

            URI location = uriInfo.getBaseUriBuilder()
                    .path(BatchJobResource.class)
                    .path(BatchJobResource.class, "getBatchStatus")
                    .build(jobId);

            return Response.created(location)
                    .entity(Map.of(
                            "jobId", jobId,
                            "jobName", normalizedJobName,
                            "status", "STARTED",
                            "location", location.toString()))
                    .build();

        } catch (NoSuchJobException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No such job", "jobName", normalizedJobName))
                    .build();
        } catch (JobStartException e) {
            return Response.status(Response.Status.CONFLICT) // starting invalid state / already complete parameters, etc.
                    .entity(Map.of("error", "Unable to start job", "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.severe("Unexpected error starting job: " + e.getMessage());
            return Response.serverError()
                    .entity(Map.of("error", "Unexpected error", "message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("{id}")
    public Response getBatchStatus(@PathParam("id") Long jobId) {
        try {
            JobExecution exec = jobOperator.getJobExecution(jobId);
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("jobId", exec.getExecutionId());
            payload.put("jobName", exec.getJobName());
            payload.put("batchStatus", exec.getBatchStatus().toString());
            payload.put("exitStatus", exec.getExitStatus());
            payload.put("startTime", toInstant(exec.getStartTime()));
            payload.put("endTime", toInstant(exec.getEndTime()));
            payload.put("createTime", toInstant(exec.getCreateTime()));
            payload.put("lastUpdateTime", toInstant(exec.getLastUpdatedTime()));
            return Response.ok(payload).build();
        } catch (NoSuchJobExecutionException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No such job execution", "jobId", jobId))
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("jobnames")
    public Response getJobNames() {
        try {
            Set<String> jobNames = jobOperator.getJobNames();
            return Response.ok(jobNames).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    private static String normalizeJobName(String input) {
        String trimmed = input.trim();
        if (trimmed.endsWith(".xml")) {
            trimmed = trimmed.substring(0, trimmed.length() - 4);
        }
        return trimmed;
    }

    /** Check if /META-INF/batch-jobs/<jobName>.xml exists on the classpath. */
    private static boolean jobXmlExists(String jobName) {
        String path = "/META-INF/batch-jobs/" + jobName + ".xml";
        return BatchJobResource.class.getResource(path) != null;
    }

    private static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }
}
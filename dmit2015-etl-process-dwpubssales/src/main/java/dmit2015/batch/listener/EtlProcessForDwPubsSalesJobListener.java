package dmit2015.batch.listener;

import jakarta.batch.api.listener.JobListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This listener contains methods that executes before and after a job execution runs.
 * To apply this listener to a batch job you must define a listener element in the Job Specification Language (JSL) file
 * BEFORE the first step element as follows:
 * <pre>{@code
 *
 * <listeners>
 *      <listener ref="etlProcessForDwPubsSalesJobListener" />
 * </listeners>
 *
 * }</pre>
 */
@Named
@Dependent
public class EtlProcessForDwPubsSalesJobListener implements JobListener {

    @Inject
    private JobContext jobContext;

    @Inject
    private Logger logger;// = Logger.getLogger(EtlProcessForDwPubsSalesJobListener.class.getName());

    private long startTime;

    @Override
    public void beforeJob() throws Exception {
        logger.log(Level.INFO, "[{0}] beforeJob", jobContext.getJobName());
        startTime = System.currentTimeMillis();

    }

    @Override
    public void afterJob() throws Exception {
        logger.log(Level.INFO, "[{0}] afterJob", jobContext.getJobName());
        long endTime = System.currentTimeMillis();
        long durationMillisecond = (endTime - startTime);
        logger.log(Level.INFO, "{0} completed in {1} milliseconds",
                new Object[]{jobContext.getJobName(),
                        durationMillisecond});
    }

}
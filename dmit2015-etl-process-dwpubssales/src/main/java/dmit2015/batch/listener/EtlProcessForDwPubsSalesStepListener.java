package dmit2015.batch.listener;

import jakarta.batch.api.listener.StepListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Intercepts step execution before and after running a step.
 * It is referenced from the listener element inside the step element
 * To apply this listener to a batch job you must define a listener element in the Job Specification Language (JSL) file
 * INSIDE the step element as follows:
 * <pre>{@code
 * 	<listeners>
 * 		<listener ref="etlProcessForDwPubsSalesStepListener" />
 * 	</listeners>
 * }</pre
 */
@Named
@Dependent
public class EtlProcessForDwPubsSalesStepListener implements StepListener {

    @Inject
    private JobContext jobContext;

    @Inject
    private Logger logger;

    private long startTime;

    @Override
    public void beforeStep() {
        logger.log(Level.INFO, "[{0}] beforeStep", jobContext.getJobName());
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterStep() {
        long endTime = System.currentTimeMillis();
        long durationMilliseconds = endTime - startTime;

        logger.log(Level.INFO, "[{0}] afterStep", jobContext.getJobName());
        logger.log(Level.INFO, "[{0}] Step completed in {1} milliseconds",
                new Object[]{jobContext.getJobName(), durationMilliseconds});
    }

}
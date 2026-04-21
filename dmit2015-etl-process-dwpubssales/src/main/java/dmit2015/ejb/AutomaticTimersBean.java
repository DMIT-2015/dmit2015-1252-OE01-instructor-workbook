package dmit2015.ejb;

import common.ejb.EmailSessionBean;
import jakarta.annotation.Resource;
import jakarta.ejb.Schedule;
import jakarta.ejb.Schedules;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timer;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton		// Instruct the container to create a single instance of this EJB
@Startup		// Create this EJB is created when this app starts
public class AutomaticTimersBean {	// Also known as Calendar-Based Timers

	private Logger logger = Logger.getLogger(AutomaticTimersBean.class.getName());

	/**
	 * Assuming you have define the following entries in your src/main/META-INF/microprofile-config.properties file
     	ca.dmit2015.config.sysadmin_email=yourUsername@yourEmailServer

	 * This code assumes that this project is configured to use Eclipse Microprofile.
	 * You can add the following to pom.xml to enable Eclipse Microprofile

        <dependency>
            <groupId>org.eclipse.microprofile.config</groupId>
            <artifactId>microprofile-config-api</artifactId>
            <version>3.1</version>
        </dependency>

	 */
	@Inject
    @ConfigProperty(name="ca.dmit2015.config.sysadmin_email", defaultValue = "webadmin@dmit2015.ca")
	private String mailToAddress;

	@Inject
	private EmailSessionBean mail;

	private void sendEmail(Timer timer) {
		if (!mailToAddress.isBlank()) {
			String mailSubject = timer.getInfo().toString();
			String mailText = String.format("You have a %s on %s %s, %s  ",
				timer.getInfo().toString(),
				timer.getSchedule().getDayOfWeek(),
				timer.getSchedule().getMonth(),
				timer.getSchedule().getYear()
			);
			try {
				mail.sendTextEmail(mailToAddress, mailSubject, mailText);
				logger.info("Successfully sent email to " + mailToAddress);
			} catch (Exception e) {
                logger.log(Level.SEVERE, "sendMail failed", e);
			}
		}
	}

	// @Schedules({
	// 	@Schedule(second = "0", minute ="50", hour = "12", dayOfWeek = "Tue,Wed", month = "Sep-Dec", year = "2025", info ="DMIT2015-A01 In Person Meeting", persistent = false),
	// 	@Schedule(second = "0", minute ="50", hour = "9", dayOfWeek = "Mon", month = "Sep-Dec", year = "2025", info ="DMIT2015-A01 In Person Meeting", persistent = false)
	// })
	public void dmit2015SectionA01ClassNotifiation(Timer timer) {
		sendEmail(timer);
	}

	@Schedule(second = "0", minute ="26,28,30", hour = "18",
			dayOfWeek = "Mon,Wed,Fri",
			month = "Jan-Apr",
			year = "2026", info ="DMIT2015-OE01 Online Meeting", persistent = false)
	public void dmit2015SectionOE01ClassNotification(Timer timer) {
		sendEmail(timer);
	}

	// @Schedules({
	// 	@Schedule(second = "0", minute ="50", hour = "18", dayOfWeek = "Mon,Wed", month = "Sep-Dec", year = "2025", info ="DMIT2015-E01 In Person Meeting", persistent = false),
	// 	@Schedule(second = "0", minute ="50", hour = "18", dayOfWeek = "Fri", month = "Sep-Dec", year = "2025", info ="DMIT2015-E01 Online Meeting", persistent = false)
	// })
	public void dmit2015SectionE01ClassNotifiation(Timer timer) {
		sendEmail(timer);
	}

}
package cvmi.fipm.session;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import cvmi.fipm.constants.Constants;

@Singleton
@Startup
public class ScheduleBean implements Schedule {

	private final static Logger logger = Logger.getLogger(ScheduleBean.class);

	@Resource
	private TimerService timerService;

	@EJB
	private Fipm fipm;

	@Inject
	@ConfigProperty(name = Constants.FIPM_ENVIRONMENT_CONFIGURATION)
	private String environment;

	/*
	 * @Inject
	 * 
	 * @ConfigProperty(name = Constants.FIPM_SCHEDULE_HOUR)
	 */
	private String hourSchedule = "*";

	@Inject
	@ConfigProperty(name = Constants.FIPM_SCHEDULE_MINUTE)
	private String minuteSchedule = "*";

	@Inject
	@ConfigProperty(name = Constants.FIPM_SCHEDULE_SECONDS)
	private String secondSchedule;

	public ScheduleBean() {

	}

	// Server configured for environment:
	@PostConstruct
	public void init() {
		logger.info("Server configured for environment: " + environment);
		ScheduleExpression scheduleExpression = new ScheduleExpression();
		scheduleExpression.second("*/" + secondSchedule).minute("*").hour("*");
		timerService.createCalendarTimer(scheduleExpression);
		logger.debug("Timer Service initialization complete.");
	}

	@Timeout
	public void execute() {
		//fipm.processSaFiPurchases();
		//fipm.testData();
	}

}

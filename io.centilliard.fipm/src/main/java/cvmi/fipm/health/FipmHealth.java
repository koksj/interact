package cvmi.fipm.health;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import cvmi.fipm.constants.Constants;



@Health
@ApplicationScoped
public class FipmHealth implements HealthCheck {
	
	@EJB
	private HealthInf health;
		
	public FipmHealth() {		
	
	}		
	
	@Override
	public HealthCheckResponse call() {
	
		HealthCheckResponseBuilder  healthCheckResponseBuilder = HealthCheckResponse.named("FIPM Manager")			
				   .withData(Constants.FIPM_ENVIRONMENT_CONFIGURATION,health.getEnvironment())
				   .withData(Constants.INTERACT_INTERACTION_POINT, health.getInteractInteractionPoint())
				   .withData(Constants.INTERACT_INTERACTIVE_CHANNEL, health.getInteractInteractiveChannel())
				   .withData(Constants.NDE_CAMPAIGN_TYPE, health.getNdeCampaignType())
				   .withData(Constants.NDE_SUB_TYPE, health.getNdeSubType())
				   .withData(Constants.NDE_NOTIFICATION_SOURCE, health.getNdeNotificationSource())
				   .withData(Constants.NDE_PAYMENT_METHOD,health.getNdePaymentMethod())
				   .withData(Constants.NDE_TEMPLATE_ID, health.getNdeTemplateId())
				   .withData(Constants.NDE_PARAM_OFFER_ADVICE, health.getNdeParamOfferAdvice());
				   				   
	        return healthCheckResponseBuilder.up().build();
	}

}

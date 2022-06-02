package cvmi.fipm.health;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import cvmi.fipm.constants.Constants;

@Stateless
public class HealthSessionBean implements HealthInf {
		
	@Inject
	@ConfigProperty(name = Constants.FIPM_ENVIRONMENT_CONFIGURATION)
	private String environment;
	
	@Inject
	@ConfigProperty(name = Constants.INTERACT_INTERACTION_POINT)
	private String interactInteractionPoint;
	
	@Inject
	@ConfigProperty(name = Constants.INTERACT_INTERACTIVE_CHANNEL)
	private String interactInteractiveChannel;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_CAMPAIGN_TYPE)
	private String ndeCampaignType;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_SUB_TYPE)
	private String ndeSubType;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_NOTIFICATION_SOURCE)
	private String ndeNotificationSource;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_PAYMENT_METHOD)
	private String ndePaymentMethod;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_TEMPLATE_ID)
	private String ndeTemplateId;
	
	@Inject
	@ConfigProperty(name = Constants.NDE_PARAM_OFFER_ADVICE)
	private String ndeParamOfferAdvice;
	
	public HealthSessionBean() {
		// Auto-generated constructor stub
	}
	
	@Override
	public String getEnvironment() {
		return environment;
	}
	
	@Override
	public String getInteractInteractionPoint() {
		return interactInteractionPoint;
	}

	@Override
	public String getInteractInteractiveChannel() {
		return interactInteractiveChannel;
	}

	@Override
	public String getNdeSubType() {
		return ndeSubType;
	}

	@Override
	public String getNdeNotificationSource() {
		return ndeNotificationSource;
	}

	@Override
	public String getNdePaymentMethod() {
		return ndePaymentMethod;
	}

	@Override
	public String getNdeTemplateId() {
		return ndeTemplateId;
	}

	@Override
	public String getNdeParamOfferAdvice() {
		return ndeParamOfferAdvice;
	}

	@Override
	public String getNdeCampaignType() {
		
		return ndeCampaignType;
	}
}

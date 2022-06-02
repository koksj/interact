package cvmi.fipm.nde;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cvmi.fipm.constants.Constants;

// Message
@ApplicationScoped
public class NdeMessageImpl implements NdeMessage {

	private final static Logger logger = Logger.getLogger(NdeMessageImpl.class);

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
	private String offerAdvice;

	public NdeMessageImpl() {
		// Auto-generated constructor stub
	}

	@Override
	@Asynchronous
	public Future<String> getNdeNotification(String msisdn, Map<String, String> values) {

		List<NdeAttribute> attributes = new ArrayList<>();
		values.forEach((key, value) -> {
			attributes.add(new NdeAttribute(key, value));
		});

		NdeNotification ndeNotification = new NdeNotification();
		ndeNotification.setType(ndeCampaignType);
		ndeNotification.setSubType(ndeSubType);
		ndeNotification.setSource(ndeNotificationSource);
		ndeNotification.setDestination(msisdn);
		ndeNotification.setPaymentMethod("PREPAID"); // PREPAID
		ndeNotification.setTemplateId(ndeTemplateId);
		ndeNotification.setTransactionId(UUID.randomUUID().toString()); // Random id as per specification
		ndeNotification.setAttributes(attributes.toArray(new NdeAttribute[attributes.size()]));
		// ndeNotification.setConditions(new NdeAttribute[0]);

		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(ndeNotification);
			json = new String("[" + new String(json.getBytes()) + "]");
		} catch (JsonProcessingException e) {
			logger.error("JsonProcessingException Message: " + e.getMessage());
			logger.error("JsonProcessingException Cause: " + e.getCause());
		}

		return new AsyncResult<>(json);
	}

	@Override
	@Asynchronous
	public Future<Map<String, String>> processResponse(Future<Response> fresponse) {

		Response response = null;
		try {
			response = fresponse.get();
		} catch (InterruptedException e1) {
			logger.error("InterruptedException: " + e1);
		} catch (ExecutionException e1) {
			logger.error("ExecutionException: " + e1);
		}

		Map<String, String> attributes = new HashMap<>();

		String jsonResponse = "";

		if (response != null) {

			Object entity = response.getEntity();

			if (entity == null) {
				logger.error("response.getEntity() returend a null value.");
			} else {

				if (entity instanceof String) {
					jsonResponse = (String) entity;
				} else {
					jsonResponse = response.readEntity(String.class);
					jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
				}

			}

			JsonReader jsonReader = null;
			JsonObject jsonObject = null;
			try {
				jsonReader = Json.createReader(new StringReader(jsonResponse));
				jsonObject = jsonReader.readObject();
				jsonReader.close();
			} catch (JsonParsingException e) {
				logger.error("NDE response not in valid json format: " + jsonResponse);
			}

			if (jsonObject == null) {
				logger.error("jsonObject is nulll");
			} else {
				if (jsonObject.containsKey("code")) {
					logger.info("MessageId :" + jsonObject.getString("code") + " failed.");
					jsonObject.forEach((key, value) -> {
						logger.info("Key: " + key + " Value: " + value);
						attributes.put(key, value.toString());
					});
				} else {
					// The message to NDE was a success
					logger.info("MessageId: " + jsonObject.getString("messageId") + " successful");
					jsonObject.forEach((key, value) -> {
						logger.info("Key: " + key + " Value: " + value);
						attributes.put(key, value.toString());
					});
				}
			}

		}
		
		return new AsyncResult<>(attributes);
	}

}

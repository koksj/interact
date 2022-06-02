/**
 * 
 */
package cvmi.fipm.ineract;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.resource.ResourceException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import com.unicacorp.interact.api.soap.ExecuteBatch;
import com.unicacorp.interact.api.soap.ExecuteBatchResponse;
import com.unicacorp.interact.api.xsd.BatchResponse;
import com.unicacorp.interact.api.xsd.CommandImpl;
import com.unicacorp.interact.api.xsd.Offer;
import com.unicacorp.interact.api.xsd.OfferList;
import com.unicacorp.interact.api.xsd.Response;

import cvmi.fipm.builder.GenericBuilder;
import cvmi.fipm.commands.Qbziof;
import cvmi.fipm.constants.BatchStatusCodes;
import cvmi.fipm.constants.Constants;
import cvmi.fipm.entity.SaFiPurchase;
import cvmi.fipm.soap.ProvisioningCmdFactory;
import ibm.interact.soap.api.InteractConnection;
import ibm.interact.soap.api.InteractConnectionFactory;

/**
 * @author skok
 *
 */

@Stateless
public class InteractSessionBean implements Interact, logBatchResponse {

	private final static Logger logger = Logger.getLogger(InteractSessionBean.class);

	@Inject
	@ConfigProperty(name = Constants.INTERACT_INTERACTION_POINT)
	private String interactionPoint;

	@Inject
	@ConfigProperty(name = Constants.INTERACT_INTERACTIVE_CHANNEL)
	private String interactiveChannel;

	@Resource(name = "InteractConnectionFactory", type = ibm.interact.soap.api.InteractConnectionFactory.class, authenticationType = AuthenticationType.CONTAINER, mappedName = "java:/eis/InteractConnectionFactory")
	private InteractConnectionFactory interactConnectionFactory;

	public InteractSessionBean() {
		// Auto-generated constructor stub
	}

	@Override
	@Asynchronous
	public Future<List<CommandImpl>> buildQbziofCommandImpls(SaFiPurchase saFiPurchase) {

		String msisdn = saFiPurchase.getMsisdn();
		String tenDigit = "0" + msisdn.substring(2, msisdn.length());

		double data_size = 0;
		if (saFiPurchase.getData_size() != null) {
			data_size = saFiPurchase.getData_size().doubleValue() / 1024;
		}

		Qbziof qbziof = GenericBuilder.of(Qbziof::new).with(Qbziof::setSessionId, saFiPurchase.getSession_id())
				.with(Qbziof::setMsisdn, tenDigit).with(Qbziof::setValidity, saFiPurchase.getExp_date())
				.with(Qbziof::setPurchasedBundlePrice, saFiPurchase.getAmount()).with(Qbziof::setAllocation, data_size) // convert
																														// bytes
																														// to
																														// kilobytes
				.with(Qbziof::setInteractionPoint, interactionPoint)
				.with(Qbziof::setInteractiveChannel, interactiveChannel).with(Qbziof::setNumberRequested, 1L).build();

		Supplier<ProvisioningCmdFactory> provisioningCmdFactory = ProvisioningCmdFactory::new;
		final List<CommandImpl> commands = provisioningCmdFactory.get().getCommand(Constants.QBZIOF_KEY)
				.buildQbziofProvisioningCmd(qbziof);

		return new AsyncResult<>(commands);
	}

	@Override
	public List<CommandImpl> buildQbziofCommandImpl(SaFiPurchase saFiPurchase) {

		String msisdn = saFiPurchase.getMsisdn();
		String tenDigit = "0" + msisdn.substring(2, msisdn.length());

		// convert bytes to kilobytes
		double data_size = 0;
		if (saFiPurchase.getData_size() != null) {
			data_size = saFiPurchase.getData_size().doubleValue() / 1024;
		}

		Qbziof qbziof = GenericBuilder.of(Qbziof::new).with(Qbziof::setSessionId, saFiPurchase.getSession_id())
				.with(Qbziof::setMsisdn, tenDigit).with(Qbziof::setValidity, saFiPurchase.getExp_date())
				.with(Qbziof::setPurchasedBundlePrice, saFiPurchase.getAmount()).with(Qbziof::setAllocation, data_size)
				.with(Qbziof::setInteractionPoint, interactionPoint)
				.with(Qbziof::setInteractiveChannel, interactiveChannel).with(Qbziof::setNumberRequested, 1L).build();

		Supplier<ProvisioningCmdFactory> provisioningCmdFactory = ProvisioningCmdFactory::new;
		final List<CommandImpl> commands = provisioningCmdFactory.get().getCommand(Constants.QBZIOF_KEY)
				.buildQbziofProvisioningCmd(qbziof);

		return commands;
	}

	// @Timed(name = "sendBatchToInteract", description = "Metrics to monitor the
	// response times of IBM Interact in milliseconds..", unit =
	// MetricUnits.MILLISECONDS, absolute = true)
	@Override
	@Asynchronous
	public Future<BatchResponse> sendBatchToInteract(final String sessionId,
			final Future<List<CommandImpl>> fCommands) {

		List<CommandImpl> commands = new ArrayList<>();
		try {
			if (fCommands.get() == null) {
				logger.error(" Received a null value for a parameter on sessionID: " + sessionId);
			} else {
				commands.addAll(fCommands.get());
			}
		} catch (InterruptedException e1) {
			logger.error("InterruptedException: ", e1);
		} catch (ExecutionException e1) {
			logger.error("ExecutionException: ", e1);
		}

		InteractConnection connection = null;
		BatchResponse batchResponse = null;
		try {

			connection = interactConnectionFactory.getConnection();

			if (commands.isEmpty()) {
				logger.error(
						"No commands received for session id: " + sessionId + " not sending any request to Interact");
			} else {

				batchResponse = connection.executeBatch(sessionId, commands);

			}

			if (batchResponse == null) {
				logger.info("BatchResponse returned is null.");
			}
		} catch (ResourceException e) {
			logger.error("ResourceException Message: " + e.getMessage());
			logger.error("ResourceException Cause: " + e.getCause());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		if (logger.isInfoEnabled()) {
			logRequest(sessionId, commands);
			logResponse(batchResponse);
		}

		return new AsyncResult<BatchResponse>(batchResponse);
	}

	@Timed(name = "sendBatchToInteract", description = "Metrics to monitor the response times of IBM Interact in milliseconds..", unit = MetricUnits.MILLISECONDS, absolute = true)
	@Override
	public BatchResponse sendBatchToInteract(final String sessionId, final List<CommandImpl> commandsImpl) {

		List<CommandImpl> commands = new ArrayList<>();

		if (commandsImpl == null) {
			logger.error(" Received a null value for a parameter on sessionID: " + sessionId);
		} else {
			commands.addAll(commandsImpl);
		}

		InteractConnection connection = null;
		BatchResponse batchResponse = null;
		try {
			connection = interactConnectionFactory.getConnection();

			if (connection == null) {
				logger.error("InteractConnection connection is null");
			} else {

				if (commands.isEmpty()) {
					logger.error("No commands received for session id: " + sessionId
							+ " not sending any request to Interact");
				} else {
					batchResponse = connection.executeBatch(sessionId, commands);
				}

				if (batchResponse == null) {
					logger.info("BatchResponse returned is null.");
				}
			}

		} catch (ResourceException e) {
			logger.error("ResourceException Message: " + e.getMessage());
			logger.error("ResourceException Cause: " + e.getCause());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		if (logger.isInfoEnabled()) {
			logRequest(sessionId, commands);
			logResponse(batchResponse);
		}

		return batchResponse;
	}

	@Override
	@Asynchronous
	public Future<Map<String, String>> processBatchResponse(String sessionId, Future<BatchResponse> fBatchResponse) {

		BatchResponse batchResponse = new BatchResponse();
		try {
			if (fBatchResponse.get() == null) {
				logger.error("Received a null BatchResponse parameter");
			} else {
				batchResponse = fBatchResponse.get();
			}

		} catch (InterruptedException e) {
			logger.error("InterruptedException: ", e);
		} catch (ExecutionException e) {
			logger.error("ExecutionException: ", e);
		}

		if (batchResponse.getBatchStatusCode() == null) {
			batchResponse.setBatchStatusCode(-1);
			logger.error("BatchResponse.getBatchStatusCode() is null! Setting value to -1 fir sessionid: " + sessionId);
		}

		List<Response> responses = new ArrayList<>();
		if (batchResponse.getResponses() == null) {
			logger.error("batchResponse.getResponses() returned a null value for sessionid " + sessionId);
		} else {
			responses.addAll(batchResponse.getResponses());
		}
		Map<String, String> attributes = new HashMap<>();
		switch (batchResponse.getBatchStatusCode()) {

		case BatchStatusCodes.STATUS_SUCCESS:
			logger.debug("ExecuteBatch StatusCode returned success status for Session_id: " + sessionId);
			attributes.putAll(getAttributes(responses));
			break;

		case BatchStatusCodes.STATUS_WARNING:
			logger.warn("ExecuteBatch call processed with at least one warning for Session_id " + sessionId);
			processAdvisoryMessages(responses, "WARN");

			break;

		case BatchStatusCodes.STATUS_ERROR:
			logger.debug("ExecuteBatch StatusCode returned error status for Session_id: " + sessionId);
			processAdvisoryMessages(responses, "ERROR");
			break;

		default:
			logger.error("Failing Session_id " + sessionId + " on account of unknown batch status code "
					+ batchResponse.getBatchStatusCode());
			break;
		}

		return new AsyncResult<Map<String, String>>(attributes);
	}

	@Override
	public Map<String, String> processBatchResponse(String sessionId, BatchResponse fBatchResponse) {

		BatchResponse batchResponse = new BatchResponse();

		if (fBatchResponse == null) {
			logger.error("Received a null BatchResponse parameter");
		} else {
			batchResponse = fBatchResponse;
		}

		if (batchResponse.getBatchStatusCode() == null) {
			batchResponse.setBatchStatusCode(-1);
			logger.error("BatchResponse.getBatchStatusCode() is null! Setting value to -1 fir sessionid: " + sessionId);
		}

		List<Response> responses = new ArrayList<>();
		if (batchResponse.getResponses() == null) {
			logger.error("batchResponse.getResponses() returned a null value for sessionid " + sessionId);
		} else {
			responses.addAll(batchResponse.getResponses());
		}
		Map<String, String> attributes = new HashMap<>();
		switch (batchResponse.getBatchStatusCode()) {

		case BatchStatusCodes.STATUS_SUCCESS:
			logger.debug("ExecuteBatch StatusCode returned success status for Session_id: " + sessionId);
			attributes.putAll(getAttributes(responses));
			break;

		case BatchStatusCodes.STATUS_WARNING:
			logger.warn("ExecuteBatch call processed with at least one warning for Session_id " + sessionId);
			processAdvisoryMessages(responses, "WARN");

			break;

		case BatchStatusCodes.STATUS_ERROR:
			logger.debug("ExecuteBatch StatusCode returned error status for Session_id: " + sessionId);
			processAdvisoryMessages(responses, "ERROR");
			break;

		default:
			logger.error("Failing Session_id " + sessionId + " on account of unknown batch status code "
					+ batchResponse.getBatchStatusCode());
			break;
		}

		return attributes;
	}

	@Metered(name = "processAdvisoryMessages", unit = MetricUnits.MINUTES, description = "Metrics to monitor errors from IBM Interact.", absolute = false)
	private void processAdvisoryMessages(List<Response> responses, String level) {

		responses.forEach(response -> {

			response.getAdvisoryMessages().forEach(advisoryMsg -> {

				switch (level) {
				case "ERROR":
					logger.error("StatusLevel: " + advisoryMsg.getStatusLevel() + " MessageCode: "
							+ advisoryMsg.getMessageCode() + " Message: " + advisoryMsg.getMessage().getValue()
							+ " DetailMessage: " + advisoryMsg.getDetailMessage().getValue());
					break;

				case "WARN":
					logger.warn("StatusLevel: " + advisoryMsg.getStatusLevel() + " MessageCode: "
							+ advisoryMsg.getMessageCode() + " Message: " + advisoryMsg.getMessage().getValue()
							+ " DetailMessage: " + advisoryMsg.getDetailMessage().getValue());
					break;

				default:
					break;
				}

			});
		});
	}

	private Map<String, String> getAttributes(List<Response> responses) {

		Map<String, String> values = new HashMap<>();

		responses.forEach(response -> {
			JAXBElement<OfferList> offerList = response.getOfferList();
			if (offerList != null) {
				List<Offer> recommendedOffers = offerList.getValue().getRecommendedOffers();
				if (recommendedOffers != null) {
					recommendedOffers.forEach(offer -> {

						offer.getAdditionalAttributes().forEach(attribute -> {

							switch (attribute.getName().getValue()) {

							case Constants.ATTRIBUTE_NAME_VALUE:
								values.put("OFFER_DESCRIPTION", attribute.getValueAsString().getValue());
								break;

							default:
								break;
							}
						});
					}

					);
				}

			}
		});

		return values;
	}

	private void logResponse(BatchResponse batchResponse) {

		if (batchResponse == null) {
			logger.error("batchResponse parameter received is null.");
		} else {
			try {
				ExecuteBatchResponse executeBatchResponse = new ExecuteBatchResponse();

				JAXBElement<BatchResponse> value = new JAXBElement<BatchResponse>(
						new QName(Constants.INTERACT_NAME_SPACE_URI), BatchResponse.class, batchResponse);

				executeBatchResponse.setReturn(value);

				JAXBContext jaxbContext = JAXBContext.newInstance(ExecuteBatchResponse.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				ByteArrayOutputStream stream = new ByteArrayOutputStream(); // received
				marshaller.marshal(executeBatchResponse, stream);
				logger.debug("BatchResponse received: ");
				logger.debug(stream.toString());
			} catch (JAXBException e) {
				logger.error("JAXBException Message: " + e.getMessage());
				logger.error("JAXBException Cause: " + e.getCause());
			}
		}
	}

	// Request
	private void logRequest(String sessionID, List<CommandImpl> commandImpls) {
		try {

			ExecuteBatch executeBatch = new ExecuteBatch();
			executeBatch.setSessionID(sessionID);
			executeBatch.getCommands().addAll(commandImpls);

			JAXBContext jaxbContext = JAXBContext.newInstance(ExecuteBatch.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream stream = new ByteArrayOutputStream(); // received
			marshaller.marshal(executeBatch, stream);
			logger.debug("Request sent: ");
			logger.debug(stream.toString());
		} catch (JAXBException e) {
			logger.error("JAXBException Message: " + e.getMessage());
			logger.error("JAXBException Cause: " + e.getCause());
		}
	}

	@Override
	public Future<List<CommandImpl>> buildQbziofCommandImpls(List<SaFiPurchase> saFiPurchases) {
		// TODO Auto-generated method stub
		return null;
	}

}

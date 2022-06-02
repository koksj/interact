package cvmi.fipm.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.unicacorp.interact.api.xsd.BatchResponse;
import com.unicacorp.interact.api.xsd.CommandImpl;

import cvmi.fipm.entity.SaFiPurchase;
import cvmi.fipm.ineract.Interact;
import cvmi.fipm.nde.NdeMessage;

@Stateless
public class FipmSessionBean implements Fipm {

	private final static Logger logger = Logger.getLogger(FipmSessionBean.class);

	@Inject
	private Interact interact;

	@EJB
	private RestResource restResource;

	@Inject
	private NdeMessage ndeMessage;

	public FipmSessionBean() {
		// Auto-generated constructor stub
	}

	@Override
	@Asynchronous
	public void processSaFiPurchases(List<SaFiPurchase> saFiPurchases) {

		// MSISDN, RECHARGE_DATE, TRANSACTION_ID
		saFiPurchases.forEach(saFiPurchase -> {

			if (saFiPurchase.getSession_id() == null) {
				String digit = saFiPurchase.getMsisdn().substring(2, saFiPurchase.getMsisdn().length());
				saFiPurchase.setSession_id(digit);
				logger.warn("No session id received; substituting  MSISDN for session id : " + Integer.parseInt(digit));
			}

			Future<List<CommandImpl>> commandImpls = interact.buildQbziofCommandImpls(saFiPurchase);
			Future<BatchResponse> batchResponse = interact.sendBatchToInteract(saFiPurchase.getSession_id().toString(),
					commandImpls);
			Future<Map<String, String>> fAttributes = interact
					.processBatchResponse(saFiPurchase.getSession_id().toString(), batchResponse);

			// NDE
			Map<String, String> ndeAtttributes = new HashMap<>();

			try {
				if (fAttributes.get().isEmpty()) {
					logger.error("Received an empty map of attributes for session id: " + saFiPurchase.getSession_id());
				} else {
					ndeAtttributes.putAll(fAttributes.get());
				}
			} catch (InterruptedException e) {
				logger.error("InterruptedException", e);
			} catch (ExecutionException e) {
				logger.error("ExecutionException", e);
			}

			if (ndeAtttributes.isEmpty()) {
				logger.info("No message to send to NDE for MSISDN: " + saFiPurchase.getMsisdn());
				Integer subscriber_offer_sent = saFiPurchase.getSubscriber_offer_sent();

				if (subscriber_offer_sent == 0) {
					// Assign 2 as the value 1 means the record was processed successfully; so we
					// skip it
					subscriber_offer_sent = 2;
					saFiPurchase.setSubscriber_offer_sent(subscriber_offer_sent);
					logger.info("Update subscriber_offer_sent value to: " + subscriber_offer_sent + " for MSISDN: "
							+ saFiPurchase.getMsisdn());
				}

			} else {
				Future<String> msg = ndeMessage.getNdeNotification(saFiPurchase.getMsisdn(), ndeAtttributes);
				Future<Response> fResponse = restResource.sendRestRequest(msg);
				Future<Map<String, String>> fProcessResponses = ndeMessage.processResponse(fResponse);

				try {
					ndeAtttributes.putAll(fProcessResponses.get());
				} catch (InterruptedException e) {
					logger.error("InterruptedException: " + e);
				} catch (ExecutionException e) {
					logger.error("ExecutionException: " + e);
				}
			}

			if (ndeAtttributes.containsKey("code")) {
				logger.info("Proccessing record for Msisdn " + saFiPurchase.getMsisdn() + " failed");
			}

			if (ndeAtttributes.containsKey("messageId")) {
				saFiPurchase.setSubscriber_offer_sent(1);
			}

		});
	}

	@Override
	public void processSaFiPurchase(List<SaFiPurchase> saFiPurchases) {

		// MSISDN, RECHARGE_DATE, TRANSACTION_ID
		saFiPurchases.forEach(saFiPurchase -> {

			if (saFiPurchase.getSession_id() == null) {
				String digit = saFiPurchase.getMsisdn().substring(2, saFiPurchase.getMsisdn().length());
				saFiPurchase.setSession_id(digit);
				logger.warn("No session id received; substituting  MSISDN for session id : " + Integer.parseInt(digit));
			}

			List<CommandImpl> commandImpls = interact.buildQbziofCommandImpl(saFiPurchase);
			BatchResponse batchResponse = interact.sendBatchToInteract(saFiPurchase.getSession_id().toString(),
					commandImpls);
			Map<String, String> fAttributes = interact.processBatchResponse(saFiPurchase.getSession_id().toString(),
					batchResponse);

			// NDE
			Map<String, String> ndeAtttributes = new HashMap<>();

			if (fAttributes.isEmpty()) {
				logger.error("Received an empty map of attributes for session id: " + saFiPurchase.getSession_id());
			} else {
				ndeAtttributes.putAll(fAttributes);
			}

			if (ndeAtttributes.isEmpty()) {

				logger.info("No message to send to NDE for MSISDN: " + saFiPurchase.getMsisdn());
				Integer subscriber_offer_sent = saFiPurchase.getSubscriber_offer_sent();

				if (subscriber_offer_sent == null) { 
					subscriber_offer_sent = 0;
				}

				if (subscriber_offer_sent == 0) {
					// Assign 2 as the value 1 means the record was processed successfully; so we skip it
					subscriber_offer_sent = 2;
					saFiPurchase.setSubscriber_offer_sent(subscriber_offer_sent);
					logger.info("Update subscriber_offer_sent value to: " + subscriber_offer_sent + " for MSISDN: " + saFiPurchase.getMsisdn());
				
			}

			} else {
				Future<String> msg = ndeMessage.getNdeNotification(saFiPurchase.getMsisdn(), ndeAtttributes);
				Future<Response> fResponse = restResource.sendRestRequest(msg);
				Future<Map<String, String>> fProcessResponses = ndeMessage.processResponse(fResponse);

				try {
					ndeAtttributes.putAll(fProcessResponses.get());
				} catch (InterruptedException e) {
					logger.error("InterruptedException: " + e);
				} catch (ExecutionException e) {
					logger.error("ExecutionException: " + e);
				}

			}

			if (ndeAtttributes.containsKey("code")) {
				logger.info("Proccessing record for Msisdn " + saFiPurchase.getMsisdn() + " failed");
			}

			if (ndeAtttributes.containsKey("messageId")) {
				saFiPurchase.setSubscriber_offer_sent(1);
			}

		});
	}

}

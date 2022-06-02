package cvmi.fipm.mdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.resource.ResourceException;

import org.jboss.ejb3.annotation.ResourceAdapter;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.jca.RabbitMqConnection;
import com.rabbitmq.jca.RabbitMqConnectionFactory;
import com.rabbitmq.jca.inflow.RabbitMqMessageListener;

import cvmi.fipm.entity.SaFiPurchase;
import cvmi.fipm.session.Fipm;

@ResourceAdapter(value = "rabbitmq-jca.rar")
@MessageDriven(name = "SaFiPurchasesMDB", messageListenerInterface = RabbitMqMessageListener.class)
public class SaFiPurchasesMDB implements RabbitMqMessageListener {

	final static Logger logger = Logger.getLogger(SaFiPurchasesMDB.class);

	@Resource(name = "RabbitMqConnectionFactory", type = com.rabbitmq.jca.RabbitMqConnectionFactory.class, authenticationType = AuthenticationType.CONTAINER, mappedName = "java:/eis/RabbitMqConnectionFactory")
	private RabbitMqConnectionFactory rabbitMqConnectionFactory;

	@EJB
	private Fipm fipm;

	public SaFiPurchasesMDB() {
		// Auto-generated constructor stub
	}

	@Override
	public void onMessage(String msg) {

		if (msg != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

			SaFiPurchase[] saFiPurchases = new SaFiPurchase[0];
			try {
				saFiPurchases = objectMapper.readValue(msg.toLowerCase().getBytes(), SaFiPurchase[].class);
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException Message: " + e.getMessage());
				logger.error("JsonProcessingException Cause: " + e.getCause());
			} catch (IOException e) {
				logger.error("IOException Message: " + e.getMessage());
				logger.error("IOException Cause: " + e.getCause());
			}

			List<SaFiPurchase> fiPurchases = new ArrayList<SaFiPurchase>();
			if (saFiPurchases.length != 0) {
				fiPurchases.addAll(Arrays.asList(saFiPurchases));				
				fipm.processSaFiPurchase(fiPurchases);
			}
			objectMapper = new ObjectMapper();

			try {
				if (fiPurchases != null && !fiPurchases.isEmpty()) {
					String json = objectMapper.writeValueAsString(fiPurchases.toArray());
					if (json == null) {
						logger.error("JSON string object is null.");
					} else {
						sendResultToRabbitMq(json.getBytes("UTF-8"));
					}
				}
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException Message: " + e.getMessage());
				logger.error("JsonProcessingException Cause: " + e.getCause());
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException Message: " + e.getMessage());
				logger.error("UnsupportedEncodingException Cause: " + e.getCause());
			} catch (NullPointerException e) {			
				logger.error("NullPointerException caught and handled.");			
			}
			
		} else {
			logger.error("Received a null msg paramater.");
		}
	}

	public void sendResultToRabbitMq(byte[] message) {

		RabbitMqConnection connection = null;
		try {
			connection = rabbitMqConnectionFactory.getConnection();
			if (connection != null) {
				connection.onMessage(message);
			}

		} catch (ResourceException e) {
			logger.error("ResourceException: " + e);			
		} catch (NullPointerException e) {			
			logger.error("NullPointerException caught and handled.");			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
}

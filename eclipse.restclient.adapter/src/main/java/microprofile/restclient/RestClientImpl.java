package microprofile.restclient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;

public class RestClientImpl implements RestClient {

	private final static Logger logger = Logger.getLogger(RestClientImpl.class);

	private String spec;
	private String json;
	private String username;
	private String password;

	public RestClientImpl(String username, String password, String spec, String json) {
		this.username = username;
		this.password = password;
		this.spec = spec;
		this.json = json;
	}

	/**
	 * 
	 * @param spec Rest URL as a string
	 * @param json
	 */
	public Response sendRest(String spec, String json) {
		URL url = null;
		try {
			url = new URL(spec);
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException Message: " + e.getMessage());
			logger.error("MalformedURLException Cause: " + e.getCause());
		}
		
		RestClient restClient = RestClientBuilder.newBuilder().baseUrl(url).build(RestClient.class);
		return restClient.sendNdeSmsNotification(json);
	}

	@Override
	public Response sendNdeSmsNotification(String notification) {

		return Response.status(Status.NO_CONTENT).entity("Not Implemented ").build();
	}

	@Override
	public Response sendNdeSmsNotification() {

		URL url = null;
		try {
			url = new URL(spec);
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException Message: " + e.getMessage());
			logger.error("MalformedURLException Cause: " + e.getCause());
		}

		logger.debug("Remote Rest service username : " + username);
		logger.debug("Remote Rest service password : " + password);
		logger.debug("Rest URL to use: " + spec);
		logger.debug("Json string to transmit: " + json);
		
		NdeMsg ndeMsg = RestClientBuilder.newBuilder().baseUrl(url)
				.register(new BasicAuthentication(username, password)).build(NdeMsg.class);
		Response response = null;
		try {
			response = ndeMsg.sendNdeSmsNotification(json);
		} catch (WebApplicationException e) {
			final String jsonResponse = e.getResponse().readEntity(String.class);
			final int status = e.getResponse().getStatus();
			response = Response.status(status).entity(jsonResponse).build();
			logger.error("WebApplicationException readEntity: " + jsonResponse);
		}

		return response;
	}

}

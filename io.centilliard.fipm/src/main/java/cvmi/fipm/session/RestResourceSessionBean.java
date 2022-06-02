package cvmi.fipm.session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.resource.ResourceException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import microprofile.restclient.RestConnection;
import microprofile.restclient.RestConnectionFactory;

@Stateless
public class RestResourceSessionBean implements RestResource {

	private final static Logger logger = Logger.getLogger(RestResourceSessionBean.class);

	@Resource(name = "RestConnectionFactory",
              type = microprofile.restclient.RestConnectionFactory.class,
              authenticationType = AuthenticationType.CONTAINER,
              mappedName = "java:/eis/RestConnectionFactory")
	private RestConnectionFactory restConnectionFactory;

	public RestResourceSessionBean() {
		// Auto-generated constructor stub
	}

	@Timed(name = "sendRestRequest", description = "Metrics to monitor the response times of NDE in milliseconds.", unit = MetricUnits.MILLISECONDS, absolute = true)
	@Override
	@Asynchronous
	public Future<Response> sendRestRequest(Future<String> fjson) {
		
		String json ="";
		try {
			json = fjson.get();
		} catch (InterruptedException | ExecutionException e1) {
			logger.debug("InterruptedException | ExecutionException: " +e1);
		}
		
		logger.debug("Sending request to NDE: " + json);
		Response response = null;
		RestConnection connection = null;
		try {
			connection = restConnectionFactory.getConnection();
			response = connection.sendRestRequest(json);
		} catch (ResourceException e) {
			logger.error("ResourceException Message: " + e.getMessage());
			logger.error("ResourceException Cause: " + e.getCause());
		} finally {
			if (connection != null) {
				connection.close();
			}

		}

		return new AsyncResult<>(response);
	}

}

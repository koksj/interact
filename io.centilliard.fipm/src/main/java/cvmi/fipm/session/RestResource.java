package cvmi.fipm.session;

import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

public interface RestResource {

	/**
	 * Sends a rest request
	 * 
	 * @param json
	 * @return
	 */
	public Future<Response> sendRestRequest(Future<String> json);

}

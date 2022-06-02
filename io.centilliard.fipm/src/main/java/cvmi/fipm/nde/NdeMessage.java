package cvmi.fipm.nde;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

public interface NdeMessage {

	public  Future<String> getNdeNotification(String msisdn, Map<String, String>  values);
	
	
	public Future<Map<String, String>> processResponse (Future<Response> response);

}

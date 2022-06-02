package microprofile.restclient;

import javax.ws.rs.core.Response;

public interface RestClient {

	public Response sendNdeSmsNotification(String notification); // Send Data Bonanza Notification
	

	public Response sendNdeSmsNotification(); // Send Data Bonanza Notification
	
	
		
}

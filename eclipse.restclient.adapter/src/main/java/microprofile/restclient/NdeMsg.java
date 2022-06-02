package microprofile.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface NdeMsg {

	@POST
	@Path("/notification/interface")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendNdeSmsNotification(String notification); // Send Data Bonanza Notification
	
	@POST
	@Path("/notification/interface")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendNdeSmsNotification(); // Send Data Bonanza Notification
}

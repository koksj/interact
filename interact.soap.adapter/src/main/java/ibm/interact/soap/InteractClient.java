package ibm.interact.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.resource.ResourceException;

import org.jboss.logging.Logger;

import com.unicacorp.interact.api.soap.InteractService;
import com.unicacorp.interact.api.soap.InteractSoapServicePortType;
import com.unicacorp.interact.api.xsd.BatchResponse;
import com.unicacorp.interact.api.xsd.CommandImpl;



public class InteractClient implements Interact {
	
	private final static Logger logger = Logger.getLogger(InteractClient.class);
	
	private String sessionID;
	private String wsdlFile;
	private List<CommandImpl> commands;

	public InteractClient() {
		// Auto-generated constructor stub
	}
	
	public InteractClient(String sessionID, List<CommandImpl> commands, String wsdlFile) {
		this.sessionID = sessionID;
		this.commands = commands;
		this.wsdlFile  = wsdlFile;
	}

	@Override
	public BatchResponse executeBatch() throws ResourceException {
		
		logger.debug("WsdlFile location set to: " + wsdlFile);
		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL(wsdlFile);
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException message:  " + e.getMessage());
			logger.error("MalformedURLException cause:  " + e.getCause());
		}

		BatchResponse batchResponse = null;
		
		try {
			InteractService interactService = new InteractService(wsdlUrl);
			InteractSoapServicePortType interactSoapServicePort = interactService.getInteractSoapServiceHttpSoap11Endpoint();			
			batchResponse = interactSoapServicePort.executeBatch(sessionID, commands);
			
			if(batchResponse == null) {
				batchResponse = new BatchResponse(); 
			}
			
		} catch (Exception e) {
			throw new ResourceException(e);
		}
		
		return batchResponse;
	}

}
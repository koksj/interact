package ibm.interact.soap;

import javax.resource.ResourceException;

import com.unicacorp.interact.api.xsd.BatchResponse;

public interface Interact {
	
	public BatchResponse executeBatch() throws ResourceException;
	

}

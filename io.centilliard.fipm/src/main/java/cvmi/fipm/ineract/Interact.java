package cvmi.fipm.ineract;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.unicacorp.interact.api.xsd.BatchResponse;
import com.unicacorp.interact.api.xsd.CommandImpl;

import cvmi.fipm.entity.SaFiPurchase;

/**
 * 
 * @author skok
 *
 */
public interface Interact {

	/**
	 * Build the list of QBZIOF parameters from the SaFiPurchase entity. 
	 * @param saFiPurchase
	 * @return
	 */
	public Future<List<CommandImpl>> buildQbziofCommandImpls(SaFiPurchase saFiPurchase);
	
	public Future<List<CommandImpl>> buildQbziofCommandImpls(List<SaFiPurchase> saFiPurchases);
	public List<CommandImpl> buildQbziofCommandImpl(SaFiPurchase saFiPurchase);
	
	public Future<BatchResponse> sendBatchToInteract(final String sessionId,final Future<List<CommandImpl>> commands);
	
	public Future<Map<String, String>> processBatchResponse(String sessionId,Future<BatchResponse> batchResponse);
	
	public BatchResponse sendBatchToInteract(final String sessionId, final List<CommandImpl> commandsImpl);
	
	public Map<String, String> processBatchResponse(String sessionId, BatchResponse batchResponse);

	
}

package cvmi.fipm.session;

import java.util.List;

import cvmi.fipm.entity.SaFiPurchase;

public interface Fipm {
	
	public void processSaFiPurchases(List<SaFiPurchase> saFiPurchases);
	public void processSaFiPurchase(List<SaFiPurchase> saFiPurchases);
	
}

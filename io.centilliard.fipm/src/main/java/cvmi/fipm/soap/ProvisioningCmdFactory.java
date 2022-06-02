package cvmi.fipm.soap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import cvmi.fipm.constants.Constants;

/**
 * Factory pattern implementation to compile the request to be sent to IBM
 * Interact using the IBM Interact CommandImpl classes. The IBM Interact
 * CommandImpl classes is obtained from compiling the IBM WSDL file.
 * 
 * @author skok
 *
 */
public class ProvisioningCmdFactory {

	private final static Map<String, Supplier<ProvisioningCmd>> map = new HashMap<>();

	static {
		map.put(Constants.QBZIOF_KEY, QbziofProvisioningCmd::new);
	}

	public ProvisioningCmdFactory() {
		// Auto-generated constructor stub
	}

	public ProvisioningCmd getCommand(String factoryKey) {

		ProvisioningCmd command = null;

		Supplier<ProvisioningCmd> supplier = map.get(factoryKey);

		if (supplier != null) {
			command = supplier.get();
		}

		return command;
	}
}

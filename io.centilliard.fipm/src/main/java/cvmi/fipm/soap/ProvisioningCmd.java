package cvmi.fipm.soap;

import java.util.List;

import com.unicacorp.interact.api.xsd.CommandImpl;

import cvmi.fipm.commands.Qbziof;

public interface ProvisioningCmd {

	public List<CommandImpl> buildQbziofProvisioningCmd(Qbziof qbziof);
}

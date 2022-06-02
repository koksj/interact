package cvmi.fipm.soap;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.unicacorp.interact.api.xsd.CommandImpl;
import com.unicacorp.interact.api.xsd.NameValuePairImpl;

import cvmi.fipm.commands.Qbziof;
import cvmi.fipm.constants.Constants;

/**
 * 
 * @author skok
 *
 */
public class QbziofProvisioningCmd implements ProvisioningCmd {

	public QbziofProvisioningCmd() {
		// Auto-generated constructor stub
	}

	@Override
	public List<CommandImpl> buildQbziofProvisioningCmd(Qbziof qbziof) {

		List<CommandImpl> commands = new ArrayList<>();
		/**/
		CommandImpl commandImpl = new CommandImpl();
		commandImpl.getAudienceID().add(buildNameValuePair("MSISDN", qbziof.getMsisdn(), "String"));
		commandImpl.setAudienceLevel(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "audienceLevel"), String.class, "MSISDN"));
		commandImpl.setDebug(false);
		commandImpl.getEventParameters().add(buildNameValuePair("ALLOCATION", qbziof.getAllocation(), "numeric"));
		commandImpl.getEventParameters().add(buildNameValuePair("PURCHASED_BUNDLE_PRICE", qbziof.getPurchasedBundlePrice(), "numeric"));
		commandImpl.getEventParameters().add(buildNameValuePair("VALIDITY", dateToString(qbziof.getValidity()), "String"));

		commandImpl.setInteractiveChannel(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "interactiveChannel"),	String.class, qbziof.getInteractiveChannel()));
		commandImpl.setMethodIdentifier(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "methodIdentifier"), String.class, "startSession"));
		commandImpl.setRelyOnExistingSession(false);
		commands.add(commandImpl);
		
		/**/
		commandImpl = new CommandImpl();
		commandImpl.setInteractionPoint(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "interactionPoint"), String.class,	qbziof.getInteractionPoint()));
		commandImpl.setMethodIdentifier(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "methodIdentifier"), String.class, "getOffers"));
		commandImpl.setNumberRequested(1);
		
		commands.add(commandImpl);
		
		/**/
		commandImpl = new CommandImpl();
		commandImpl.setEvent(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "event"), String.class, "Contacted"));
		commandImpl.setMethodIdentifier(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "methodIdentifier"), String.class, "postEvent"));
		commands.add(commandImpl);

		return commands;
	}

	private NameValuePairImpl buildNameValuePair(String name, String value, String dataType) {

		NameValuePairImpl nameValuePairImpl = new NameValuePairImpl();
		nameValuePairImpl.setName(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "name"), String.class, name));
		nameValuePairImpl.setValueAsString(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "valueAsString"), String.class, value));
		nameValuePairImpl.setValueDataType(new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "valueDataType"), String.class, dataType));

		return nameValuePairImpl;
	}

	private NameValuePairImpl buildNameValuePair(String name, Double value, String dataType) {

		NameValuePairImpl nameValuePairImpl = new NameValuePairImpl();
		nameValuePairImpl.setName(
				new JAXBElement<String>(new QName(Constants.INTERACT_NAME_SPACE_URI, "name"), String.class, name));

		nameValuePairImpl.setValueAsNumeric(new JAXBElement<Double>(
				new QName(Constants.INTERACT_NAME_SPACE_URI, "valueAsNumeric"), Double.class, value));
		nameValuePairImpl.setValueDataType(new JAXBElement<String>(
				new QName(Constants.INTERACT_NAME_SPACE_URI, "valueDataType"), String.class, dataType));

		return nameValuePairImpl;
	}

	private String dateToString(Date date) {

		return DateTimeFormatter.ofPattern(Constants.YYYY_MM_DD_T_HH_MM_SS).withLocale(Locale.getDefault())
				.withZone(ZoneId.systemDefault()).format(date.toInstant());
	}

}

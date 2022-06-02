package cvmi.fipm.commands;

import java.util.Date;

/**
 * This is a POJO class containing the parameters to be sent to IBM interact
 * for the J4U Data Bonanza promotion
 * @author skok
 *
 */
public final class Qbziof {

	private String sessionId;

	private String msisdn;

	private Date validity;

	private Double purchasedBundlePrice;

	private Double allocation;

	private String interactiveChannel;

	private String interactionPoint;

	private Long numberRequested;

	public Qbziof() {
		// Auto-generated constructor stub
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public Double getPurchasedBundlePrice() {
		return purchasedBundlePrice;
	}

	public void setPurchasedBundlePrice(Double purchasedBundlePrice) {
		this.purchasedBundlePrice = purchasedBundlePrice;
	}

	public Double getAllocation() {
		return allocation;
	}

	public void setAllocation(Double allocation) {
		this.allocation = allocation;
	}

	public String getInteractiveChannel() {
		return interactiveChannel;
	}

	public void setInteractiveChannel(String interactiveChannel) {
		this.interactiveChannel = interactiveChannel;
	}

	public String getInteractionPoint() {
		return interactionPoint;
	}

	public void setInteractionPoint(String interactionPoint) {
		this.interactionPoint = interactionPoint;
	}

	public Long getNumberRequested() {
		return numberRequested;
	}

	public void setNumberRequested(Long numberRequested) {
		this.numberRequested = numberRequested;
	}

}

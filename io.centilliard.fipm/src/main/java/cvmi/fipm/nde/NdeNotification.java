package cvmi.fipm.nde;

public class NdeNotification {

	private String type;
	
	private String subtype;
	
	private String source;
	
	private String destination;
	
	private String  paymentMethod;
	
	private String templateId;
	
	private String transactionId;
	
   // private  NdeAttribute[] conditions;
	
	private NdeAttribute[] attributes;
	
	
	public NdeNotification() {
		// Auto-generated constructor stub
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getSubType() {
		return subtype;
	}


	public void setSubType(String subType) {
		this.subtype = subType;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public String getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getTemplateId() {
		return templateId;
	}


	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}


	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

/**
	public  NdeAttribute[] getConditions() {
		return conditions;
	}


	public void setConditions( NdeAttribute[] conditions) {
		this.conditions = conditions;
	}
**/

	public NdeAttribute[] getAttributes() {
		return attributes;
	}


	public void setAttributes(NdeAttribute[] attributes) {
		this.attributes = attributes;
	}
	
}

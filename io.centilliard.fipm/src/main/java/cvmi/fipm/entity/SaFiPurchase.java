package cvmi.fipm.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SaFiPurchase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String transfering_msisdn;
	private String psp_id;
	private String provider_id;
	private Integer record_version;
	private Integer voucher_batch_no;
	private String voucher_pin;
	private Date recharge_date;
	private String session_id;
	private String msisdn;
	private Integer voucher_type;
	private Integer voucher_no;
	private String trans_id;
	private Date exp_date;
	private Integer air_time_add;
	private Integer exp_period;
	private Integer value_add;
	private Integer initial_subseq;
	private String recharge_type;
	private Double amount;
	private String transaction_id;
	private Date staging_insert_date;
	private Date replication_insert_date;
	private Date extraction_audit_date;
	private Integer report_flag;
	private Integer rms_icap_id;
	private Integer sms_units;
	private Double data_size;
	private Integer subscriber_offer_sent;

	public SaFiPurchase() {
		// Auto-generated constructor stub
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getTransfering_msisdn() {
		return transfering_msisdn;
	}

	public void setTransfering_msisdn(String transfering_msisdn) {
		this.transfering_msisdn = transfering_msisdn;
	}

	public String getPsp_id() {
		return psp_id;
	}

	public void setPsp_id(String psp_id) {
		this.psp_id = psp_id;
	}

	public String getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(String provider_id) {
		this.provider_id = provider_id;
	}

	
	public Integer getRecord_version() {
		return record_version;
	}

	public void setRecord_version(Integer record_version) {
		this.record_version = record_version;
	}

	public Integer getVoucher_batch_no() {
		return voucher_batch_no;
	}

	public void setVoucher_batch_no(Integer voucher_batch_no) {
		this.voucher_batch_no = voucher_batch_no;
	}

	public String getVoucher_pin() {
		return voucher_pin;
	}

	public void setVoucher_pin(String voucher_pin) {
		this.voucher_pin = voucher_pin;
	}

	public Date getRecharge_date() {
		return recharge_date;
	}

	public void setRecharge_date(Date recharge_date) {
		this.recharge_date = recharge_date;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Integer getVoucher_type() {
		return voucher_type;
	}

	public void setVoucher_type(Integer voucher_type) {
		this.voucher_type = voucher_type;
	}

	public Integer getVoucher_no() {
		return voucher_no;
	}

	public void setVoucher_no(Integer voucher_no) {
		this.voucher_no = voucher_no;
	}

	public String getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}

	public Date getExp_date() {
		return exp_date;
	}

	public void setExp_date(Date exp_date) {
		this.exp_date = exp_date;
	}

	public Integer getAir_time_add() {
		return air_time_add;
	}

	public void setAir_time_add(Integer air_time_add) {
		this.air_time_add = air_time_add;
	}

	public Integer getExp_period() {
		return exp_period;
	}

	public void setExp_period(Integer exp_period) {
		this.exp_period = exp_period;
	}

	public Integer getValue_add() {
		return value_add;
	}

	public void setValue_add(Integer value_add) {
		this.value_add = value_add;
	}

	public Integer getInitial_subseq() {
		return initial_subseq;
	}

	public void setInitial_subseq(Integer initial_subseq) {
		this.initial_subseq = initial_subseq;
	}

	public String getRecharge_type() {
		return recharge_type;
	}

	public void setRecharge_type(String recharge_type) {
		this.recharge_type = recharge_type;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getStaging_insert_date() {
		return staging_insert_date;
	}

	public void setStaging_insert_date(Date staging_insert_date) {
		this.staging_insert_date = staging_insert_date;
	}

	public Date getReplication_insert_date() {
		return replication_insert_date;
	}

	public void setReplication_insert_date(Date replication_insert_date) {
		this.replication_insert_date = replication_insert_date;
	}

	public Date getExtraction_audit_date() {
		return extraction_audit_date;
	}

	public void setExtraction_audit_date(Date extraction_audit_date) {
		this.extraction_audit_date = extraction_audit_date;
	}

	public Integer getReport_flag() {
		return report_flag;
	}

	public void setReport_flag(Integer report_flag) {
		this.report_flag = report_flag;
	}

	public Integer getRms_icap_id() {
		return rms_icap_id;
	}

	public void setRms_icap_id(Integer rms_icap_id) {
		this.rms_icap_id = rms_icap_id;
	}

	public Integer getSms_units() {
		return sms_units;
	}

	public void setSms_units(Integer sms_units) {
		this.sms_units = sms_units;
	}

	public Double getData_size() {
		return data_size;
	}

	public void setData_size(Double data_size) {
		this.data_size = data_size;
	}

	public Integer getSubscriber_offer_sent() {
		return subscriber_offer_sent;
	}

	public void setSubscriber_offer_sent(Integer subscriber_offer_sent) {
		this.subscriber_offer_sent = subscriber_offer_sent;
	}

}

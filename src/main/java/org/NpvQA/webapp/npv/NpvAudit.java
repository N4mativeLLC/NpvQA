package org.NpvQA.webapp.npv;

public class NpvAudit {
	private int client_id;
	private int project_id;
	private int generate_id;
	private String customer_id;
	private String customer_name;
	private boolean result;
	private String failure_reason;
	private String audit_by;
	
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getFailure_reason() {
		return failure_reason;
	}
	public void setFailure_reason(String failure_reason) {
		this.failure_reason = failure_reason;
	}
	public String getAudit_by() {
		return audit_by;
	}
	public void setAudit_by(String audit_by) {
		this.audit_by = audit_by;
	}
	public int getClient_id() {
		return client_id;
	}
	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
	public int getGenerate_id() {
		return generate_id;
	}
	public void setGenerate_id(int generate_id) {
		this.generate_id = generate_id;
	}
	
	
}

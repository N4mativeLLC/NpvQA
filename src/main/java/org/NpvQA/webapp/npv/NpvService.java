package org.NpvQA.webapp.npv;

import org.json.JSONException;

public interface NpvService {
     
    String getFnbData(String filter, Integer clientId, Integer projectId, Integer generateId);
    boolean insertAudit(String auditJson);
    String checkCustomer(String customer_id);
	String getProjectData(int clientID);
	String getGenerateData(Integer clientID, Integer projID);
	String getQaData(String filter, Integer clientId, Integer projectId, Integer generateId);
	  
}
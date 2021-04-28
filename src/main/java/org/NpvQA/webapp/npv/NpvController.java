package org.NpvQA.webapp.npv;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import org.apache.commons.codec.binary.Base64;

//@CrossOrigin //(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping(value="/npv")
public class NpvController {
	
	@Autowired
    NpvService npvService;
    
	@RequestMapping(value = "/data/{filter}/{clientId}/{projectId}/{generateId}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getUsers(
    		@PathVariable("filter") String filter,
    		@PathVariable("clientId") Integer clientId,
    		@PathVariable("projectId") Integer projectId,
    		@PathVariable("generateId") Integer generateId) {

        String users = npvService.getFnbData(filter,clientId,projectId,generateId);
        if (users == null) {
            System.out.println("getFnbData not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(users, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/qaData/{filter}/{clientId}/{projectId}/{generateId}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getQaData(
    		@PathVariable("filter") String filter,
    		@PathVariable("clientId") Integer clientId,
    		@PathVariable("projectId") Integer projectId,
    		@PathVariable("generateId") Integer generateId) {

        String users = npvService.getQaData(filter,clientId,projectId,generateId);
        if (users == null) {
            System.out.println("getQAData not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(users, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/project/{clientID}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getProject(
    		@PathVariable("clientID") Integer clientID) {

        String project = npvService.getProjectData(clientID);
        if (project == null) {
            System.out.println("getProject not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(project, HttpStatus.OK);
    }
	
	@RequestMapping(value = "/generate/{clientID}/{projID}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getProject(
    		@PathVariable("clientID") Integer clientID,
    		@PathVariable("projID") Integer projID) {

        String generate = npvService.getGenerateData(clientID, projID);
        if (generate == null) {
            System.out.println("getProject not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(generate, HttpStatus.OK);
    }

    @RequestMapping(value = "/audit/save/{auditJson:.+}", method = RequestMethod.PUT)//, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> saveUser(
    		@PathVariable("auditJson") String auditJson) {
    	
    	auditJson = auditJson.replace("[", "").replace("]", "");

    	boolean success = npvService.insertAudit(auditJson);
        if (!success) {
            System.out.println("operation not successful");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("Insert Successful", HttpStatus.OK);
    	
    }
    
    @RequestMapping(value = "/data/isCustomerQA/{customer_id:.+}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkCustomer(
    		@PathVariable("customer_id") String customer_id) {

    	
        String isDone= npvService.checkCustomer(customer_id);
        
        return new ResponseEntity<String>(isDone, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/n" , method = RequestMethod.GET  )
	public String sample()
	{
    	StringBuilder sb = new StringBuilder();
    	sb.append("NPV audit home page");
    	sb.append("</br>");
    	sb.append("</br>");
    	sb.append("/npv/data/{filter}");
    	sb.append("</br>");
    	sb.append("/npv/audit/save/{auditJson:.+}");
    	sb.append("</br>");

	    return sb.toString();

	}
}

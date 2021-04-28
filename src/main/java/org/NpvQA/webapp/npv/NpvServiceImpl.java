package org.NpvQA.webapp.npv;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.NpvQA.webapp.database.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("npvService")
//@Transactional
public class NpvServiceImpl implements NpvService {
	
	@Autowired
	private Environment env;
	
	/*
	public String[] authUser(String username, String password) {

		CallableStatement stmt = null;
		ResultSet rs = null;
        JSONArray jArray = new JSONArray();
        String[] values = new String[3];
        
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        
        //try (Connection conn = new ConnectionController().getConnection()) {
        try (Connection conn = ConnectionPool.getConnection(host, db)) {
        	String strSQL = "{call sp_LoginUser_v2(?, ?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            boolean hadResults = stmt.execute();
            int userId = 0;
            while (hadResults) {

            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject children = new JSONObject() ;
                	userId = rs.getInt("user_id");//.children;[]
                    children.put("user_id", userId);
                    children.put("user_name", rs.getString("user_name"));
                    
                    jArray.put(children);
                }
 
                hadResults = stmt.getMoreResults();
            }
            String userdetails = jArray.toString().trim().equals("[]") ? "" : jArray.toString();
            
            if(userdetails.length() > 0) {
            	//token = AuthHelper.createJsonWebToken(username, 60L);
            	String token = AuthHelper.createJsonWebToken(username, 1440);
            	String uuid = UUID.randomUUID().toString();
            	updateToken(username, token, uuid);
            	
            	values[0] = token;
            	values[1] = uuid;
            	
            	//String[] attributes = getUserProfile(userId);
            	values[2] = getUserProfile(userId);
            }
            //return token;
        }
        catch(SQLException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return values;
	}
	*/
	@Override
	public String getFnbData(String filter, Integer client_id, Integer project_id, Integer generate_id) {
		// TODO Auto-generated method stub
		CallableStatement stmt = null;
		ResultSet rs = null;
        JSONArray jArray = new JSONArray();
        
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        
        try (Connection conn = ConnectionPool.getConnection(host, db)) {

        	String strSQL = "{call sp_GetFNBData_v1(?,?,?,?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setString(1, filter);
            stmt.setInt(2, client_id);
            stmt.setInt(3, project_id);
            stmt.setInt(4, generate_id);
            
            boolean hadResults = stmt.execute();
            String header = "Customer_id,Custome_Name,current_date,amount_money_in,amount_money_out,dates_from_until,amount_possible_shortfall,possible_cash,Upcoming_payments,AVAIL_BALANCE,account_type,amount_status,video";
            while (hadResults) {

            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject children = new JSONObject() ;
                    //children.put("customer_id", rs.getString("customer_id"));
                    //children.put("customer_name", rs.getString("customer_name"));
                    //children.put("QA_Purl", rs.getString("QA_Purl"));
                    
                    //String data = rs.getString("purlstring");
                	String headers[] = header.split(",");
                    String data[] = rs.getString("QA_Purl").split("\\|");//.replace("\\|", ","); //.split("\\|");
                    //data = data.replaceAll("\\|", ",");
                    for(int i=0;i<headers.length;i++) {
                    	children.put(headers[i], data[i]);
                    }
                    
                    jArray.put(children);
                }
 
                hadResults = stmt.getMoreResults();
            }
        }
        catch(SQLException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        JSONObject jFinal = new JSONObject();
        try {
			jFinal.put("data", jArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jFinal.toString();
	}

	@Override
	public boolean insertAudit(String auditJson) {
		// TODO Auto-generated method stub
		NpvAudit npvAudit = null;
		CallableStatement stmt = null;
		String strSQL = "{call sp_InsertQAAudit(?,?,?,?,?,?,?,?)}";
		
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
			  	ObjectMapper objectMapper = new ObjectMapper();

				// convert json string to object
			  	npvAudit = objectMapper.readValue(
			  			auditJson.getBytes(), NpvAudit.class);
			    
				stmt = conn.prepareCall(strSQL);
				//String custmer_unique=npvAudit.getCustomer_id().replaceAll("-","/");
				// set all the preparedstatement parameters
				/*String customer_uniqueidentity=npvAudit.getCustomer_id();
				int emailIndex= customer_uniqueidentity.lastIndexOf("_");
				String customer_date= customer_uniqueidentity.substring(emailIndex).replaceAll("-","/");
				String custmer_unique=customer_uniqueidentity.substring(0,emailIndex)+customer_date;*/
				
				stmt.setInt(1, npvAudit.getClient_id());
				stmt.setInt(2, npvAudit.getProject_id());
				stmt.setInt(3, npvAudit.getGenerate_id());
				//stmt.setString(4, custmer_unique);
				stmt.setString(4, npvAudit.getCustomer_id());
				stmt.setString(5, npvAudit.getCustomer_name());
				stmt.setBoolean(6, npvAudit.getResult());
				stmt.setString(7, npvAudit.getFailure_reason());
				stmt.setString(8, npvAudit.getAudit_by());
			    
			    // execute the preparedstatement insert
				stmt.executeQuery();
			    //st.close();
			    return true;
		  } 
		  catch (Exception e)
		  {
			  //throw new RuntimeException(e);
			  e.printStackTrace();
		  } finally {
	            try {
	            	if (stmt != null) {
	            		stmt.close();
	            	}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}

	@Override
	public String checkCustomer(String customer_id) {
		// TODO Auto-generated method stub
		
		CallableStatement stmt = null;
		ResultSet rs = null;
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        int no_rec = 0;
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
			
			//String custmer_unique=customer_id.replaceAll("-","/");
			/*String customer_uniqueidentity=customer_id;
			int emailIndex= customer_uniqueidentity.lastIndexOf("_");
			String customer_date= customer_uniqueidentity.substring(emailIndex).replaceAll("-","/");
			String custmer_unique=customer_uniqueidentity.substring(0,emailIndex)+customer_date;*/

        	//String strSQL = "{call sp_GetFNBData(?)}";
        	String strSQL = "select count(*) AS Records from qaaudit where customer_id=?";

            stmt = conn.prepareCall(strSQL);
            //stmt.setString(1, custmer_unique);
            stmt.setString(1, customer_id);
            boolean hadResults = stmt.execute();
            
            while (hadResults) {

            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	no_rec= rs.getInt("Records");
                	//
                }
                hadResults = stmt.getMoreResults();
           }
        } catch(SQLException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        }
        finally{
        	try {
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
       return no_rec>0?"true":"false";
	}

	@Override
	public String getProjectData(int clientID) {

		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
        JSONArray jArray = new JSONArray();
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");

		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {


        	String strSQL = "{call sp_GetProjectByClientID(?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setInt(1, clientID);
            
            boolean hadResults = stmt.execute();
            
            while (hadResults) {
                //ResultSet resultSet
            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject jsonobj = new JSONObject() ;

                	jsonobj.put("project_id", rs.getInt("project_id"));
                	jsonobj.put("project_details", rs.getString("project_details"));
                	
                    jArray.put(jsonobj);
                }
 
                hadResults = stmt.getMoreResults();
            }
            return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
        }
        catch(SQLException e) {
		System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (ps != null) {
            		ps.close();
            	}
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}
	
	@Override
	public String getGenerateData(Integer clientID, Integer projID) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
        JSONArray jArray = new JSONArray();
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");

		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {


        	String strSQL = "{call sp_GetGenerateIdByProjID(?,?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setInt(1, clientID);
            stmt.setInt(2, projID);
            
            boolean hadResults = stmt.execute();
            
            while (hadResults) {
                //ResultSet resultSet
            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject jsonobj = new JSONObject() ;

                	jsonobj.put("generate_id", rs.getInt("generate_id"));
                	jsonobj.put("start_date", rs.getString("start_date"));
                	jsonobj.put("completion_date", rs.getString("completion_date"));
                	jsonobj.put("video_count", rs.getInt("video_count"));
                	
                    jArray.put(jsonobj);
                }
 
                hadResults = stmt.getMoreResults();
            }
            return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
        }
        catch(SQLException e) {
		System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (ps != null) {
            		ps.close();
            	}
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getQaData(String filter, Integer clientId, Integer projectId, Integer generateId) {
		CallableStatement stmt = null;
		ResultSet rs = null;
        JSONArray jArray = new JSONArray();
        
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {


        	String strSQL = "{call sp_GetClientData_QA(?,?,?,?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setString(1, filter);
            stmt.setInt(2, clientId);
            stmt.setInt(3, projectId);
            stmt.setInt(4, generateId);
            
            boolean hadResults = stmt.execute();
            
            String purlStringHeader = getProjectHeader(clientId,projectId);
            //String header="clm_lead_id|first_name|account_age|initial|last_name|threeMonthAvg|savings|AccountType|video";

            //String header="customer_id|customer_First_name|Profit_Share_Account_Balance|from_date|Projected_Profite_Share_Increase|to_date|PPS_Credit_Card|Health_Professionals_Indemnity|video";
            
            //String header="MemberNumber|YearsMember|FirstName|ProfShareAlloc|OperativeProfit|ProfShareBal|InvReturns|ClosingBal|AgeAsAtEndOfBonusYear|RecCount|NumberOfProducts|Advisor|Advisor_Email|video|thumbnail_1";
            //String header= "Client_Email|CONTACT_FIRST_CAP|PM_FIRST_CAP|PM_First|client_first|Inv_Date|Inv_Year|Inv_Month|Client_Account|Inv_Amt|Service_Full_CAP|Service_Full|Service_First|video|thumbnail_1";
            //String header= "acc_type|first_name|customer_number|Account_Customer_Type|video|thumbnail|thumbnail_1";
            //String purlStringHeader = "MemberNumber,FirstName,Age_group,ProfitShare_Amount_Display,Potential_ProfitShare_Amount_Display,"
					  //+ "2019_Closing_Bal_Display,Operating_profit_Display,Investment_returns_Display,2020_prof_share_balance_Display,"
					  //+ "PPS_Life_Risk,PPS_Investments,PPS_short_term_Insurance,PPS_Prof_Med,"
					  //+ "Medical_Health_Professional,Profit_member,financial_advisor,advisor_email,video,thumbnail_1";
            //String[] purlHeaders = purlStringHeader.split(",");
            String headers[] = purlStringHeader.split("\\|");
      	
            	while (hadResults) {

            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject children = new JSONObject() ;
                	
                	//String headers[] = header.split("\\|");
                    String data[] = rs.getString("QA_Purl").split("\\|");//.replace("\\|", ","); //.split("\\|");
                    //data = data.replaceAll("\\|", ",");
                    for(int i=0;i<headers.length;i++) {
                    	//System.out.println(purlHeaders[i] + " - " + data[i]);
                    	children.put(headers[i], data[i]);
                    }
                    
                    jArray.put(children);
                }
 
                hadResults = stmt.getMoreResults();
            }
        }
        catch(SQLException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        JSONObject jFinal = new JSONObject();
        try {
			jFinal.put("data", jArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jFinal.toString();
	}
	
	/*@Override
	public String getQaData_changed(String filter, Integer clientId, Integer projectId, Integer generateId) {
		CallableStatement stmt = null;
		ResultSet rs = null;
        JSONArray jArray = new JSONArray();
        
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {


        	String strSQL = "{call sp_GetClientData_QA(?,?,?,?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setString(1, filter);
            stmt.setInt(2, clientId);
            stmt.setInt(3, projectId);
            stmt.setInt(4, generateId);
            
            boolean hadResults = stmt.execute();
            
            //String header = getProjectHeader(clientId,projectId);
            //String header="clm_lead_id|first_name|account_age|initial|last_name|threeMonthAvg|savings|AccountType|video";

            //String header="customer_id|customer_First_name|Profit_Share_Account_Balance|from_date|Projected_Profite_Share_Increase|to_date|PPS_Credit_Card|Health_Professionals_Indemnity|video";
            
            //String header="MemberNumber|YearsMember|FirstName|ProfShareAlloc|OperativeProfit|ProfShareBal|InvReturns|ClosingBal|AgeAsAtEndOfBonusYear|RecCount|NumberOfProducts|Advisor|Advisor_Email|video|thumbnail_1";
            String header= "Client_Email|CONTACT_FIRST_CAP|PM_FIRST_CAP|PM_First|client_first|Inv_Date|Inv_Year|Inv_Month|Client_Account|Inv_Amt|Service_Full_CAP|Service_Full|Service_First|video|thumbnail_1";

            while (hadResults) {

            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	JSONObject children = new JSONObject() ;
                	
                	String headers[] = header.split("\\|");
                    String data[] = rs.getString("QA_Purl").split("\\|");//.replace("\\|", ","); //.split("\\|");
                    //data = data.replaceAll("\\|", ",");
                    for(int i=0;i<headers.length;i++) {
                    	children.put(headers[i], data[i]);
                    }
                    
                    jArray.put(children);
                }
 
                hadResults = stmt.getMoreResults();
            }
            return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
        }
        catch(SQLException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        JSONObject jFinal = new JSONObject();
        try {
			jFinal.put("data", jArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}*/
	
	private String getProjectHeader(Integer clientID, Integer projID) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
        JSONArray jArray = new JSONArray();
        String host = env.getProperty("mysql.host");
        String db = env.getProperty("mysql.db");
        String header="";

		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {


        	String strSQL = "{call sp_ProjectHeaderByProjID(?,?)}";

            stmt = conn.prepareCall(strSQL);
            stmt.setInt(1, clientID);
            stmt.setInt(2, projID);
            
            boolean hadResults = stmt.execute();
            
            while (hadResults) {
                //ResultSet resultSet
            	rs = stmt.getResultSet();
 
                // process result set
                while (rs.next()) {
                	/*JSONObject jsonobj = new JSONObject() ;
                	
                	jsonobj.put("client_id", rs.getInt("client_id"));
                	jsonobj.put("project_id", rs.getString("project_id"));
                	jsonobj.put("project_details", rs.getString("project_details"));
                	jsonobj.put("project_inputHeader", rs.getString("project_inputHeader"));
                	jsonobj.put("project_outputHeader", rs.getString("project_outputHeader"));
                	
                    jArray.put(jsonobj);*/
                    header= rs.getString("project_outputHeader");
                }
 
                hadResults = stmt.getMoreResults();
            }
            return header;//jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
        }
        catch(SQLException e) {
		System.err.println(e.getMessage());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{
        	try {
            	if (ps != null) {
            		ps.close();
            	}
            	if (rs != null) {
            		rs.close();
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return header;
        //jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	
}

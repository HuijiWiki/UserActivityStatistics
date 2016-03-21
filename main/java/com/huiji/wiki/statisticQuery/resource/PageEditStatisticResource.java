package com.huiji.wiki.statisticQuery.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bson.Document;
import org.json.JSONObject;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.huiji.wiki.statisticQuery.mongodb.FakedPageEditRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.mongodb.PageEditRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.util.Helper;

@Path("/edit")
public class PageEditStatisticResource {

	private static String DB_Name = "userData";
	private static String Edit_Collection = "editRecords";
	private static String FakedEdit_Collection = "fakedEditRecords";
	
//	private static String Edit_Collection = "test-edit";
//	private static String FakedEdit_Collection = "test-fake";
	
	@POST
	@Path("/getAllPageEditCountFromUserId")
	@Produces("application/json")
	@Consumes("application/json")
	public String getAllPageEditCountFromUserId(String input){
		int count = 0;
		StringBuffer errBuff = new StringBuffer();
		
		try{
			JSONObject inputInfo = new JSONObject(input);
			int userId = inputInfo.optInt("userId", -1);
			String fromDate = inputInfo.optString("fromDate");
			String toDate = inputInfo.optString("toDate");
			
			PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(ProjectR.client,DB_Name, Edit_Collection);
			FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(ProjectR.client, DB_Name,FakedEdit_Collection);
		
			count += editH.getPageEditCountOnWikiSiteFromUserId(userId, null , fromDate, toDate);
			count += fakedEditH.getFakedPageEditCountFromUserId(userId, fromDate, toDate);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse(count);
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}
	}

	@POST
	@Path("/getAllPageEditRecordsFromUserIdGroupByDay")
	@Produces("application/json")
	@Consumes("application/json")
	public String getAllPageEditRecordsFromUserIdGroupByDay(String input){

		
		JSONObject result = new JSONObject();
		HashMap<String, Integer> out = new HashMap<String, Integer>();
		StringBuffer errBuff = new StringBuffer();

		try{
			JSONObject inputInfo = new JSONObject(input);
			int userId = inputInfo.optInt("userId", -1);
			String fromDate = inputInfo.optString("fromDate");
			String toDate = inputInfo.optString("toDate");
			
			PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(ProjectR.client, DB_Name, Edit_Collection);
			FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(ProjectR.client, DB_Name, FakedEdit_Collection);
			
			for(Document doc : editH.getPageEditRecordsOnWikiSiteFromUserIdGroupByDay(userId, null, fromDate, toDate)){
				out.put(doc.getString("_id"), doc.getInteger("value"));
			}
			for(Document doc : fakedEditH.getFakedPageEditRecordsFromUserIdGroupByDay(userId, fromDate, toDate)){
				if(out.containsKey(doc.getString("_id"))){
					out.put(doc.getString("_id"), doc.getInteger("value") + out.get(doc.getString("_id")));
				}else{
					out.put(doc.getString("_id"), doc.getInteger("value"));
				}
			}
		}catch (Exception e) { 
				errBuff.append(e.toString());
		}
		
		
		 for (Map.Entry<String, Integer> entry : out.entrySet()) {
			 if(entry.getKey() != null){
				 result.put(entry.getKey(), entry.getValue());
			 }
			 	
		 }
		 
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse(result);
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}
	}
	
	@POST
	@Path("/getPageEditCountOnWikiSiteFromUserId")
	@Produces("application/json")
	@Consumes("application/json")
	public String getPageEditCountOnWikiSiteFromUserId(String input){
		StringBuffer errBuff = new StringBuffer();
		int count = 0;
		
		try{
			JSONObject inputInfo = new JSONObject(input);
			int userId = inputInfo.optInt("userId", -1);
			String fromDate = inputInfo.optString("fromDate");
			String toDate = inputInfo.optString("toDate");
			String sitePrefix = inputInfo.optString("sitePrefix");
			
			PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(ProjectR.client, DB_Name , Edit_Collection);
			
			count += editH.getPageEditCountOnWikiSiteFromUserId(userId, sitePrefix, fromDate, toDate);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse(count);
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}
	}
	
	@POST
	@Path("/getPageEditorRecordsGroupByWikiSite")
	@Produces("application/json")
	@Consumes("application/json")
	public String getPageEditorRecordsGroupByWikiSite(String input){
		JSONObject result = new JSONObject();
	
		StringBuffer errBuff = new StringBuffer();
		try{
			JSONObject inputInfo = new JSONObject(input);
			String fromDate = inputInfo.optString("fromDate");
			String toDate = inputInfo.optString("toDate");
			
			PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(ProjectR.client, DB_Name, Edit_Collection);
			
			
			
			for (Document doc:editH.getPageEditorRecordsGroupByWikiSite(fromDate, toDate)){
				result.put(doc.getString("_id"), doc.getInteger("value"));	
			};
			
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse(result);
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}	
	}
	
	
	@POST
	@Path("/insertOnePageEditRecord")
	@Produces("application/json")
	@Consumes("application/json")
	public String insertOnePageEditRecord(String input){
		StringBuffer errBuff = new StringBuffer();
		try{
			JSONObject inputInfo = new JSONObject(input);
			String user_name = Helper.clearStringData(inputInfo.optString("user_name"));
			int user_id = inputInfo.optInt("user_id", 0);
			String site_name = Helper.clearStringData(inputInfo.optString("site_name"));
			String site_prefix =  Helper.clearStringData(inputInfo.optString("site_prefix"));
			String page_title =  Helper.clearStringData(inputInfo.optString("page_title"));
			int page_id = inputInfo.optInt("page_id", 0);	
			int page_ns = inputInfo.optInt("page_ns",0);
			String client_ip = Helper.clearStringData(inputInfo.optString("client_ip"));
			String client_userAgent =  Helper.clearStringData(inputInfo.optString("client_userAgent"));
			
			PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(ProjectR.client, DB_Name, Edit_Collection);
			editH.insertOnePageEditRecord(user_name, user_id, site_name, site_prefix, page_title, page_id, page_ns, client_ip, client_userAgent);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse("edit");
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}	
	}

}

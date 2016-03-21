package com.huiji.wiki.statisticQuery.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.json.JSONObject;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.huiji.wiki.statisticQuery.mongodb.PageViewRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.util.Helper;

@Path("/view")
public class PageViewStatisticResource {

	private static String DB_Name = "userData";

	private static String View_Collection = "viewRecords";
	//private static String View_Collection = "test-view";

	
	
	@POST
	@Path("/getPageViewCountOnWikiSiteFromUserId")
	@Produces("application/json")
	@Consumes("application/json")
	public String getPageViewCountOnWikiSiteFromUserId(String input){
		StringBuffer errBuff = new StringBuffer();
		int count = 0;
		
		try{
			JSONObject inputInfo = new JSONObject(input);
			int userId = inputInfo.optInt("userId", -1);
			String fromDate = inputInfo.optString("fromDate");
			String toDate = inputInfo.optString("toDate");
			String sitePrefix = inputInfo.optString("sitePrefix");
			PageViewRecordsMongoDBHandler viewH = new PageViewRecordsMongoDBHandler(ProjectR.client, DB_Name, View_Collection);
			
			count += viewH.getPageViewCountOnWikiSiteFromUserId(userId, sitePrefix, fromDate, toDate);
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
	@Path("/insertOnePageViewRecord")
	@Produces("application/json")
	@Consumes("application/json")
	public String insertOnePageViewRecord(String input, @Context HttpServletRequest request){
		StringBuffer errBuff = new StringBuffer();
		try{
			JSONObject inputInfo = new JSONObject(input);
			String user_name = Helper.clearStringData(inputInfo.optString("user_name")) ;
			int user_id = inputInfo.optInt("user_id", 0);
			String site_name = Helper.clearStringData(inputInfo.optString("site_name"));
			String site_prefix =  Helper.clearStringData(inputInfo.optString("site_prefix"));
			String article_title =  Helper.clearStringData(inputInfo.optString("article_title"));
			int article_id = inputInfo.optInt("article_id", 0);	
			String client_ip = Helper.clearStringData(Helper.getIpAddress(request));
			String client_userAgent =  Helper.clearStringData(inputInfo.optString("client_userAgent"));
			String source = Helper.clearStringData(inputInfo.optString("source"));
			
			PageViewRecordsMongoDBHandler viewH = new PageViewRecordsMongoDBHandler(ProjectR.client, DB_Name, View_Collection);
			viewH.insertOnePageViewRecord(user_name, user_id, site_name, site_prefix, article_title, article_id, client_ip, client_userAgent, source);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse("Go Go Go!");
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}	
	}
	
	@POST
	@Path("/insertOnePageViewRecord")
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String insertOnePageViewRecord( @DefaultValue("") @FormParam("user_name") String user_name,
										   @DefaultValue("0") @FormParam("user_id") int user_id,
										   @DefaultValue("") @FormParam("site_name") String site_name,
										   @DefaultValue("") @FormParam("site_prefix") String site_prefix,
										   @DefaultValue("") @FormParam("article_title") String article_title,
										   @DefaultValue("0") @FormParam("article_id") int article_id,
										   @DefaultValue("") @FormParam("client_userAgent") String client_userAgent,
										   @DefaultValue("") @FormParam("source") String source,
			   							  @Context HttpServletRequest request){
		
		StringBuffer errBuff = new StringBuffer();
		user_name = Helper.clearStringData(user_name) ;
		site_name = Helper.clearStringData(site_name);
		site_prefix =  Helper.clearStringData(site_prefix);
		article_title =  Helper.clearStringData(article_title);
		String client_ip = Helper.clearStringData(Helper.getIpAddress(request));
		client_userAgent =  Helper.clearStringData(client_userAgent);
		source = Helper.clearStringData(source);
		
		try{
			PageViewRecordsMongoDBHandler viewH = new PageViewRecordsMongoDBHandler(ProjectR.client, DB_Name, View_Collection);
			viewH.insertOnePageViewRecord(user_name, user_id, site_name, site_prefix, article_title, article_id, client_ip, client_userAgent, source);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse("view");
		}else{
			return Helper.getFailResponse(source + "\n" + errBuff.toString());
		}	
	}
	
}

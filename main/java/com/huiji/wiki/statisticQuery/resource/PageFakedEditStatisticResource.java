package com.huiji.wiki.statisticQuery.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.JSONObject;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.huiji.wiki.statisticQuery.mongodb.FakedPageEditRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.util.Helper;

@Path("/fakedEdit")
public class PageFakedEditStatisticResource {

	private static String DB_Name = "userData";
	private static String FakedEdit_Collection = "fakedEditRecords";
//	private static String FakedEdit_Collection = "test-fake";

	
	@POST
	@Path("/insertOneFakedPageEditRecord")
	@Produces("application/json")
	@Consumes("application/json")
	public String insertOneFakedPageEditRecord(String input){
		StringBuffer errBuff = new StringBuffer();
		int userId = 0;
		String targetDate = null;
		int num = 0;

		try{
			JSONObject inputInfo = new JSONObject(input);
			userId = inputInfo.optInt("userId", -1);
			targetDate = inputInfo.optString("targetDate");
			num = inputInfo.optInt("num", 0);
			
			FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(ProjectR.client, DB_Name, FakedEdit_Collection);
			
			if(userId <= 0 || num <=0 ) return Helper.getFailResponse("userId or num is not valid");
			fakedEditH.insertOneFakedPageEditRecord(userId, num, targetDate);
		}catch (Exception e) { 
			errBuff.append(e.toString());
		}
		
		if(errBuff.length() == 0){
			return Helper.getSuccessResponse(userId + ":" + num + ":" + targetDate);
		}else{
			return Helper.getFailResponse(errBuff.toString());
		}
	}

}

package com.huiji.wiki.statisticQuery;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.huiji.wiki.statisticQuery.mongodb.FakedPageEditRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.mongodb.MongoDBClientInfo;
import com.huiji.wiki.statisticQuery.mongodb.PageEditRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.mongodb.PageViewRecordsMongoDBHandler;
import com.huiji.wiki.statisticQuery.util.DateAndTimeProcessor;
import com.huiji.wiki.statisticQuery.util.Helper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.*;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Filters;
//import ua_parser.Client;
//import ua_parser.Parser;
import com.snowplowanalytics.refererparser.*;

import ua_parser.Client;
import ua_parser.Parser;




public class Test {

	
public static String getAllPageEditCountOnWikiSiteFromUserId(String input){
    MongoClient client = new MongoDBClientInfo("huijidata.com:27017","userData").getMongoClient();

		JSONObject inputInfo = new JSONObject(input);
		int count = 0;
		
		StringBuffer errBuff = new StringBuffer();
		int userId = inputInfo.opt("userId") == null ? -1 : inputInfo.optInt("userId");
		String sitePrefix = inputInfo.optString("sitePrefix");
		String fromDate = inputInfo.optString("fromDate");
		String toDate = inputInfo.optString("toDate");
		
		PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(client, "userData","editRecords");
		FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(client, "userData","fakedEditRecords");
		try{
			count += editH.getPageEditCountOnWikiSiteFromUserId(userId, sitePrefix , fromDate, toDate);
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
	
public static String getAllPageEditRecordsFromUserIdGroupByDay(String input){
    MongoClient client = new MongoDBClientInfo("huijidata.com:27017","userData").getMongoClient();

    JSONArray result = new JSONArray();
	HashMap<String, Integer> out = new HashMap<String, Integer>();
	JSONObject inputInfo = new JSONObject(input);
	
	
	StringBuffer errBuff = new StringBuffer();
	int userId = inputInfo.optInt("userId", -1);

	System.out.print(userId);
	String fromDate = inputInfo.optString("fromDate");
	String toDate = inputInfo.optString("toDate");
	
	PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(client, "userData","editRecords");
	FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(client, "userData","fakedEditRecords");
	
	try{
		
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
	
	if(errBuff.length() == 0){
		return Helper.getSuccessResponse(result.put(out));
	}else{
		return Helper.getFailResponse(errBuff.toString());
	}
}
	
	public static String insertOneFakedPageEditRecord(String input){
	    MongoClient client = new MongoDBClientInfo("huijidata.com:27017","userData").getMongoClient();

		JSONObject inputInfo = new JSONObject(input);
		
		StringBuffer errBuff = new StringBuffer();
		int userId = inputInfo.optInt("userId", -1);
		String targetDate = inputInfo.optString("targetDate");
		int num = inputInfo.optInt("num", 0);
		FakedPageEditRecordsMongoDBHandler fakedEditH = new FakedPageEditRecordsMongoDBHandler(client, "userData","fakedEditRecords");
		if(userId <= 0 || num <=0 ) return Helper.getFailResponse("userId or num is not valid");

		try{
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
	
	public static String getPageEditCountOnWikiSiteFromUserId(String input){
	    MongoClient client = new MongoDBClientInfo("huijidata.com:27017","userData").getMongoClient();

		JSONObject inputInfo = new JSONObject(input);
		int count = 0;
		
		StringBuffer errBuff = new StringBuffer();
		int userId = inputInfo.optInt("userId", -1);
		String fromDate = inputInfo.optString("fromDate");
		String toDate = inputInfo.optString("toDate");
		String sitePrefix = inputInfo.optString("sitePrefix");
		
		PageEditRecordsMongoDBHandler editH = new PageEditRecordsMongoDBHandler(client, "userData","editRecords");
		try{
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
	

	public static void main(String[] args) throws IOException, GeoIp2Exception {
		// TODO Auto-generated method stub		
		//System.out.println(ProjectR.hostParser.extractInfo("http://www.huiji.wiki/"));
		//System.out.println(ProjectR.uaParser.extractInfo("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-CN; Redmi Note 3 Build/LMY47V) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.9.5.729 U3/0.8.0 Mobile Safari/534.30"));
		
		//MongoDBClientInfo clientInfo = new MongoDBClientInfo("huijidata.com:27017","userData");
		//MongoClient client = clientInfo.getMongoClient();
		
		PageEditRecordsMongoDBHandler h = new PageEditRecordsMongoDBHandler(ProjectR.client, "userData","test-edit");
		//h.insertOnePageEditRecord("zhang", 0, "zhang01", "zhanbg", "dfd", 0, 0, "122.158.86.112", "");
		PageViewRecordsMongoDBHandler h1 = new PageViewRecordsMongoDBHandler(ProjectR.client, "userData","test-view");

		h1.print(Filters.regex("user_agent.device", "Spider"));
		
		
		
		//StatisticResource sr = new StatisticResource();
		//FakedPageEditRecordsMongoDBHandler h = new FakedPageEditRecordsMongoDBHandler(client, "userData","fakedEditRecords");
		//h.deleteFakedPageEditRecord(543,"");
		//h.deleteFakedPageEditRecord(543, "2016-04-01");
		//System.out.println(Helper.printAggregateResult(h.getFakedPageEditRecordsFromUserIdGroupByDay(543, "", "")));
		//System.out.println(h.getFakedPageEditCountFromUserId(543, "", ""));
		//System.out.println(DateAndTimeProcessor.getDateStr("2016-2-4"));
		//h.insertOneFakedPageEditRecord(543, 20, "2016-03-08");
		//PageEditRecordsMongoDBHandler h = new PageEditRecordsMongoDBHandler(client, "userData","editRecords");
		//AggregateIterable<Document> r = h.getPageEditRecordsOnWikiSiteFromUserIdGroupByDay(-1,"","2016-03-01","");
		
		//System.out.println(sr.getPageEditorRecordsGroupByWikiSite("{fromDate:2016-03-01}"));

		//System.out.println(sr.getAllPageEditRecordsFromUserIdGroupByDay("{}"));
		//System.out.println(sr.getPageViewCountOnWikiSiteFromUserId("{userId:543,fromDate:2016-03-10}"));
		//System.out.println(sr.getAllPageEditCountFromUserId("{userId:543}"));
		//System.out.println(sr.getPageEditCountOnWikiSiteFromUserId("{userId:543,sitePrefix:zhang0}"));
		//System.out.println(Test.getPageEditCountOnWikiSiteFromUserId("{userId:543,fromDate:2016-03-01:toDate:2016fd;}"));

		//System.out.println(Test.insertOneFakedPageEditRecord("{userId:543,targetDate:2016-04-01,num:10}"));

		//System.out.println(Test.getAllPageEditRecordsFromUserIdGroupByDay("{userId:dfdfd,fromDate:2016-03-03,toDate:2017-03-10}"));
		//System.out.println(h.getPageEditorRecordsGroupByWikiSite("2016-03-09","2016-03-09"));
		//System.out.println(sr.insertOnePageViewRecord("{}"));
		//System.out.println(h.getPageEditCountOnWikiSiteFromUserId(543, "", "", ""));
		
		//System.out.println(Test.getAllPageEditCountOnWikiSiteFromUserId("{userId:-1,fromDate:2014-03-03,toDate:2017-03-10}"));
		
		
		/*
		// A File object pointing to your GeoIP2 or GeoLite2 database
		File database = new File("/Users/Zhang/Documents/GeoLite2-City.mmdb");

		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		
		
		DatabaseReader reader = new DatabaseReader.Builder(database).build();

		InetAddress ipAddress = InetAddress.getByName("111.193.160.232");

		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		CityResponse response = reader.city(ipAddress);

		Country country = response.getCountry();
		System.out.println(country.getIsoCode());            // 'US'
		System.out.println(country.getName());               // 'United States'
		System.out.println(country.getNames().get("zh-CN")); // '美国'

		Subdivision subdivision = response.getMostSpecificSubdivision();
		System.out.println(subdivision.getName());    // 'Minnesota'
		System.out.println(subdivision.getIsoCode()); // 'MN'

		City city = response.getCity();
		System.out.println(city.getName()); // 'Minneapolis'

		Postal postal = response.getPostal();
		System.out.println(postal.getCode()); // '55455'

		Location location = response.getLocation();
		System.out.println(location.getLatitude());  // 44.9733
		System.out.println(location.getLongitude()); // -93.2323
		*/
		
	}

}

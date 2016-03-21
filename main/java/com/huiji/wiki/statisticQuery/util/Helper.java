package com.huiji.wiki.statisticQuery.util;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Filters;

public class Helper {

	
	
	public static Bson getFilters(int userId, String sitePrefix, String fromDate, String toDate){
		Bson filters = Filters.and(Filters.gte("timeStamp", DateAndTimeProcessor.getFromDate(fromDate)),
                Filters.lt("timeStamp",DateAndTimeProcessor.getToDate(toDate))
                );
		if(userId > 0){
			filters = Filters.and(Filters.eq("userId", userId),filters);
		}
		if(sitePrefix != null && !sitePrefix.trim().equals("")){
			filters = Filters.and(Filters.eq("wikiSite", sitePrefix),filters);
		}
		
		return filters;
	}
	
	 
	
	public static String getFailResponse(Object err){
		JSONObject out = new JSONObject();
		out.put("status", "fail").put("err", err);
		return out.toString();
	}
	
	public static String getSuccessResponse(Object result){
		JSONObject out = new JSONObject();
		out.put("status", "success").put("result", result);
		return out.toString();
	}
	
	public static String printAggregateResult(AggregateIterable<Document> in){
		JSONArray out = new JSONArray();
		for(Document doc : in){
			out.put(doc);
		}
		return out.toString();
	}
	
	public static String getIpAddress(HttpServletRequest request){
		 String ip  =  request.getHeader( " x-forwarded-for " );
         if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
            ip  =  request.getHeader( " Proxy-Client-IP " );
        }
         if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
            ip  =  request.getHeader( " WL-Proxy-Client-IP " );
        }
         if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
           ip  =  request.getRemoteAddr();
       }
        return  ip;
	}
	
	public static String clearStringData(String data){
		if(data == null || data.trim().equals("")) return null;
		return data;
	}
	
	
}

package com.huiji.wiki.statisticQuery.mongodb;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.huiji.wiki.statisticQuery.util.DateAndTimeProcessor;
import com.huiji.wiki.statisticQuery.util.Helper;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

public class PageViewRecordsMongoDBHandler extends MongoDBHandler {

	public PageViewRecordsMongoDBHandler(MongoClient client, String database, String collection) {
		super(client, database, collection);
	}
	
	public int getPageViewCountOnWikiSiteFromUserId(int userId, String sitePrefix, String fromDate, String toDate){
		Bson filters = Helper.getFilters(userId, sitePrefix, fromDate, toDate);
		return super.count(filters);
	}
	
	public JSONArray getPageViewRecordsOnWikiSiteFromUserIdGroupByDay(int userId, String sitePrefix, String fromDate, String toDate){
		JSONArray result = new JSONArray();
		Bson filters = Helper.getFilters(userId, sitePrefix, fromDate, toDate);
		Bson match = Aggregates.match(filters);
		Bson project = Aggregates.project(Projections.fields(Projections.include("date"), Projections.excludeId()));
		Bson group = Aggregates.group("$date", Accumulators.sum("value", 1));
		Bson sort = Aggregates.sort(Sorts.descending("value"));
		for (Document doc : super.aggregate(Arrays.asList(match,project,group,sort)).allowDiskUse(true)){
			result.put(doc);
		};
		return result;
	}
	
	
	public JSONArray getPageViewRecordsFromUserIdGroupByWikiSite(int userId, String fromDate, String toDate){
		JSONArray result = new JSONArray();
		Bson match = Aggregates.match(Helper.getFilters(userId, null, fromDate, toDate));
		Bson project = Aggregates.project(Projections.fields(Projections.include("wikiSite"), Projections.excludeId())); 
		Bson group = Aggregates.group("$wikiSite", Accumulators.sum("value", 1));
		Bson sort = Aggregates.sort(Sorts.descending("value"));
		for (Document doc : super.aggregate(Arrays.asList(match,project,group,sort)).allowDiskUse(true)){
			result.put(doc);
		};
		return result;
	}
	
	public void insertOnePageViewRecord(String user_name,
										int user_id,
										String site_name,
										String site_prefix,
										String article_title,
										int article_id,
										String client_ip,
										String client_userAgent,
										String source){
		
		Document record = new Document("userId",user_id)
				.append("userName", user_name)
				.append("articleId", article_id)
				.append("titleName", article_title)
				.append("wikiSite", site_prefix)
				.append("siteName", site_name)
				.append("date", DateAndTimeProcessor.getDateStr())
				.append("timeStamp",DateAndTimeProcessor.getDateAndTime())
				.append("raw_source", source)
				.append("raw_userAgent", client_userAgent)
				.append("user_ip", client_ip)
				.append("user_location", ProjectR.ipParser.extractInfo(client_ip))
				.append("user_agent", ProjectR.uaParser.extractInfo(client_userAgent))
				.append("source", ProjectR.hostParser.extractInfo(source));
			
		super.insert(record);	
	}
	

	
	
	
	
	

}

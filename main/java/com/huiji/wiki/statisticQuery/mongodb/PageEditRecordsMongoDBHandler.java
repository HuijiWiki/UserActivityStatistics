package com.huiji.wiki.statisticQuery.mongodb;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.huiji.wiki.statisticQuery.util.DateAndTimeProcessor;
import com.huiji.wiki.statisticQuery.util.Helper;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
public class PageEditRecordsMongoDBHandler extends MongoDBHandler{

	public PageEditRecordsMongoDBHandler(MongoClient client, String database, String collection) {
		super(client, database, collection);
	}

	
	
	public int getPageEditCountOnWikiSiteFromUserId(int userId, String sitePrefix, String fromDate, String toDate){
		Bson filters = Helper.getFilters(userId, sitePrefix, fromDate, toDate);
		return super.count(filters);
	}
	
	public AggregateIterable<Document> getPageEditRecordsOnWikiSiteFromUserIdGroupByDay(int userId, String sitePrefix, String fromDate, String toDate){
		
		Bson filters = Helper.getFilters(userId, sitePrefix, fromDate, toDate);
		Bson match = Aggregates.match(filters);
		Bson project = Aggregates.project(Projections.fields(Projections.include("date"), Projections.excludeId()));
		Bson group = Aggregates.group("$date", Accumulators.sum("value", 1));
		Bson sort = Aggregates.sort(Sorts.descending("value"));
		return super.aggregate(Arrays.asList(match,project,group,sort));
	}
	
	
	public AggregateIterable<Document> getPageEditRecordsFromUserIdGroupByWikiSite(int userId, String fromDate, String toDate){
		
		Bson match = Aggregates.match(Helper.getFilters(userId, null, fromDate, toDate));
		Bson project = Aggregates.project(Projections.fields(Projections.include("wikiSite"), Projections.excludeId())); 
		Bson group = Aggregates.group("$wikiSite", Accumulators.sum("value", 1));
		Bson sort = Aggregates.sort(Sorts.descending("value"));
		return super.aggregate(Arrays.asList(match,project,group,sort));
		
	}
	
	public AggregateIterable<Document> getPageEditorRecordsGroupByWikiSite(String fromDate, String toDate){
		
		Bson match = Aggregates.match(Helper.getFilters(-1, null, fromDate, toDate));
		Bson project = Aggregates.project(Projections.fields(Projections.include("wikiSite","userId"), Projections.excludeId())); 
		Bson group1 = Aggregates.group(Document.parse("{userId:'$userId',wikiSite:'$wikiSite'}"));
		Bson group2 = Aggregates.group("$_id.wikiSite",Accumulators.sum("value", 1));
		Bson sort = Aggregates.sort(Sorts.descending("value"));
		return super.aggregate(Arrays.asList(match,project,group1,group2,sort));
	}
	
	public void insertOnePageEditRecord(String user_name,
										int user_id,
										String site_name,
										String site_prefix,
										String page_title,
										int page_id,
										int page_ns,
										String client_ip,
										String client_userAgent
										){
		Document record = new Document()
				.append("userId", user_id)
				.append("userName", user_name)
				.append("articleId", page_id)
				.append("page_ns", page_ns)
				.append("titleName", page_title)
				.append("wikiSite", site_prefix)
				.append("siteName", site_name)
				.append("date", DateAndTimeProcessor.getDateStr())
				.append("timeStamp",DateAndTimeProcessor.getDateAndTime())
				.append("raw_userAgent", client_userAgent)
				.append("user_ip", client_ip)
				.append("user_location", ProjectR.ipParser.extractInfo(client_ip))
				.append("user_agent", ProjectR.uaParser.extractInfo(client_userAgent));
		super.insert(record);
	}

}

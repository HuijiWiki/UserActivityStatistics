package com.huiji.wiki.statisticQuery.mongodb;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.huiji.wiki.statisticQuery.util.DateAndTimeProcessor;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

public class FakedPageEditRecordsMongoDBHandler extends MongoDBHandler{

	public FakedPageEditRecordsMongoDBHandler(MongoClient client, String database, String collection) {
		super(client, database, collection);
	}

	
	public int getFakedPageEditCountFromUserId(int userId, String fromDate, String toDate){
		Bson filters = Filters.and(Filters.gte("targetDate", DateAndTimeProcessor.getFromDate(fromDate)),
                Filters.lt("targetDate",DateAndTimeProcessor.getToDate(toDate)));
		if(userId > 0) filters = Filters.and(Filters.eq("userId", userId),filters);
		Bson match = Aggregates.match(filters);
		Bson group = Aggregates.group(null, Accumulators.sum("count", "$count"));
		return super.sum(Arrays.asList(match,group));
	}
	
	public AggregateIterable<Document> getFakedPageEditRecordsFromUserIdGroupByDay(int userId, String fromDate, String toDate){
		
		Bson filters = Filters.and(Filters.gte("targetDate", DateAndTimeProcessor.getFromDate(fromDate)),
                Filters.lt("targetDate",DateAndTimeProcessor.getToDate(toDate)));
		if(userId > 0) filters = Filters.and(Filters.eq("userId", userId),filters);
		Bson match = Aggregates.match(filters);
		Bson project = Aggregates.project(Projections.fields(Projections.include("date","count"), Projections.excludeId())); 
		Bson group = Aggregates.group("$date", Accumulators.sum("value", "$count"));
		return super.aggregate(Arrays.asList(match,project,group));
	}
	
	public void insertOneFakedPageEditRecord(int userId, int num, String date){
		Document record = new Document("count",num)
							.append("userId", userId)
							.append("targetDate", DateAndTimeProcessor.getDate(date))
							.append("date", DateAndTimeProcessor.getDateStr(date))
							.append("timeStamp",DateAndTimeProcessor.getDateAndTime());
		super.insert(record);	
	}
	
	public void deleteFakedPageEditRecord(int userId, String date){
		Bson filter ;
		if(date == null || date.equals("")) {
			filter = Filters.eq("userId",userId);
		}else{
			filter = Filters.and(Filters.eq("date", DateAndTimeProcessor.getDateStr(date)),
	                Filters.eq("userId",userId));
		}
		super.delete(filter);
	}
	
	
}

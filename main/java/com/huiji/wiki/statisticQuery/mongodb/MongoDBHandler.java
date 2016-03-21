package com.huiji.wiki.statisticQuery.mongodb;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;


public abstract class MongoDBHandler {

	public String database;
	public String collection;
	public MongoClient client;
	
	public MongoDBHandler(MongoClient client, String database, String collection){
		this.client = client;
		this.database = database;
		this.collection = collection;
	}
	
	public int count(Bson filter){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		return (int)collection.count(filter);
	}
	
	public AggregateIterable<Document> aggregate(List<Bson> stages){
		
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		return collection.aggregate(stages).allowDiskUse(true);
	}
	
	public int sum(List<Bson> stages){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		Document doc = collection.aggregate(stages).allowDiskUse(true).first();
		if(doc == null) return 0;
		return doc.getInteger("count").intValue();
	}
	
	public void insert(Document doc){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		collection.insertOne(doc);
	}
	
	public void delete(Bson filter){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		collection.deleteMany(filter);
	}
	
	public FindIterable<Document> find(Bson filter){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		return collection.find(filter);
	}
	
	public void print(){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		for(Document doc: collection.find()){
			
			System.out.println(doc.get("raw_source") + doc.getString("raw_userAgent") );
		
			
		}
		
		
	}
	
	
	
	public void print(Bson filter){
		MongoCollection<Document> collection = this.client.getDatabase(this.database).getCollection(this.collection);
		for(Document doc: collection.find(filter)){
			
			System.out.println(doc.get("raw_source") + doc.getString("raw_userAgent") );
			
			/*
			if(t.getString("source").equals("Google") || t.getString("source").equals("google")) 
				System.out.println(t.toJson());
			}
			*/
			
		}
	}
	
	
}

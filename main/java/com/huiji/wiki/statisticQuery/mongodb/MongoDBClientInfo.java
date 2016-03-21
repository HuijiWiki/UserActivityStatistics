package com.huiji.wiki.statisticQuery.mongodb;

import com.huiji.wiki.statisticQuery.Confidential;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBClientInfo {

	String uri;
	String dbName;
	
	
	public MongoDBClientInfo(String uri, String dbName){
		this.uri = uri;
		this.dbName = dbName;
	}
	


	public MongoClient getMongoClient(){
		String auth = Confidential.username + ":" + Confidential.password;
		String mongodbURL = "mongodb://" + auth + "@" + this.uri + "/" + this.dbName;
		MongoClientURI connectionString = new MongoClientURI(mongodbURL);
		return new MongoClient(connectionString);
	}

}

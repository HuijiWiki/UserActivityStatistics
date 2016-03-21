package com.huiji.wiki.statisticQuery;

import com.huiji.wiki.statisticQuery.mongodb.MongoDBClientInfo;
import com.huiji.wiki.statisticQuery.util.HostParser;
import com.huiji.wiki.statisticQuery.util.IpParser;
import com.huiji.wiki.statisticQuery.util.UserAgentParser;
import com.mongodb.MongoClient;

public class ProjectR {

	public static MongoClient client = new MongoDBClientInfo("huijidata.com:27017","userData").getMongoClient();
	public static UserAgentParser uaParser = new UserAgentParser();
	public static HostParser hostParser = new HostParser();
	public static IpParser ipParser = new IpParser();
	
}

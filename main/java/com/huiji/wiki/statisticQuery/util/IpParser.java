package com.huiji.wiki.statisticQuery.util;

import java.io.IOException;
import java.net.InetAddress;

import org.bson.Document;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

public class IpParser {

	
	private static final String DB_PATH = "/GeoLite2-City.mmdb";
	private DatabaseReader reader;
	
	
	
	public IpParser(){
		try {
			this.reader = new DatabaseReader.Builder(IpParser.class.getResourceAsStream(DB_PATH)).withCache(new CHMCache()).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Document extractInfo(String ip){
		Document result = null;	
		if(ip == null || ip.trim().equals("")) return null;
		try {
			InetAddress ipAddress = InetAddress.getByName(ip);
			CityResponse response = reader.city(ipAddress);
			result = new Document();
			result.append("country", response.getCountry().getName())
			      .append("subdivision", response.getMostSpecificSubdivision().getName())
			      .append("city", response.getCity().getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(ProjectR.ipParser.extractInfo("119.117.201.69"));
	}

}

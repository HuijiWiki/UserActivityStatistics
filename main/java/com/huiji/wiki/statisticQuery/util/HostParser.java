package com.huiji.wiki.statisticQuery.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.bson.Document;

import com.huiji.wiki.statisticQuery.ProjectR;
import com.snowplowanalytics.refererparser.CorruptYamlException;
import com.snowplowanalytics.refererparser.Parser;
import com.snowplowanalytics.refererparser.Referer;



public class HostParser {

	private Parser hostParser;
	
	public HostParser(){
		try {
			this.hostParser = new Parser();
		} catch (IOException | CorruptYamlException e) {
			e.printStackTrace();
		}
	}
	
	public Document extractInfo(String rawInfo){
		Document result = null;
		if(rawInfo == null || rawInfo.trim().equals("")) return null;
		try {
			Referer r = this.hostParser.parse(rawInfo, "www.huiji.wiki");
			result = new Document();
			result.append("medium", r.medium.toString())
			      .append("source", r.source)
			      .append("term", r.term);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args){
		System.out.println(ProjectR.hostParser.extractInfo("http://awoiaf.westeros.org/index.php/Valyria"));
	}
}

package com.huiji.wiki.statisticQuery.util;

import java.io.IOException;
import org.bson.Document;

import com.huiji.wiki.statisticQuery.ProjectR;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentParser {

	private Parser uaParser;
	
	public UserAgentParser(){
		try {
			this.uaParser = new Parser();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Document extractInfo(String rawInfo){
		Document result = null;
		if(rawInfo == null || rawInfo.trim().equals("")) return result;
		Client c = this.uaParser.parse(rawInfo);
		result = new Document();
		result.append("browser",c.userAgent.family)
			  .append("os", c.os.family)
			  .append("device", c.device.family);
		return result;
	}
	
	public static void main(String[] args){
		System.out.println(ProjectR.uaParser.extractInfo("Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn; PLK-AL10 Build/HONORPLK-AL10) AppleWebKit/534.24 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.24 T5/2.0 baiduboxapp/7.1 (Baidu; P1 5.0.2)"));
	}
}

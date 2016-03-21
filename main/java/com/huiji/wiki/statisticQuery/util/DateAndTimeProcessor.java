package com.huiji.wiki.statisticQuery.util;


import java.util.Date;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

public class DateAndTimeProcessor {

	public static Date getFromDate(String fromDate){
		String fromDateStr = (fromDate == null || fromDate.trim().equals("")) ? "1970-01-01" : fromDate;
		return new DateTime(fromDateStr).withZone(DateTimeZone.forID("PRC")).toDate();
		
	}

	public static Date getToDate(String toDate){
		String toDateStr = (toDate == null || toDate.trim().equals("")) ? "2070-01-01" :toDate;
		return new DateTime(toDateStr).withZone(DateTimeZone.forID("PRC")).plusDays(1).toDate();
		
	}
	
	public static Date getDate(String date){
		return new DateTime(date).withZone(DateTimeZone.forID("PRC")).toDate();
	}
	
	public static Date getDateAndTime(){
		return new DateTime().withZone(DateTimeZone.forID("PRC")).toDate();
	}
	
	
	public static String getDateStr(String date){
		return new DateTime(date).withZone(DateTimeZone.forID("PRC")).toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
	}
	

	public static String getDateStr(){
		return new DateTime().withZone(DateTimeZone.forID("PRC")).toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
	}
}

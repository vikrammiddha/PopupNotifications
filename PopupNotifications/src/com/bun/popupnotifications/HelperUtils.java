package com.bun.popupnotifications;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class HelperUtils {
	
	public static int getDateTimeValue(String date){
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime dt = formatter.parseDateTime(date);
        return dt.getMillisOfSecond();
	}
	
	public static int getCurrentTime(){
		DateTime dt = new DateTime();
		return dt.getMillisOfSecond();
	}

}

package com.nice.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
	
	public static Date Fn_FindBeforeDate(int defore) {
		Date now = new Date();
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		String todayCal = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, -30);
		todayCal = format.format(cal.getTime());  
		System.out.println(todayCal);
		Date beforeDate = null;
		
		try {
			beforeDate = format.parse(todayCal);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return beforeDate;
	}
}

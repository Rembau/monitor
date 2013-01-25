package test;

import java.util.Calendar;

public class Time {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int year,month,day,hour,mi,second;
		Calendar ca = Calendar.getInstance();
		year=ca.get(Calendar.YEAR);
		month=ca.get(Calendar.MONTH)+1;
		day=ca.get(Calendar.DATE);
		hour=ca.get(Calendar.HOUR);
		mi=ca.get(Calendar.MINUTE);
		second=ca.get(Calendar.SECOND);
		System.out.println(year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second);
	}

}

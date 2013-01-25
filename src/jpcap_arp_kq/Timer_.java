package jpcap_arp_kq;

import java.util.Timer;

public class Timer_ {
	public static int scanTime=3*60;
	public Timer_(){
		Timer timer=new Timer();
		Task t1=new Task();
		timer.schedule(t1, 1*1000, scanTime*1000);
	}
	public static void main(String args[]){
		new Timer_();
	}
}

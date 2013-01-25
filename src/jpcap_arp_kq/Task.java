package jpcap_arp_kq;

import java.util.TimerTask;

public class Task extends TimerTask {
	public Task(){
	}
	public void run() {
		new SendHandle().handle();
	}

}

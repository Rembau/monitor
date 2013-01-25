package snmp;

import gui.MonitorPanel;
import gui.NoClassPanel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;

import conn.DBoperate;

public class Task extends TimerTask {
	String type;
	//JLabel label;
	//JButton button;
	public Task(String type){
		this.type=type;
	}
	public void setLabel(JLabel label){
		//this.label=label;
	}
	public void setButton(JButton button){
		//this.button=button;
	}
	public void run() {
		if(type.equals("allScanPc")){
			Calendar ca=Calendar.getInstance();
			new DBoperate().update("update machine_information set record_time ='"+new Timestamp(ca.getTimeInMillis())+"';");
			//label.setText("正在扫描");
			//button.setEnabled(false);
			new StartHandle().getAllIp_Mac();
			//label.setText("休息中");
			//button.setEnabled(true);
		}
		else if(type.equals("scanRouterIndex")){
			ScanHandle sh=new ScanHandle("");
			sh.getAllRouter("192.168.21.1");
			sh.getAllRouter("192.168.7.1");
			sh.getAllRouter("192.168.30.1");
			sh.getAllRouter("172.18.125.254");
		}
		else if(type.equals("showIPNum")){
			//System.out.println("---------------------------");
			ScanIPNum sc=new ScanIPNum();
			if(NoClassPanel.iPPart.equals("")){
				return;
			}
			sc.init(NoClassPanel.iPPart);
			String str=sc.search_(SnmpAll.oid_7);
			System.out.println("========================="+NoClassPanel.iPPart+" "+str);
			NoClassPanel.setShowIPNumTxt("接受的IP数据报数量:"+str);
		}
	}
	public static void main(String[] args) {
		Timer timer=new Timer();
		Task t1=new Task("allScanPc");
		//t1.setLabel(MonitorPanel.p1.getLabel());
		//t1.setButton(MonitorPanel.p1.getB());
		Task t2=new Task("scanRouterIndex");
		Task t3=new Task("showIPNum");
		new Timer().schedule(t3, 0, 5*1000);
		timer.schedule(t1, 0*1000, MonitorPanel.allScanPcTime*1000);
		timer.schedule(t2, 20*1000, MonitorPanel.scanRouterIndexTime*1000);
	}
}

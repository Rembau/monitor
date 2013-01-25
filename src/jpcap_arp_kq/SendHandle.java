package jpcap_arp_kq;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import conn.DBoperate;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

public class SendHandle {
	String startIp;
	String endIp;
	String room;
	String ip;
	DBoperate db=new DBoperate();
	public SendHandle(){
	}
	public void handle(){
		int mark=1;
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		for(int i=0;i<devices.length;i++){
			for (NetworkInterfaceAddress a : devices[i].addresses){
				if(a.address.toString().length()!=1){
					ip=a.address.toString().replace("/", "");
					mark=i;
				}
			}
		}
		System.out.println(ip);
		ResultSet rs=db.select("select * from machine_room where room_ip='"+ip+"'");
		try {
			if(rs.next()){
				this.startIp=rs.getString("room_startIp");
				this.endIp=rs.getString("room_endIp");
				this.room=rs.getString("room_name");
			}
			else{
				System.out.println("Ã»ÓÐ¸ÃIP");
				return;
			}
			rs.getStatement().close();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		JpcapCaptor jpcap=null;
		try {
			jpcap = JpcapCaptor.openDevice(devices[mark], 2000, false, 20);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			ARPPacket_ arp=new ARPPacket_();
			JpcapSender s=JpcapSender.openDevice(devices[1]);
			int start=Integer.parseInt(startIp.substring(startIp.lastIndexOf(".")+1));
			int end=Integer.parseInt(endIp.substring(endIp.lastIndexOf(".")+1));
			String ipPart=startIp.substring(0,startIp.lastIndexOf(".")+1);
			System.out.println(start+" "+end);
			for(int i=start;i<=end;i++){
				arp.setIP(ipPart+""+i);
				s.sendPacket(arp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Dump_ARP a=new Dump_ARP(devices[1]);
		new Th1(jpcap,a).start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int year,month,day,hour,mi,second;
		Calendar ca = Calendar.getInstance();
		year=ca.get(Calendar.YEAR);
		month=ca.get(Calendar.MONTH)+1;
		day=ca.get(Calendar.DATE);
		hour=ca.get(Calendar.HOUR_OF_DAY);
		mi=ca.get(Calendar.MINUTE);
		second=ca.get(Calendar.SECOND);
		System.out.print(a.getOnlineIP().size()+" "+a.getOnlineIP());
		db.update("update machine_room set room_checkTime='"+year+"-"+month+"-"+day+"-"+hour+"-"+mi+"' where room_ip='"+ip+"'");
		//DBoperate.update("update machine_roomPC set computer_isOnline='n' where computer_ip='"+ip+"'");
		rs=db.select("select * from machine_roomPC where computer_room='"+room+"'");
		Hashtable<String,String> pc=new Hashtable<String,String>();
		try {
			while(rs.next()){
				pc.put(rs.getString("computer_ip"), rs.getString("computer_isOnline"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String str:a.getOnlineIP()){
			if(!pc.containsKey(str.toString())){
				db.insert("insert machine_roomPC(computer_ip,computer_room) values('"+str+"','"+room+"')");
				db.update("update machine_roomPC set computer_isOnline='y',computer_onTime='"+year+"-"+month+"-"+day+"-"+hour+"-"+mi+"-"+second+"'" +
						" where computer_ip='"+str+"'");
			}
			else{
				pc.remove(str);
			}
		}
		Enumeration<String> noOnline=pc.keys();
		while(noOnline.hasMoreElements()){
			String str=noOnline.nextElement();
			db.update("update machine_roomPC set computer_isOnline='n'" +
					",computer_downTime='"+year+"-"+month+"-"+day+"-"+hour+"-"+mi+"-"+second+"'" +
					" where computer_ip='"+str+"'");
		}
		jpcap.close();
	}
	public static void main(String[] args) {
		new SendHandle().handle();
	}
}

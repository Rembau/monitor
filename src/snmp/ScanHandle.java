package snmp;

import gui.MonitorPanel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Calendar;

import conn.DBoperate;

public class ScanHandle extends SnmpAll{
	int timeSpace=MonitorPanel.allScanPcTime/60;
	String str="";
	DBoperate db = new DBoperate();
	public ScanHandle(String name){
		this.setName(name);
	}
	/**
	 * 获得ip地址Mac地址对照表
	 */
	
	public void run(){
		getIp_Mac(this.getStr());
		System.out.println(this.routerIP+"结束");
	}
	public String getStr(){
		return this.str;
	}
	public void setStr(String str){
		this.str=str;
	}
	@SuppressWarnings("unused")
	public void setNotOnline(Hashtable<String,String> h,String index){
		String iPPart=this.routerIP.substring(0, this.routerIP.lastIndexOf("."));
		ResultSet rs=db.select("select * from machine_computer where computer_ip like '"+iPPart+".%'");
		try {
			int year,month,day,hour,mi,second;
			Calendar ca = Calendar.getInstance();
			year=ca.get(Calendar.YEAR);
			month=ca.get(Calendar.MONTH)+1;
			day=ca.get(Calendar.DATE);
			hour=ca.get(Calendar.HOUR_OF_DAY);
			mi=ca.get(Calendar.MINUTE);
			second=ca.get(Calendar.SECOND);
			while(rs.next()){
				if(!h.containsKey(SnmpAll.oid_5+"."+index+"."+rs.getString("computer_ip"))){
					//System.out.println(rs.getString("computer_ip")+"不在线"+SnmpAll.oid_5+"."+index+"."+rs.getString("computer_ip"));
					if(rs.getString("computer_isOnline").equals("y")){
						db.update("update machine_computer set computer_isOnline='n'," +
							"computer_downTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' " +
							"where computer_ip='"+rs.getString("computer_ip")+"'");
					}
					else{
						String time=rs.getString("computer_onlineTime");
						//System.out.println(time);
						if(time!=null){
							String time_1[]=time.split(" ");
							String time_2[]=time_1[0].split("-");
							String time_3[]=time_1[1].split(":");
							int year_=Integer.parseInt(time_2[0]);
							int month_=Integer.parseInt(time_2[1]);
							int day_=Integer.parseInt(time_2[2]);
							if(day!=day_){
								db.update("update machine_computer set computer_isOnline='n'," +
										"computer_dateOnlineTime='0' " +
										"where computer_ip='"+rs.getString("computer_ip")+"'");
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				rs.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("unused")
	public void getIp_Mac(String index){
		Hashtable<String,String> pc=search(SnmpAll.oid_5+"."+index);
		if(pc.size()==0){
			System.out.println("没有元素！");
			return;
		}
		setNotOnline(pc,index);                         //设置不在线标志
		Enumeration<String> keys=pc.keys();
		String key,value;
		ResultSet rs = null;
		while(keys.hasMoreElements()){
			key=keys.nextElement();
			value=pc.get(key);
			key=key.substring((SnmpAll.oid_5+"."+index).length()+1);
			//System.out.println(key+" "+value);
			int year,month,day,hour,mi,second;
			Calendar ca = Calendar.getInstance();
			year=ca.get(Calendar.YEAR);
			month=ca.get(Calendar.MONTH)+1;
			day=ca.get(Calendar.DATE);
			hour=ca.get(Calendar.HOUR_OF_DAY);
			mi=ca.get(Calendar.MINUTE);
			second=ca.get(Calendar.SECOND);
			db.insert("insert into machine_computer(computer_ip) value('"+key+"')");
			rs=db.select("select * from machine_computer where computer_ip='"+key+"'");
			try {
				if(rs.next()){
					String compute_isOnline=rs.getString("computer_isOnline");
					if(compute_isOnline.equals("n")){
						db.update("update machine_computer set computer_mac='"+value.trim()+"',computer_isOnline='y'" +
								",computer_onTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"'" +
								",computer_onlineTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' where computer_ip='"+key+"'");
					}
					else if(compute_isOnline.equals("y")){
						String time=rs.getString("computer_onlineTime");
						String time_1[],time_2[],time_3[];
						try{
							time_1=time.split(" ");
							time_2=time_1[0].split("-");
							time_3=time_1[1].split(":");
						} catch(Exception e){
							break;
						}
						int year_=Integer.parseInt(time_2[0]);
						int month_=Integer.parseInt(time_2[1]);
						int day_=Integer.parseInt(time_2[2]);
						if(year==year_){
							if(month_==month){
								int t_=Integer.parseInt(rs.getString("computer_monthOnlineTime"))+timeSpace;
								if(day==day_){
									int t=Integer.parseInt(rs.getString("computer_dateOnlineTime"))+timeSpace;
									db.update("update machine_computer set computer_dateOnlineTime='"+t+"'" +
											",computer_monthOnlineTime='"+t_+"'" +
											",computer_onlineTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' where computer_ip='"+key+"'");
								} else{
									db.update("update machine_computer set computer_dateOnlineTime='"+timeSpace+"'" +
											",computer_monthOnlineTime='"+t_+"'" +
											",computer_onlineTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' where computer_ip='"+key+"'");
								}
							}
							else{
								db.update("update machine_computer set computer_dateOnlineTime='0'" +
										",computer_monthOnlineTime='"+timeSpace+"'" +
										",computer_onlineTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' where computer_ip='"+key+"'");
							}
						}
						else{
							db.update("update machine_computer set computer_dateOnlineTime='"+timeSpace+"'" +
									",computer_monthOnlineTime='"+timeSpace+"'" +
									",computer_onlineTime='"+year+"-"+month+"-"+day+" "+hour+":"+mi+":"+second+"' where computer_ip='"+key+"'");
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
					rs.getStatement().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取与首路由有关的所有路由
	 */
	public void getAllRouter(String ip){
		ResultSet rs;
		init(ip);
		getRouter();
		
		while(true){
			rs=db.select("Select * from machine_router where router_index=0");// where router_index='null'
			try {
				if(!rs.next()){
					System.out.println("为空了");
					return;
				}
				else {
					rs.beforeFirst();
				}
				while(rs.next()){
					init(rs.getString("router_ip"));
					this.routerIP=rs.getString("router_ip");
					getRouter();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				try {
					rs.getStatement().close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}//end while
	}
	public void getRouter(){
		Hashtable<String,String> routers=search(SnmpAll.oid_2);//1.3.6.1.2.1.4.20.1.2.192.168.1.1 39
		if(routers.size()==0){
			System.out.println("没有元素！");
			return;
		}
		Enumeration<String> keys=routers.keys();
		String key,value;
		boolean flag=false;
		while(keys.hasMoreElements()){
			key=keys.nextElement();
			//System.out.println(key);
			value=routers.get(key);
			key=key.substring(21);
			//System.out.println(key+" "+value);
			if(key.startsWith("127")){
				continue;
			}
			if(key.startsWith(this.routerIP)){
				db.insert("insert into machine_router(router_ip) values('"+key+"')");
				db.update("update machine_router set router_index = '"+value+"' where router_ip='"+key+"'");
				flag=true;
			}
			else {
				db.insert("insert into machine_router(router_ip,router_index) values('"+key+"',0)");
			}
		}
		if(!flag){
			System.out.println(this.routerIP+"获取本段ip索引错误！");
		}
		else {
			System.out.println(this.routerIP+"获取本段ip索引ok！");
		}
	}
	public static void main(String[] args) {
		new ScanHandle("").getAllRouter("172.16.2.21");
	}
}

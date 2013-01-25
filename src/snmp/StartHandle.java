package snmp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import conn.DBoperate;

public class StartHandle {
	DBoperate db=new DBoperate();
	public void getAllIp_Mac(){
		ResultSet rs=db.select("Select * from machine_router");
		try {
			if(!rs.next()){
				System.out.println("Îª¿ÕÁË");
				return;
			}
			else {
				rs.beforeFirst();
			}
			HashSet<String> set=new HashSet<String>();
			while(rs.next()){
				String router_ip=rs.getString("router_ip");
				if(set.contains(router_ip.substring(0,router_ip.lastIndexOf(".")))){
					continue;
				}
				set.add(router_ip.substring(0,router_ip.lastIndexOf(".")));
				ScanHandle sc=new ScanHandle(router_ip);
				sc.init(rs.getString("router_ip"));
				sc.routerIP=rs.getString("router_ip");
				sc.setStr(rs.getString("router_index"));
				sc.start();
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("============================================½áÊø!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

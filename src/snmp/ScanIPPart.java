package snmp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import conn.DBoperate;

public class ScanIPPart extends SnmpAll{
	public ScanIPPart(){
	}
	/**
	 * ��ȡ�ƶ�·�ɵĶ�IP_MAC
	 * @param ip : ·��IP
	 */
	public void handle(String ip){
		init(ip);
		DBoperate db=new DBoperate();
		ResultSet rs=db.select("Select * from machine_router where router_ip='"+ip+"'");
		try {
			if(rs.next()){
				getIp_Mac(rs.getString("router_index"));
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
		try {
			snmp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {

	}

}

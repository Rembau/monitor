package snmp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import conn.DBoperate;

public class SnmpAll extends Thread{
	/**
	 * IP组
	 */
	public static String oid_1="1.3.6.1.2.1.4.1"; 
	/**
	 * 网关索列表
	 */
	public static String oid_2="1.3.6.1.2.1.4.20.1.2";
	/**
	 * 子网掩码
	 */
	public static String oid_3="1.3.6.1.2.1.4.20.1.3"; 
	/**
	 * MAC地址
	 */
	public static String oid_5="1.3.6.1.2.1.4.22.1.2";
	/**
	 * 映射类型
	 */	
	public static String oid_6="1.3.6.1.2.1.4.22.1.4";
	/**
	 * 接受的iP数目
	 */
	public static String oid_7="1.3.6.1.2.1.4.3";
	protected String routerIP="";
	protected PDU response=null;
	protected Vector<?> variableBindings=null;
	protected ResponseEvent response_=null;
	protected CommunityTarget target=null;
	protected PDU pdu;
	protected Snmp snmp;
	DBoperate db= new DBoperate();
	String ip="";
	
	public SnmpAll(){
	}
	/**
	 * 初始化
	 * @param ip : 要初始化的路由IP
	 */
	public void init(String ip){
		this.ip=ip;
		System.out.println(ip+"初始化开始");
		Address targetAddress = GenericAddress.parse("udp:"+ip+"/161");   
        target = new CommunityTarget();    
        target.setCommunity(new OctetString("public"));    
        target.setAddress(targetAddress);    
        target.setTimeout(3000);    
        target.setVersion(SnmpConstants.version1);
        pdu = new PDU();
        TransportMapping transport=null;
		try {
			transport = new DefaultUdpTransportMapping();
            transport.listen();    
		} catch (IOException e) {
			e.printStackTrace();
		}
        snmp = new Snmp(transport);     
        pdu.setType(PDU.GETNEXT); 
	}
	/**
	 * 获取路由记录
	 * @param oid : OID
	 * @return : 关键字为OID,记录信息为值的Hashtable
	 */
	@SuppressWarnings("unused")
	public Hashtable<String,String> search(String oid){
		Hashtable<String,String> list=new Hashtable<String,String>();
		String content=null;
		String oidstring=oid;
		int mark=0,x=0;
		while(true) {
			x++;
            pdu.add(new VariableBinding(new OID(oidstring)));
            while(true){
            	try{ 				  
            		response_ = snmp.send(pdu, target);
					response =response_.getResponse();
	            	variableBindings=response.getVariableBindings();
	            	mark=0;
	                break;
	              }catch(Exception e){
	            	  mark++;
	            	  if(mark>3){
	            		  System.out.println(this.ip+"==========error=========");
	            		  try {
	            				snmp.close();
	            			} catch (IOException e1) {
	            				// TODO Auto-generated catch block
	            				e.printStackTrace();
	            			}
	            		 return list;
	            	  }
	            	  System.out.println(this.ip+"==========重复========="+mark);
	              }
			}
			String variable=variableBindings.toString();
            int i=variable.indexOf("=");
            int j=variable.length();
            oidstring=variable.substring(1,i-1);
            content=variable.substring(i+1,j-1);
            if(oidstring.startsWith(oid)){
            	list.put(oidstring, content);
            }
            else {
            	break;
            }
			/*System.out.print(x+" ");
            System.out.print(oidstring);
            System.out.println(content);*/
            pdu.remove(0);
		}
		list.remove(oidstring);
		//System.out.println(list);
		try {
			snmp.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取当前路由的指定索引的IP_MAC
	 * @param index : 索引
	 */
	public void getIp_Mac(String index){
		Hashtable<String,String> pc=search(SnmpAll.oid_5+"."+index);
		if(pc.size()==0){
			System.out.println("没有元素！");
			return;
		}
		Enumeration<String> keys=pc.keys();
		String key,value;
		while(keys.hasMoreElements()){
			key=keys.nextElement();
			value=pc.get(key);
			key=key.substring((SnmpAll.oid_5+"."+index).length()+1);
			System.out.println(key+" "+value);
			db.insert("insert into machine_computer(computer_ip) value('"+key+"')");
			db.update("update machine_computer set computer_mac='"+value.trim()+"',computer_isOnline='y' where computer_ip='"+key+"'");
		}
	}
}

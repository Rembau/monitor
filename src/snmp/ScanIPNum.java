package snmp;

import java.io.IOException;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class ScanIPNum extends SnmpAll{
	public String search_(String oid){
		String str=null;
		int mark=0;
		pdu.add(new VariableBinding(new OID(oid)));
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
            		  System.out.println("==========error=========");
            		  try {
            				snmp.close();
            			} catch (IOException e1) {
            				e.printStackTrace();
            			}
            			return "нч";
            	  }
              }
		}
        str=variableBindings.toString();
        int i=str.indexOf("=");
        int j=str.length();
        str=str.substring(i+1,j-1);
        try {
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return str;
	}
	public static void main(String args[]){
		ScanIPNum sc=new ScanIPNum();
		sc.init("192.168.21.1");
		String str=sc.search_(SnmpAll.oid_7);
		System.out.println(str);
	}
}

package conn;

import java.util.Enumeration;
import java.util.Hashtable;

public class DBoperate_pool {
	private int maxConnectNum=50;  //�����������Ϊ25
	private int nowConnectNum=0;   //����������
	//private Conn conn=new Conn();
	private static Hashtable<Conn,Boolean> conn_pool =new Hashtable<Conn,Boolean>();   //true ��ʾ����
	
	public DBoperate_pool(){
		
	}
	public synchronized Conn getFreeConn(String str){
		//System.out.println("getFreeCon()"+conn_pool+","+str);
		while(true){
			Enumeration<Conn> e=conn_pool.keys();
			while(e.hasMoreElements()){
				Conn conn=e.nextElement();
				if(conn_pool.get(conn)){
					if(conn==null){
						conn_pool.remove(conn);
						conn=new Conn();
					}//end if
					conn_pool.put(conn,false);
					return conn;
				}//end if
			}//end while
			if(nowConnectNum<maxConnectNum){
				Conn conn =new Conn();
				nowConnectNum++;
				conn_pool.put(conn, false);
				return conn;
			}
			else {
				try {
					System.out.println("\nû�п��õ�Conn,wait()");
					wait();
					System.out.println("=====================������==========================");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}//end if
		}//end while
	}
	public synchronized void freeConn(Conn conn){
		conn_pool.put(conn, true);
		//System.out.println("�ͷ�һ��Conn,����");
		//System.out.println("freeConn()"+conn_pool);
		notify();
	}
	public synchronized void freeConn(Conn conn,String str){
		conn_pool.put(conn, true);
		//System.out.println("�ͷ�һ��Conn,����"+str);
		//System.out.println("freeConn()"+conn_pool);
		notifyAll();
	}
	public void close(){
		Enumeration<Conn> e=conn_pool.keys();
		while(e.hasMoreElements()){
			Conn conn=e.nextElement();
			conn.close();
		}
	}
}

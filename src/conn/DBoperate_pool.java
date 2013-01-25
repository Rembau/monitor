package conn;

import java.util.Enumeration;
import java.util.Hashtable;

public class DBoperate_pool {
	private int maxConnectNum=50;  //连接最大数量为25
	private int nowConnectNum=0;   //已有连接数
	//private Conn conn=new Conn();
	private static Hashtable<Conn,Boolean> conn_pool =new Hashtable<Conn,Boolean>();   //true 表示空闲
	
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
					System.out.println("\n没有可用的Conn,wait()");
					wait();
					System.out.println("=====================被唤醒==========================");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}//end if
		}//end while
	}
	public synchronized void freeConn(Conn conn){
		conn_pool.put(conn, true);
		//System.out.println("释放一个Conn,唤醒");
		//System.out.println("freeConn()"+conn_pool);
		notify();
	}
	public synchronized void freeConn(Conn conn,String str){
		conn_pool.put(conn, true);
		//System.out.println("释放一个Conn,唤醒"+str);
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

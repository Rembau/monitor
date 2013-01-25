package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 public class DBManager {
	 String ip="localhost";
	 int port=3306;
	 String dbName="test";
	 String user = "root";
	 String password ="123";
	 private Connection con;
	 private Statement stmt;
     public Connection getConnection(){  
        try {
        	Class.forName("com.mysql.jdbc.Driver");   //加载驱动
        }
        catch (ClassNotFoundException e)
        {
        	System.out.print("找不到类");
        }

        try { 
        	//尝试连接
        	System.out.println(this.ip+""+  this.port+""+   this.dbName+""+  this.user+""+ this.password);
        	con = DriverManager.getConnection("jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.dbName, this.user, this.password);         
        	stmt = con.createStatement();
        	System.out.println("连接成功");
        } catch (SQLException e){
        	System.out.println("连接不成功"+e.getMessage());
        }
        return con;
     }
     public void getData() throws Exception{
    	 String sql ="select * from test";
    	 try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
    	 
     }
     public void closeConnection(){
    	 //关闭连接
    	 try {
			this.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
     }
     public static void main(String args[]){
    	 DBManager db =new DBManager();
    	 db.getConnection();
    	 try {
    		 db.getData();
    	 } catch (Exception e) {
    		 e.printStackTrace();
    	 } finally{
    		 db.closeConnection();
    	 }
     } 
 }



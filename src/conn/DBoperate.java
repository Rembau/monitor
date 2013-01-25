package conn;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DBoperate {
	private Conn conn;
	static DBoperate_pool pool = new DBoperate_pool();
	static int count=0;
	public DBoperate(){
	}
	/*public void close(){
		conn.close();
	}*/
	public  boolean delete(String sql){
		conn=pool.getFreeConn("delete");
		while(conn==null){
			conn=pool.getFreeConn("delete");
			if(conn!=null){
				break;
			}
		}
		try {
			conn.getOldStmt().executeUpdate(sql);
			return true;
		} catch(MySQLIntegrityConstraintViolationException e1){
			return false;
		} catch (SQLException e) {
			conn.setConn();
			try {
				conn.getOldStmt().executeUpdate(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally{
			freeConn("delete");
		}
		return true;
	}
	public  boolean insert(String sql){
		conn=pool.getFreeConn("insert");
		while(conn==null){
			conn=pool.getFreeConn("insert");
			if(conn!=null){
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			conn.getOldStmt().executeUpdate(sql);
			//System.out.println(sql);
		} catch(MySQLIntegrityConstraintViolationException e1){
			return false;
		} catch (SQLException e) {
			conn.setConn();
			try {
				conn.getOldStmt().executeUpdate(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally{
			freeConn("insert");
		}
		return true;
	}
	public void update(String sql){
		conn=pool.getFreeConn("update");
		while(conn==null){
			conn=pool.getFreeConn("update");
			if(conn!=null){
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			conn.getOldStmt().executeUpdate(sql);
			//System.out.println(sql);
		} catch(MySQLIntegrityConstraintViolationException e1){
			System.out.println(sql+"error"+e1.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			conn.setConn();
			try {
				conn.getOldStmt().executeUpdate(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println(sql);
			}
		} finally{
			freeConn("update");
		}
	}
	public ResultSet select(String sql){
		conn=pool.getFreeConn("select");
		while(conn==null){
			conn=pool.getFreeConn("select");
			if(conn!=null){
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			return conn.getNewStmt().executeQuery(sql);
		} catch (SQLException e) {
			conn.setConn();
			try {
				conn.getOldStmt().executeQuery(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		} finally{
			freeConn("select");
		}
	}
	public void freeConn(String str){
		pool.freeConn(conn,str);
	}
	/*public void freeConn(){
		pool.freeConn(conn_);
	}*/
	public static void main(String[] args) {

	}
}

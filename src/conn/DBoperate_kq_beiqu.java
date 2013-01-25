package conn;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DBoperate_kq_beiqu {
	private static Conn conn;
	public DBoperate_kq_beiqu(){
	}
	static {
		conn = new Conn("sqldata_kq_beiqu.properties");
	}
	public static void close(){
		conn.close();
	}
	public static boolean delete(String sql){
		if(conn==null){
			conn= new Conn("sqldata_kq_beiqu.properties");
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
		}
		return true;
	}
	public static boolean insert(String sql){
		if(conn==null){
			conn=new Conn("sqldata_kq_beiqu.properties");
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
		}
		return true;
	}
	public static void update(String sql){
		if(conn==null){
			conn=new Conn("sqldata_kq_beiqu.properties");
		}
		try {
			conn.getOldStmt().executeUpdate(sql);
		} catch(MySQLIntegrityConstraintViolationException e1){
			
		} catch (SQLException e) {
			conn.setConn();
			try {
				conn.getOldStmt().executeUpdate(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	public static ResultSet select(String sql){
		if(conn==null){
			conn=new Conn("sqldata_kq_beiqu.properties");
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
		}
	}
	public static void main(String[] args) {

	}
}

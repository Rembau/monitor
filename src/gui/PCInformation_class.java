package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import conn.DBoperate_;
import conn.DBoperate_kq_201;
import conn.DBoperate_kq_203;
import conn.DBoperate_kq_301;
import conn.DBoperate_kq_beiqu;

public class PCInformation_class {
	JWindow window;
	JPanel panel_wait;
	int mouseRange=3;
	Thread showTh;
	public PCInformation_class(final int x,final int y){
		window=new JWindow();
		JPanel panel=new JPanel();
		JLabel label= new JLabel("教师机！");
		panel.add(label);
		window.add(panel);
		int x_=x,y_=y;
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		if(y+100>screen.height){
			y_=y-100;
		}
		if(x+300>screen.width){
			x_=x-300;
		}
		window.setFocusable(false);
		window.setBounds(x_+mouseRange, y_+mouseRange, 300, 100);
		window.setVisible(true);
		window.validate();
	}
	public PCInformation_class(final String ip,final int x,final int y){
		window=new JWindow();
		panel_wait = new JPanel();
		JLabel label = new JLabel("正在获取信息！");
		panel_wait.add(label);
		
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x_=x,y_=y;
		if(y+100>screen.height){
			y_=y-100;
		}
		if(x+300>screen.width){
			x_=x-300;
		}
		window.add(panel_wait);
		window.setFocusable(false);
		window.setBounds(x_+mouseRange, y_+mouseRange, 300, 100);
		window.setVisible(true);
		window.validate();
		showTh=new Thread(){
			public void run(){
				show(ip,x,y);
			}
		};
		showTh.start();
	}
	public void show(String ip,int x,int y){
		
		JPanel panel= new JPanel();
		panel.setLayout(new GridLayout(0,2));
		try{
			ip=ip.substring(0,ip.indexOf(" "));
		}catch(Exception e){
		}
		
		ResultSet rs=DBoperate_.select("select * from machine_roomPc where computer_ip ='"+ip+"'");
		String time=null;
		try {
			if(rs.next()){
				if(rs.getString("computer_isOnline").equals("y")){
					time=rs.getString("computer_onTime");
					JLabel label_1=new JLabel("上线时间:");
					JLabel label_1_=new JLabel(time);
					panel.add(label_1);
					panel.add(label_1_);
				}
				else {
					time=rs.getString("computer_downTime");
					JLabel label_1=new JLabel("下线时间:");
					JLabel label_1_=new JLabel(time);
					panel.add(label_1);
					panel.add(label_1_);
				}
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(ip);//
		try{
			if(ip.split("\\.")[2].equals("25")){
				rs = DBoperate_kq_201.select("select * from manager where v_hostIP= '"+ip+"'");
			}
			else if(ip.split("\\.")[2].equals("27")){
				rs = DBoperate_kq_203.select("select * from manager where v_hostIP= '"+ip+"'");
			}
			else if(ip.split("\\.")[2].equals("26") || ip.split("\\.")[2].equals("24")){
				rs = DBoperate_kq_301.select("select * from manager where v_hostIP= '"+ip+"'");
			}
			else {
				rs = DBoperate_kq_beiqu.select("select * from manager where v_hostIP= '"+ip+"'");
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
			return;
		}
		String code=null,name=null,class_=null,teacher=null,course=null;
		try {
			if(rs.next()){
				code=rs.getString("v_studentID");
				name=rs.getString("v_studentName");
				class_=rs.getString("v_class");
				teacher=rs.getString("v_teacherID");
				course=rs.getString("v_courseID");
				
				JLabel label;
				panel.add(label=new JLabel("学号:"));
				label.setOpaque(true);
				panel.add(label=new JLabel(code));
				label.setOpaque(true);
				panel.add(label=new JLabel("姓名:"));
				label.setOpaque(true);
				panel.add(label=new JLabel(name));
				label.setOpaque(true);
				panel.add(label=new JLabel("班级:"));
				label.setOpaque(true);
				panel.add(label=new JLabel(class_));
				label.setOpaque(true);
				panel.add(label=new JLabel("教师:"));
				label.setOpaque(true);
				panel.add(label=new JLabel(teacher));
				label.setOpaque(true);
				panel.add(label=new JLabel("课程:"));
				label.setOpaque(true);
				panel.add(label=new JLabel(course));
				label.setOpaque(true);
			}
			else {
				JLabel label;
				panel.add(label=new JLabel("无学生信息"));
				label.setOpaque(true);
				panel.add(label=new JLabel(""));
				label.setOpaque(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		panel.setBackground(Color.white);

		window.remove(panel_wait);
		window.add(panel);
		window.validate();
	}
	public void hidden(){
		try{
			if(showTh!=null){
				showTh.interrupt();
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		window.removeNotify();
	}
	public void move(int x,int y){
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		if(y+100>screen.height){
			y=y-100;
		}
		if(x+300>screen.width){
			x=x-300;
		}
		window.setLocation(x+mouseRange, y+mouseRange);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}

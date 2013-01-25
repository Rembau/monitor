package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import conn.DBoperate_;

public class PCInformation {
	JWindow window;
	JPanel show_wait;
	Thread showTh;
	int mouseRange=3;
	public PCInformation(final String ip,final int x,final int y){
		window=new JWindow();
		show_wait=new JPanel();
		JLabel label=new JLabel("正在获取信息！");
		show_wait.add(label);
		window.add(show_wait);
		
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x_=x,y_=y;
		if(y+100>screen.height){
			y_=y-100;
		}
		if(x+200>screen.width){
			x_=x-200;
		}
		window.setBounds(x_+mouseRange, y_+mouseRange, 200, 100);
		window.setVisible(true);
		window.validate();
		showTh=new Thread(){
			public void run(){
				show(ip,x,y);
			}
		};
		showTh.start();
		/*RoundRectangle2D.Float mask = new RoundRectangle2D.Float(0, 0, window
                .getWidth(), window.getHeight(), 0, 0);
		WindowUtils.setWindowMask(window, mask);
		WindowUtils.setWindowAlpha(window, 0.7f);*/
	}
	public void show(String ip,int x,int y){
		JPanel panel= new JPanel();
		panel.setLayout(new GridLayout(0,1));
		try{
			ip=ip.substring(0,ip.indexOf(" "));
		}catch(Exception e){
			
		}
		ResultSet rs=DBoperate_.select("select * from machine_computer where computer_ip ='"+ip+"'");
		String mac=null;
		String dateT=null;
		String monthT=null;
		try {
			if(rs.next()){
				mac=rs.getString("computer_mac");
				dateT=rs.getString("computer_dateOnlineTime");
				monthT=rs.getString("computer_monthOnlineTime");
				if(rs.getString("computer_isOnline").equals("y")){
					panel.add(new JLabel("在线",SwingConstants.CENTER));
				}
				else {
					panel.add(new JLabel("不在线",SwingConstants.CENTER));
				}
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JPanel panel_1_=new JPanel();
		JLabel label_1_1=new JLabel("IP地址:");
		label_1_1.setOpaque(true);
		JLabel label_1_1_=new JLabel(ip);
		panel_1_.add(label_1_1);
		panel_1_.add(label_1_1_);
		panel.add(panel_1_);
		
		JPanel panel_1=new JPanel();
		JLabel label_1=new JLabel("MAC地址:");
		label_1.setOpaque(true);
		JLabel label_1_=new JLabel(mac);
		panel_1.add(label_1);
		panel_1.add(label_1_);
		panel.add(panel_1);
		
		JPanel panel_2=new JPanel();
		JLabel label_2=new JLabel("今天在线时间:");
		int hour_1=Integer.parseInt(dateT)/60;
		int mi_1=Integer.parseInt(dateT)%60;
		JLabel label_2_=new JLabel(hour_1+"小时"+mi_1+"分钟");
		label_2.setOpaque(true);
		label_2_.setOpaque(true);
		panel_2.add(label_2);
		panel_2.add(label_2_);
		panel.add(panel_2);
		
		JPanel panel_3=new JPanel();
		JLabel label_3=new JLabel("本月在线时间:");
		int date=Integer.parseInt(monthT)/(24*60);
		int date_=Integer.parseInt(monthT)%(24*60);
		int hour_2=date_/60;
		int mi_2=date_%60;
		JLabel label_3_=new JLabel(date+"天"+hour_2+"小时"+mi_2+"分钟");
		label_3.setOpaque(true);
		label_3_.setOpaque(true);
		panel_3.add(label_3);
		panel_3.add(label_3_);
		panel.add(panel_3);
		
		panel.setBackground(Color.lightGray);
		window.remove(show_wait);
		window.setContentPane(panel);
		//window.validate();
	}
	public void hidden(){
		showTh.interrupt();
		window.removeNotify();
	}
	public void move(int x,int y){
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		if(y+100>screen.height){
			y=y-100;
		}
		if(x+200>screen.width){
			x=x-200;
		}
		window.setLocation(x+mouseRange, y+mouseRange);
	}
	public static void main(String[] args) {
	}
}

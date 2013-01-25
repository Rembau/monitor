package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import conn.DBoperate_;

public class ClassPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private String room="==All==";
	String ip="";
	public int scanTime=3;
	private PCInformation_class pcPanel;
	private JButton nowEventLabel;
	
	JPanel topPanel,showPanel;
	public ClassPanel(){
		new Timer().schedule(new Task(), 5*1000, 10*60*1000);
		topPanel = createTopPanel();
		showPanel = createShowPanel_();
		this.setLayout(new BorderLayout());
		this.add(topPanel,BorderLayout.NORTH);
		this.add(showPanel,BorderLayout.CENTER);
		this.setSize(700, 700);
	}
	class Task extends TimerTask{
		public void run() {
			System.out.println("ClassPanel.Task刷新界面！");
			reprintShowPanel(ClassPanel.this.room);
		}
	}
	/**
	 * 建立顶层面板
	 */
	public JPanel createTopPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JButton button = new JButton("教室管理");
		Action_button action = new Action_button();
		button.addActionListener(action);
		JLabel label = new JLabel("请选择教室:");
		JComboBox select=setSelecter();
		
		//JButton button_1 = new JButton("刷新");
		//button_1.addActionListener(action);
		JLabel label_=new JLabel("rad--notonline  blue--online  gray--none");
		
		JPanel panel_1 = new JPanel();
		panel_1.add(button);
		panel_1.add(label);
		panel_1.add(select);
		panel_1.add(label_);
		panel.add(panel_1,BorderLayout.WEST);
		return panel;
	}
	/**
	 * 建立下拉菜单
	 * @return 下拉菜单
	 */
	public JComboBox setSelecter(){
		JComboBox select = new JComboBox();
		select.addItem("==All==");
		String sql="select * from machine_room";
		ResultSet rs=DBoperate_.select(sql);
		try {
			while(rs.next()){
				select.addItem(rs.getString("room_name"));
				System.out.println(rs.getString("room_name"));
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		select.addActionListener(new Action_selectRoom(select));
		return select;
	}
	public JPanel createShowPanel_(){
		JPanel panel = new JPanel();
		ResultSet rs=DBoperate_.select("select count(*) from machine_room");
		int totle=0;
		try {
			if(rs.next()){
				totle=rs.getInt(1);
				System.out.println("totle的value"+totle);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally{
			try {
				rs.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(totle==0){
			return null;
		}
		else if(totle<=2){
			panel.setLayout(new GridLayout(2,1));
		}
		else if(totle<=4){
			panel.setLayout(new GridLayout(2,2));
		}
		else if(totle<=6){
			panel.setLayout(new GridLayout(3,2));
		} 
		else if(totle<=9){
			panel.setLayout(new GridLayout(3,3));
		}
		else if(totle<=12){
			panel.setLayout(new GridLayout(4,3));
		}
		rs=DBoperate_.select("select * from machine_room");
		try {
			MouseEvent_panel moueEvent_panel;
			while(rs.next()){
				JPanel panel_=createShowPanel(rs.getString("room_name"));
				panel_.setBorder(BorderFactory.createTitledBorder(rs.getString("room_name")));
				moueEvent_panel=new MouseEvent_panel(rs.getString("room_ip"));
				panel_.addMouseListener(moueEvent_panel);
				panel.add(panel_);
				//System.out.println(rs.getString(1));
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
		return panel;
	}
	@SuppressWarnings("unused")
	public JPanel createShowPanel(String room){
		JPanel panel = new JPanel();
		String sql= "select * from machine_room where room_name='"+room+"'";
		ResultSet rs = DBoperate_.select(sql);
		try {
			if(rs.next()){
				int year,month,day,hour,mi;
				Calendar ca = Calendar.getInstance();
				year=ca.get(Calendar.YEAR);
				month=ca.get(Calendar.MONTH)+1;
				day=ca.get(Calendar.DATE);
				hour=ca.get(Calendar.HOUR_OF_DAY);
				mi=ca.get(Calendar.MINUTE);
				
				String time = rs.getString("room_checkTime");
				//System.out.println(time);
				/*if(time==null || time.length()==0){
					JOptionPane.showMessageDialog(null, "该教室没有上课!");
					return new JPanel();
				}
				String time_[]=time.split("-");
				if(year==Integer.parseInt(time_[0]) && month==Integer.parseInt(time_[1]) && day==Integer.parseInt(time_[2])){
					if(hour==Integer.parseInt(time_[3]) && mi-Integer.parseInt(time_[4]) > scanTime){
						JOptionPane.showMessageDialog(null, "该教室没有上课!");
						return new JPanel();
					}
					else if(hour-Integer.parseInt(time_[3])>1){
						JOptionPane.showMessageDialog(null, "该教室没有上课!");
						return new JPanel();
					}
					else if(hour-Integer.parseInt(time_[3])==1 && 60-mi+Integer.parseInt(time_[4])> scanTime){
						JOptionPane.showMessageDialog(null, "该教室没有上课!");
						return new JPanel();
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "该教室没有上课!");
					return new JPanel();
				}*/
				String room_ip=rs.getString("room_ip");
				String room_ip_="";
				try{
					room_ip_=room_ip.substring(room_ip.lastIndexOf(".")+1);
					//System.out.println(room_ip_);
				} catch(Exception e){
					System.out.println(e.getMessage());
				}
				String startIp=rs.getString("room_startIp");
				String endIp=rs.getString("room_endIp");
				ip=rs.getString("room_ip");
				rs.getStatement().close();
				int start=Integer.parseInt(startIp.substring(startIp.lastIndexOf(".")+1));
				int end=Integer.parseInt(endIp.substring(endIp.lastIndexOf(".")+1));
				int count = end-start;
				int i_;
				for(i_=1;i_<15;i_++){
					if(count<=i_*i_){
						break;
					}
				}
				panel.setLayout(new GridLayout(0,i_));
				String frontIp=startIp.substring(0,startIp.lastIndexOf(".")+1);
				Hashtable<String,String> hrs=new Hashtable<String,String>();
				for(int i=start;i<=end;i++){
					ip=frontIp+""+i;
					rs=DBoperate_.select("select * from machine_computer where computer_ip='"+ip+"'");
					//System.out.println("select * from machine_computer where computer_ip='"+ip+"'");
					while(rs.next()){
						//System.out.println("===="+rs.getString(1).substring(rs.getString(1).lastIndexOf(".")+1)+" "+rs.getString(2));
						hrs.put(i+"", rs.getString("computer_isOnline"));
					}
					rs.getStatement().close();
				}
				//rs=DBoperate_.select("select * from machine_roomPC where computer_room='"+room+"'");
				
				MouseEvent_label mouseEvent = new MouseEvent_label();
				MouseEvent_label mouseEvent_ = new MouseEvent_label(true);
				for(int i=start;i<end;i++){
					JButton label=null;
					if(room_ip_.equals(i+"")){
						//System.out.println("value of i"+i);
						label=createButton(i+"","teacher");
						label.addMouseListener(mouseEvent_);
						label.addMouseMotionListener(mouseEvent_);
					}
					else if(hrs.containsKey(i+"")){
						label=createButton(i+"",hrs.get(i+""));
						label.addMouseListener(mouseEvent);
						label.addMouseMotionListener(mouseEvent);
					}
					else {
						label=createButton(i+"","no");
					}
					panel.add(label);
				}
			}
			else{
				panel.setBackground(Color.white);
				return panel;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return panel;
	}
	private JButton createButton(String str,String isOnline){
		//button.setContentAreaFilled(false);
		//button.setBackground(Color.blue);
		JButton button=null;
		if(isOnline.equals("y")){
			button= new JButton(str);
			button.setFont(new Font("Dialog",Font.PLAIN,9));
			button.setForeground(Color.blue);
			button.setMargin(new Insets(0,2,0,2));
			//button.setOpaque(true);
		} 
		else if(isOnline.equals("n")){
			button= new JButton(str);
			button.setFont(new Font("Dialog",Font.PLAIN,9));
			button.setForeground(Color.red);
			button.setMargin(new Insets(0,2,0,2));
			//button.setOpaque(false);
		}
		else if(isOnline.equals("teacher")){
			button= new JButton(str);
			button.setFont(new Font("Dialog",Font.PLAIN,9));
			button.setForeground(Color.magenta);
			button.setMargin(new Insets(0,2,0,2));
			//System.out.println(str+"is computer of teacher");
			
			//button.setOpaque(false);
		}
		else {
			button= new JButton();
		}
		return button;
	}
	/*private JLabel createLabel(String str,String isOnline){
		JLabel label=null;
		//System.out.println("==="+str+" "+isOnline);
		if(isOnline.equals("y")){
			label = new JLabel();
			Image image = Toolkit.getDefaultToolkit().createImage("images/y.JPG");
			image=image.getScaledInstance(50, 25, Image.SCALE_DEFAULT);
			ImageIcon icon=new ImageIcon(image,str);
			label.setIcon(icon);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setIconTextGap(2);
			label.setOpaque(true);
			label.setBackground(Color.white);
			label.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray),
					BorderFactory.createTitledBorder(str)));
		}
		else if(isOnline.equals("n")){
			label = new JLabel();//new ImageIcon("images/未命名_1.gif")
			Image image = Toolkit.getDefaultToolkit().createImage("images/n.JPG");
			image=image.getScaledInstance(50, 25, Image.SCALE_DEFAULT);
			ImageIcon icon=new ImageIcon(image,str);
			label.setIcon(icon);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setIconTextGap(2);
			label.setOpaque(true);
			label.setBackground(Color.white);
			label.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray),
					BorderFactory.createTitledBorder(str)));
		}
		else {
			label = new JLabel("");//new ImageIcon("images/未命名_1.gif")
			label.setOpaque(true);
			label.setBackground(Color.white);
			label.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray));
		}
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setFocusable(true);
		return label;
	}*/
	public void reprintTopPanel(){
		this.remove(topPanel);
		topPanel = createTopPanel();
		this.add(topPanel,BorderLayout.NORTH);
		this.validate();
	}
	public void reprintShowPanel(String room){
		this.remove(showPanel);
		try{
			if(room.equals("==All==")){
				showPanel=createShowPanel_();
			}
			else {
				showPanel=createShowPanel(room);
			}
			this.room=room;
			System.out.println("选择:"+room);
		}catch(Exception e1){
			showPanel=createShowPanel("");
			this.room="";
			e1.printStackTrace();
		}
		this.add(showPanel,BorderLayout.CENTER);
		this.validate();
	}
	/**
	 * 下拉菜单选择事件监听
	 * @author Administrator
	 *
	 */
	class Action_selectRoom implements ActionListener{
		JComboBox select;
		public Action_selectRoom(JComboBox select){
			this.select=select;
		}
		public void actionPerformed(ActionEvent e) {
			String str=select.getSelectedItem().toString();
			reprintShowPanel(str);
		}
	}
	/**
	 * button事件监听
	 * @author Administrator
	 *
	 */
	class Action_button implements ActionListener{
		Action_button(){
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("教室管理")){
				new ClassManagePanel(ClassPanel.this);
			}
			else if(e.getActionCommand().equals("刷新")){
				reprintShowPanel(ClassPanel.this.room);
			}
		}
	}
	class MouseEvent_panel implements MouseListener{
		String markIp;
		public MouseEvent_panel(String str){
			markIp=str;
		}
		public void mouseClicked(MouseEvent e) {
			
		}

		public void mouseEntered(MouseEvent e) {
			ip=markIp;
		}

		public void mouseExited(MouseEvent e) {
			
		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	class MouseEvent_label implements MouseListener,MouseMotionListener{
		boolean b=false;
		public MouseEvent_label(){
			
		}
		public MouseEvent_label(boolean b){
			this.b=b;
		}
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseMoved(MouseEvent e) {
			if(pcPanel!=null){
				pcPanel.move(e.getXOnScreen(), e.getYOnScreen());
			}
		}
		public void mouseEntered(MouseEvent e) {
			/*try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			if(nowEventLabel==(JButton)e.getSource()){
				return;
			}
			else if(nowEventLabel==null){
				nowEventLabel=(JButton)e.getSource();
			}
			else if(nowEventLabel!=(JButton)e.getSource()){
				nowEventLabel=(JButton)e.getSource();
			}
			/*((JLabel)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 1,1, Color.blue),
					BorderFactory.createTitledBorder(((ImageIcon)(((JLabel)e.getSource()).getIcon())).getDescription())));*/
			final String iPPart_=ip.substring(0,ip.lastIndexOf("."));
			final MouseEvent e_=e;
			//pcPanel=new PCInformation_class(iPPart_+"."+((ImageIcon)(((JLabel)e.getSource()).getIcon())).getDescription(),e.getXOnScreen(),e.getYOnScreen());
			if(!b){
				pcPanel=new PCInformation_class(iPPart_+"."+((JButton)e_.getSource()).getText(),e_.getXOnScreen(),e_.getYOnScreen());
			}
			else {
				pcPanel=new PCInformation_class(e_.getXOnScreen(),e_.getYOnScreen());
			}
		}

		public void mouseExited(MouseEvent e) {
			/*((JButton)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray),
					BorderFactory.createTitledBorder(((JButton)e.getSource()).getText())));*/
			if(pcPanel!=null){
				pcPanel.hidden();
			}
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseDragged(MouseEvent e) {
		}
		
	}
	public static void main(String[] args) {

	}
}

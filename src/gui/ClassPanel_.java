package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import conn.DBoperate_;

public class ClassPanel_ extends JPanel{
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private String room="";
	String ip="";
	public int scanTime=3;
	private PCInformation_class pcPanel;
	private JLabel nowEventLabel;
	private JDesktopPane desktopPane= new JDesktopPane();
	private LinkedList<JInternalFrame> internals= new LinkedList<JInternalFrame>();
	JPanel topPanel,showPanel;
	public ClassPanel_(){
		topPanel = createTopPanel();
		//Timer timer=new Timer();
		//Task t1=new Task();
		//timer.schedule(t1, 1*1000, 30*1000);
		
		this.setLayout(new BorderLayout());
		this.add(topPanel,BorderLayout.NORTH);
		this.add(desktopPane,BorderLayout.CENTER);
		this.setSize(700, 700);
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
		
		JButton button_1 = new JButton("连接");
		button_1.addActionListener(action);
		
		JPanel panel_1 = new JPanel();
		panel_1.add(button);
		panel_1.add(label);
		panel_1.add(select);
		panel_1.add(button_1);
		panel.add(panel_1,BorderLayout.WEST);
		return panel;
	}
	/**
	 * 建立下拉菜单
	 * @return 下拉菜单
	 */
	public JComboBox setSelecter(){
		JComboBox select = new JComboBox();
		select.addItem("==select==");
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
				for(int i=start;i<end;i++){
					JLabel label=null;
					if(hrs.containsKey(i+"")){
						label=createLabel(i+"",hrs.get(i+""));
						label.addMouseListener(mouseEvent);
						label.addMouseMotionListener(mouseEvent);
					}
					else {
						label=createLabel(i+"","no");
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
	private JLabel createLabel(String str,String isOnline){
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
	}
	public void reprintShowPanel(String room){
			this.remove(showPanel);
			try{
				showPanel=createShowPanel(room);
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
	public void reprintTopPanel(){
		this.remove(topPanel);
		topPanel = createTopPanel();
		this.add(topPanel,BorderLayout.NORTH);
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
			//reprintShowPanel(str);
			JPanel panel=createShowPanel(str);
			if(str.equals("==select==")){
				return;
			}
			JInternalFrame internalFrame = 
	        	new JInternalFrame(str, true, true, true, true); //子窗口
	                             // 创建具有指定标题、可调整、可关闭、可最大化和可图标化的 JInternalFrame
	        internalFrame.setLocation( 20,20);    //将组件移到新位置
	        internalFrame.setSize(200,200);        //大小
	        internalFrame.setVisible(true);         //组件可见或不可见
	        
	        Container icontentPane = internalFrame.getContentPane();  //获取容器                   
	        icontentPane.add(panel,"Center");
			internals.add(internalFrame);
			desktopPane.add(internalFrame);
			try {
				internalFrame.setSelected(true);
		    } catch (java.beans.PropertyVetoException ex) {
		        System.out.println("Exception while selecting");
		    }
		}
	}
/*	class Task extends TimerTask{
		public void run() {
			System.out.println("刷新");
			Iterator<JInternalFrame> i=internals.iterator();
			while(i.hasNext()){
				try{
					JInternalFrame jif=i.next();
					if(jif==null){
						continue;
					}
					JPanel panel=createShowPanel(jif.getTitle());
					Container icontentPane = jif.getContentPane();  //获取容器    
					icontentPane.remove(internals.get(jif));
					JPanel panel_=internals.get(jif);
					panel_=null;
					icontentPane.add(panel,"Center");
					jif.validate();
				} catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}	
	}*/
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
				//new ClassManagePanel(ClassPanel_.this);
			}
			/*else if(e.getActionCommand().equals("刷新")){
				reprintShowPanel(ClassPanel_.this.room);
			}*/
		}
	}
	class MouseEvent_label implements MouseListener,MouseMotionListener{

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseMoved(MouseEvent e) {
			pcPanel.move(e.getXOnScreen(), e.getYOnScreen());
		}
		public void mouseEntered(MouseEvent e) {
			/*try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			if(nowEventLabel==(JLabel)e.getSource()){
				return;
			}
			else if(nowEventLabel==null){
				nowEventLabel=(JLabel)e.getSource();
			}
			else if(nowEventLabel!=(JLabel)e.getSource()){
				nowEventLabel=(JLabel)e.getSource();
			}
			((JLabel)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 1,1, Color.blue),
					BorderFactory.createTitledBorder(((ImageIcon)(((JLabel)e.getSource()).getIcon())).getDescription())));
			String iPPart_=ip.substring(0,ip.lastIndexOf("."));
			pcPanel=new PCInformation_class(iPPart_+"."+((ImageIcon)(((JLabel)e.getSource()).getIcon())).getDescription(),e.getXOnScreen(),e.getYOnScreen());
		}

		public void mouseExited(MouseEvent e) {
			((JLabel)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray),
					BorderFactory.createTitledBorder(((ImageIcon)(((JLabel)e.getSource()).getIcon())).getDescription())));
			pcPanel.hidden();
			nowEventLabel=null;
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
		JFrame f=new JFrame();
		f.getContentPane().add(new ClassPanel_());
		f.setVisible(true);
		
	}
}

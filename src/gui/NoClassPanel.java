package gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import conn.DBoperate_;

import snmp.ScanIPPart;


public class NoClassPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private ScanIPPart scan;
	private JPanel showPC,select;
	public static String iPPart="";
	JLabel label_1=new JLabel("休息中");
	JButton button = new JButton("立即刷新");
	static JLabel showIPNum= new JLabel();
	PCInformation pcPanel;
	JLabel nowEventLabel;
	Image image_yes,image_no;
	ImageIcon icon_y,icon_n;
	/**
	 * 表示当前的显示面板是样式一或者是样式二
	 */
	private int typeInt=0;
	
	public NoClassPanel(){
		image_no = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/n.jpg"));
		image_no=image_no.getScaledInstance(50, 25, Image.SCALE_DEFAULT);
		icon_n=new ImageIcon(image_no,"n");
		System.out.println("image_no ok");
		
		image_yes = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/y.JPG"));
		image_yes=image_yes.getScaledInstance(50, 25, Image.SCALE_DEFAULT);
		icon_y=new ImageIcon(image_yes,"y");
		System.out.println("image_yes ok");
		
		scan=new ScanIPPart();
		showPC = createShowPanel("");
		select = createSelectPanel();
		this.setLayout(new BorderLayout());
		this.add(select,BorderLayout.NORTH);
		this.add(showPC,BorderLayout.CENTER);
		this.setBackground(Color.WHITE);
		this.setSize(700, 700);
	}
	public JLabel getLabel(){
		return this.label_1;
	}
	public JButton getB(){
		return button;
	}
	public static void setShowIPNumTxt(String str){
		if(showIPNum==null){
			showIPNum = new JLabel();
		}
		showIPNum.setText(str);
	}
	/**
	 * @return 顶部面板
	 */
	public JPanel createSelectPanel(){
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		JPanel panel_2 = new JPanel();
		JLabel label = new JLabel("请选择ip段:");
		
		JComboBox select = setSelecter();
		JLabel label_ =new JLabel("每5分钟刷新一次,");
		JLabel label_2 =new JLabel("点击");
		
		Action_button action_=new Action_button(button);
		button.addActionListener(action_);
		
		JButton button_1 = new JButton("type one");
		JButton button_2 = new JButton("type two");
		button_1.addActionListener(action_);
		button_2.addActionListener(action_);
		
		panel.setLayout(new BorderLayout());
		panel_1.add(label);
		panel_1.add(select);
		panel_1.add(label_);
		panel_1.add(label_1);
		panel_1.add(label_2);
		panel_1.add(button);
		panel_1.add(showIPNum);
		
		panel_2.add(button_1);
		panel_2.add(button_2);
		panel.add(panel_1,BorderLayout.WEST);
		panel.add(panel_2,BorderLayout.EAST);
		
		return panel;
	}
	/**
	 * 建立下拉菜单
	 * @return 下拉菜单
	 */
	public JComboBox setSelecter(){
		JComboBox select = new JComboBox();
		select.addItem("==select==");
		String sql="select * from machine_router";
		ResultSet rs=DBoperate_.select(sql);
		try {
			while(rs.next()){
				select.addItem(rs.getString("router_ip"));
				//System.out.println(rs.getString("router_ip"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				rs.getStatement().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		select.addActionListener(new Action_selectIPPart(select));
		return select;
	}
	/**
	 * 建立显示面板
	 * @param ipPart :路由IP
	 * @return : 样式面板一
	 */
	public JPanel createShowPanel(String ipPart){
		try{
			ipPart=ipPart.substring(0, ipPart.lastIndexOf("."));
		}catch(Exception e){
			ipPart="111";
		}
		Hashtable<String,String> list = getPCStateList(ipPart,"computer_isOnline");
		/*JPanel panel_=new JPanel();
		panel_.setLayout(new BorderLayout());
		JPanel panel_1=new JPanel();
		panel_.add(panel_1,BorderLayout.WEST);
		panel_1.setLayout(new GridLayout(16,0));
		for(int i=0;i<16;i++){
			JLabel label=new JLabel((i*16+1)+"-"+(i+1)*16);
			label.setOpaque(true);
			label.setBackground(Color.white);
			label.setBorder(BorderFactory.createLineBorder(Color.blue,1));
			panel_1.add(label);
		}*/
		
		JPanel panel=new JPanel();
		//panel_.add(panel,BorderLayout.CENTER);
		
		panel.setLayout(new GridLayout(0,16));
		System.out.println("panel.width"+panel.getWidth());
		String ip;
		MouseEvent_label mevent=new MouseEvent_label();
		for(int i=1;i<17;i++){
			for(int j=1;j<17;j++){
				ip=String.valueOf((i-1)*16+j);
				if(list.containsKey(ipPart+"."+ip)){
					JLabel label=createLabel(ip,list.get(ipPart+"."+ip));
					label.addMouseListener(mevent);
					label.addMouseMotionListener(mevent);
					panel.add(label);
					//System.out.println(label.getBounds().getWidth());
				}
				else{
					panel.add(createLabel(ip,"no"));
				}
			}
		}
		return panel;
	}
	/**
	 * 建立显示面板
	 * @param ipPart : 路由IP
	 * @return : 样式面板二
	 */
	JScrollPane spanel_2;
	public JPanel createShowPanel_(String ipPart){
		try{
			ipPart=ipPart.substring(0, ipPart.lastIndexOf("."));
		}catch(Exception e){
			ipPart="111";
		}
		
		JPanel panel=new JPanel();

		JScrollPane spanel_1 =new JScrollPane();
		
		spanel_2 =new JScrollPane();
		
		panel.setLayout(new GridLayout(0,2));
		panel.add(spanel_1);
		panel.add(spanel_2);
		panel.setSize(700, 700);
		String ip;
		
		DefaultTableModel model_1=null;
		JTable table_1=null;
		String result_1[][];
		String names_1[] = {"序号","IP","上线时间","今天在线时间","本月在线时间"};
		int n_1=0;
		
		DefaultTableModel model_2=null;
		JTable table_2=null;
		String result_2[][];
		String names_2[] = {"序号","IP","下线时间","今天在线时间","本月在线时间"};
		int n_2=0;
		try {
			String sql="select * from machine_computer where computer_ip like '"+ipPart+".%' and computer_isOnline='y'";
			ResultSet rs= DBoperate_.select(sql);
			if(rs.last()){
				int row=rs.getRow();
				spanel_1.setBorder(BorderFactory.createTitledBorder("当前在线IP  共"+row+"台"));
				result_1 = new String[row][5];
				rs.beforeFirst();
				while(rs.next()){
					ip=rs.getString("computer_ip");
					//System.out.println(n_1+":"+ip);
					result_1[n_1][0]=n_1+"";
					result_1[n_1][1]=ip;
					result_1[n_1][2]=rs.getString("computer_onTime");
					int hour_1=Integer.parseInt(rs.getString("computer_dateOnlineTime"))/60;
					int mi_1=Integer.parseInt(rs.getString("computer_dateOnlineTime"))%60;
					result_1[n_1][3]=hour_1+"小时"+mi_1+"分钟";
					int date=Integer.parseInt(rs.getString("computer_monthOnlineTime"))/(24*60);
					int date_=Integer.parseInt(rs.getString("computer_monthOnlineTime"))%(24*60);
					int hour_2=date_/60;
					//int mi_2=date_%60;
					result_1[n_1][4]=date+"天"+hour_2+"小时";
					n_1++;
				}
				model_1 = new DefaultTableModel(result_1,names_1);
				table_1= new JTable(model_1);
				TableColumn tc_1=table_1.getColumn("序号");
				tc_1.setPreferredWidth(11);
				
				TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table_1.getModel());
				rowSorter.setComparator(0, new Comparator<String>() {
		            public int compare(String str1, String str2) {
		            	int int1=Integer.parseInt(str1);
		            	int int2=Integer.parseInt(str2);
		                return int1-int2;
		            }
		        });
				rowSorter.setComparator(1, new Comparator<String>() {
		            public int compare(String str1, String str2) {
		            	str1=str1.substring(str1.lastIndexOf(".")+1);
		            	str2=str2.substring(str2.lastIndexOf(".")+1);
		            	int int1=Integer.parseInt(str1);
		            	int int2=Integer.parseInt(str2);
		                return int1-int2;
		            }
		        });
				rowSorter.setComparator(2, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						long int1=getInt_(str1);
						long int2=getInt_(str2);
		                return (int)(int1-int2);
		            }
		            public long getInt_(String str1){
		            	String time_1[]=str1.split(" ");
						String time_2[]=time_1[0].split("-");
						String time_3[]=time_1[1].split(":");
						time_2[1]=transform(time_2[1]);
						time_2[2]=transform(time_2[2]);
						time_3[0]=transform(time_3[0]);
						time_3[1]=transform(time_3[1]);
						time_3[2]=transform(time_3[2]);
						String time_1_=time_2[0]+""+time_2[1]+""+time_2[2]+""+time_3[0]+""+time_3[1]+""+time_3[2];
						long int1=Long.valueOf(time_1_);
						return int1;
		            }
		            public String  transform(String str){
		            	if(str.length()==1){
		            		str="0"+str;
		            	}
		            	return str;
		            }
		        });
				rowSorter.setComparator(3, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						int int1=getInt_(str1);
						int int2=getInt_(str2);
		                return int1-int2;
		            }
		            public int getInt_(String str1){
		            	String s[]=str1.split("小时");
		            	String s1[]=s[1].split("分钟");
		            	if(s1[0].length()==1){
		            		s1[0]="0"+s1[0];
		            	}
		            	String time=s[0]+s1[0];
						int int1=Integer.parseInt(time);
						return int1;
		            }
		        });
				rowSorter.setComparator(4, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						int int1=getInt_(str1);
						int int2=getInt_(str2);
		                return int1-int2;
		            }
		            public int getInt_(String str1){
		            	String s[]=str1.split("天");
		            	String s1[]=s[1].split("小时");
		            	if(s1[0].length()==1){
		            		s1[0]="0"+s1[0];
		            	}
		            	String time=s[0]+s1[0];
						int int1=Integer.parseInt(time);
						return int1;
		            }
		        });
				table_1.setRowSorter(rowSorter);
				
				rs.getStatement().close();
			}
			else {
				model_1 = new DefaultTableModel();
			}
			sql="select * from machine_computer where computer_ip like '"+ipPart+".%' and computer_isOnline='n'";
			rs= DBoperate_.select(sql);
			if(rs.last()){
				int row=rs.getRow();
				spanel_2.setBorder(BorderFactory.createTitledBorder("不在线IP  共"+row+"台"));
				result_2 = new String[row][5];
				rs.beforeFirst();
				while(rs.next()){
					//System.out.println(n_2);
					ip=rs.getString("computer_ip");
					result_2[n_2][0]=n_2+"";
					result_2[n_2][1]=ip;
					result_2[n_2][2]=rs.getString("computer_downTime");
					int hour_1=Integer.parseInt(rs.getString("computer_dateOnlineTime"))/60;
					int mi_1=Integer.parseInt(rs.getString("computer_dateOnlineTime"))%60;
					result_2[n_2][3]=hour_1+"小时"+mi_1+"分钟";
					int date=Integer.parseInt(rs.getString("computer_monthOnlineTime"))/(24*60);
					int date_=Integer.parseInt(rs.getString("computer_monthOnlineTime"))%(24*60);
					int hour_2=date_/60;
					//int mi_2=date_%60;
					result_2[n_2][4]=date+"天"+hour_2+"小时";
					n_2++;
				}
				model_2 = new DefaultTableModel(result_2,names_2);
				table_2= new JTable(model_2);
				TableColumn tc_2=table_2.getColumn("序号");
				tc_2.setPreferredWidth(11);
				
				TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(table_2.getModel());
				rowSorter.setComparator(0, new Comparator<String>() {
		            public int compare(String str1, String str2) {
		            	int int1=Integer.parseInt(str1);
		            	int int2=Integer.parseInt(str2);
		                return int1-int2;
		            }
		        });
				rowSorter.setComparator(1, new Comparator<String>() {
		            public int compare(String str1, String str2) {
		            	str1=str1.substring(str1.lastIndexOf(".")+1);
		            	str2=str2.substring(str2.lastIndexOf(".")+1);
		            	int int1=Integer.parseInt(str1);
		            	int int2=Integer.parseInt(str2);
		                return int1-int2;
		            }

		        });
				rowSorter.setComparator(2, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						long int1=getInt_(str1);
						long int2=getInt_(str2);
		                return (int)(int1-int2);
		            }
		            public long getInt_(String str1){
		            	if(str1==null || str1.trim().length()==0){
		            		return 0;
		            	}
		            	String time_1[]=str1.split(" ");
						String time_2[]=time_1[0].split("-");
						String time_3[]=time_1[1].split(":");
						time_2[1]=transform(time_2[1]);
						time_2[2]=transform(time_2[2]);
						time_3[0]=transform(time_3[0]);
						time_3[1]=transform(time_3[1]);
						time_3[2]=transform(time_3[2]);
						String time_1_=time_2[0]+""+time_2[1]+""+time_2[2]+""+time_3[0]+""+time_3[1]+""+time_3[2];
						long int1=Long.valueOf(time_1_);
						return int1;
		            }
		            public String  transform(String str){
		            	if(str.length()==1){
		            		str="0"+str;
		            	}
		            	return str;
		            }
		        });
				rowSorter.setComparator(3, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						int int1=getInt_(str1);
						int int2=getInt_(str2);
		                return int1-int2;
		            }
		            public int getInt_(String str1){
		            	String s[]=str1.split("小时");
		            	String s1[]=s[1].split("分钟");
		            	if(s1[0].length()==1){
		            		s1[0]="0"+s1[0];
		            	}
		            	String time=s[0]+s1[0];
						int int1=Integer.parseInt(time);
						return int1;
		            }
		        });
				rowSorter.setComparator(4, new Comparator<String>() {
		            public int compare(String str1, String str2) {
						int int1=getInt_(str1);
						int int2=getInt_(str2);
		                return int1-int2;
		            }
		            public int getInt_(String str1){
		            	String s[]=str1.split("天");
		            	String s1[]=s[1].split("小时");
		            	if(s1[0].length()==1){
		            		s1[0]="0"+s1[0];
		            	}
		            	String time=s[0]+s1[0];
						int int1=Integer.parseInt(time);
						return int1;
		            }
		        });
				table_2.setRowSorter(rowSorter);
				
				rs.getStatement().close();
			}
			else {
				model_2 = new DefaultTableModel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		spanel_1.getViewport().add(table_1);
		
		spanel_2.getViewport().add(table_2);
		//设置滚动条的位置
		//System.out.println(spanel_2.getViewport().getViewSize().height);
		//spanel_2.getVerticalScrollBar().setMaximum(spanel_2.getViewport().getViewSize().height);
		//spanel_2.getVerticalScrollBar().setValue(spanel_2.getViewport().getViewSize().height);
		
		return panel;
	}
	/**
	 * 建立单个显示PC信息的label
	 * @param str : IP,最后一段
	 * @param isOnline : 是否在线
	 * @return : label
	 */
	private JLabel createLabel(String str,String isOnline){
		JLabel label=null;
		//System.out.println(str+" "+isOnline);
		try{
		if(isOnline.equals("y")){
			label = new JLabel();
			//Image image = Toolkit.getDefaultToolkit().createImage("images/y.JPG");
			
			label.setIcon(icon_y);
			label.setDisplayedMnemonic(Integer.valueOf(str));
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
			//Image image = Toolkit.getDefaultToolkit().createImage("images/n.JPG");
			
			label.setIcon(icon_n);
			label.setDisplayedMnemonic(Integer.valueOf(str));
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
		} catch(Exception e){
			e.printStackTrace();
		}
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setFocusable(true);
		return label;
	}
	private Hashtable<String,String> getPCStateList(String ipPart,String type){
		Hashtable<String,String> list=new Hashtable<String,String>();
		String sql="select * from machine_computer where computer_ip like '"+ipPart+".%'";
		ResultSet rs=DBoperate_.select(sql);
		try {
			while(rs.next()){
				list.put(rs.getString("computer_ip"), rs.getString(type));
				//System.out.println(rs.getString("computer_ip")+" "+rs.getString("computer_isOnline"));
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 重绘显示面板
	 * @param str : 路由IP
	 */
	public void reprintPanel(String str){
		if(typeInt==0){
			NoClassPanel.this.remove(showPC);
			try{
				showPC=createShowPanel(str);
				NoClassPanel.iPPart=str;
				System.out.println("选择:"+str);
			}catch(Exception e1){
				showPC=createShowPanel("");
				NoClassPanel.iPPart="";
			}
			NoClassPanel.this.add(showPC,BorderLayout.CENTER);
			NoClassPanel.this.validate();
		}
		else {
			NoClassPanel.this.remove(showPC);
			try{
				showPC=createShowPanel_(str);
				NoClassPanel.iPPart=str;

				System.out.println("选择:"+str);
			}catch(Exception e1){
				showPC=createShowPanel_("");
				NoClassPanel.iPPart="";
			}
			NoClassPanel.this.add(showPC,BorderLayout.CENTER);
			NoClassPanel.this.validate();
		}
	}
	/**
	 * 下拉菜单选择事件监听
	 * @author Administrator
	 *
	 */
	class Action_selectIPPart implements ActionListener{
		JComboBox select;
		public Action_selectIPPart(JComboBox select){
			this.select=select;
		}
		public void actionPerformed(ActionEvent e) {
			String str=select.getSelectedItem().toString();
			reprintPanel(str);
			showIPNum.setText("接受的IP数据报数量:");
		}
		
	}
	/**
	 * button事件监听
	 * @author Administrator
	 *
	 */
	class Action_button implements ActionListener{
		JButton button;
		Action_button(JButton button){
			this.button=button;
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("立即刷新")){
				button.setEnabled(false);
				scan.handle(iPPart);
				reprintPanel(iPPart);
				JOptionPane.showMessageDialog(null, "刷新成功!");
				button.setEnabled(true);
			}
			else if(e.getActionCommand().equals("type one")){
				typeInt = 0;
				reprintPanel(iPPart);
			}
			else if(e.getActionCommand().equals("type two")){
				typeInt = 1;
				reprintPanel(iPPart);
			}
		}
	}
	class MouseEvent_label implements MouseMotionListener,MouseListener{

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseMoved(MouseEvent e) {
			//System.out.println(e.getXOnScreen()+","+e.getYOnScreen());
			/*try {
				Thread.sleep(30);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			pcPanel.move(e.getXOnScreen(), e.getYOnScreen());
		}
		public void mouseEntered(MouseEvent e) {
			if(nowEventLabel==(JLabel)e.getSource()){
				return;
			}
			else if(nowEventLabel==null){
				nowEventLabel=(JLabel)e.getSource();
			}
			else if(nowEventLabel!=(JLabel)e.getSource()){
				nowEventLabel=(JLabel)e.getSource();
			}
			String str = ((JLabel)e.getSource()).getDisplayedMnemonic()+"";
			((JLabel)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 1, 1, Color.blue),
					BorderFactory.createTitledBorder(str)));
			String iPPart_=iPPart.substring(0,iPPart.lastIndexOf("."));
			pcPanel=new PCInformation(iPPart_+"."+str,e.getXOnScreen(),e.getYOnScreen());
		}

		public void mouseExited(MouseEvent e) {
			String str = ((JLabel)e.getSource()).getDisplayedMnemonic()+"";
			((JLabel)e.getSource()).setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray),
							BorderFactory.createTitledBorder(str)));
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
			// TODO Auto-generated method stub
			
		}
		
	}
	public static void main(String[] args) {
	}
}

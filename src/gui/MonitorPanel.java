 package gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import snmp.Task;

import conn.DBoperate_kq_201;
import conn.DBoperate_kq_203;
import conn.DBoperate_kq_301;
import conn.DBoperate_pool;
import conn.DBoperate_kq_beiqu;

import java.util.Timer;
/**
 * 
 * 监控面板
 *
 */
public class MonitorPanel extends JFrame{
	private static final long serialVersionUID = 1L;
	DBoperate_pool pool=new DBoperate_pool();
	public static JPanel p1;
	public static JPanel p2;
	Timer timer;
	JMenuItem startMonitor;
	JMenuItem endMonitor;
	JLabel label;
	public static int allScanPcTime=10*60;
	public static int scanRouterIndexTime=24*60*60;
	JTabbedPane tabbedPane=new JTabbedPane(JTabbedPane.TOP);
	public MonitorPanel(){
		setTitle("监控--现教中心软件研发中心");
     	JPanel p=new JPanel();
	    p.setLayout(new BorderLayout());
	
		Action_button ae = new Action_button();
		JMenuBar bar =new JMenuBar();
		JMenu operate = new JMenu("操作");
		startMonitor=new JMenuItem("开始监控");
		startMonitor.addActionListener(ae);
		//startMonitor.setEnabled(false);
		endMonitor=new JMenuItem("结束监控");
		endMonitor.addActionListener(ae);
		//endMonitor.setEnabled(false);
		operate.add(startMonitor);
		operate.add(endMonitor);
		JMenu help = new JMenu("帮助");
		JMenuItem about = new JMenuItem("关于");
		about.addActionListener(ae);
		help.add(about);
		
		bar.add(operate);
		bar.add(help);
		this.setJMenuBar(bar);
		
		p.add(tabbedPane,BorderLayout.CENTER);
		this.add(p);
		Toolkit kit =Toolkit.getDefaultToolkit();
		Image image=kit.createImage(getClass().getResource("/images/bz.png"));
		image = image.getScaledInstance(20, 20, Image.SCALE_DEFAULT);
		this.setIconImage(image);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setBounds(140,160,900,730);
		//this.setResizable(false);//设置最大化按钮不可用
		
		this.setVisible(true);
		this.validate();
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try{
					new Thread(){
						public void run(){
							pool.close();
						}
					}.start();
				} catch(Exception e1){
					System.out.println(e1.getMessage());
				}
				try{
					new Thread(){
						public void run(){
							DBoperate_kq_beiqu.close();
						}
					}.start();
				} catch(Exception e1){
					System.out.println(e1.getMessage());
				}
				try{
					new Thread(){
						public void run(){
							DBoperate_kq_201.close();
						}
					}.start();
				} catch(Exception e1){
					System.out.println(e1.getMessage());
				}
				try{
					new Thread(){
						public void run(){
							DBoperate_kq_203.close();
						}
					}.start();
				} catch(Exception e1){
					System.out.println(e1.getMessage());
				}
				try{
					new Thread(){
						public void run(){
							DBoperate_kq_301.close();
						}
					}.start();
				} catch(Exception e1){
					System.out.println(e1.getMessage());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		new Thread(){
			public void run(){
				refresh1();
			}
		}.start();
		new Thread(){
			public void run(){
				refresh2();
			}
		}.start();
	}
	public void refresh1(){
		p1 = new NoClassPanel();
		tabbedPane.add(p1,"全天监控");
		tabbedPane.validate();
	}
	public void refresh2(){
		p2 = new ClassPanel();
		tabbedPane.add(p2,"上课监控",0);
		tabbedPane.validate();
	}
	public void timerHandle(){
		timer=new Timer();
		Task t1=new Task("allScanPc");
		t1.setLabel(((NoClassPanel)p1).getLabel());
		t1.setButton(((NoClassPanel)p1).getB());
		Task t2=new Task("scanRouterIndex");
		//Task t3=new Task("showIPNum");
		//new Timer().schedule(t3, 0, 5*1000);
		timer.schedule(t1, 10*1000, allScanPcTime*1000);
		timer.schedule(t2, 0, scanRouterIndexTime*1000);
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new MonitorPanel();
	}
	class Action_button implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("开始监控")){
				timerHandle();
				((JMenuItem)e.getSource()).setEnabled(false);
				endMonitor.setEnabled(true);
			}
			else if(e.getActionCommand().equals("结束监控")){
				timer.cancel();
				((JMenuItem)e.getSource()).setEnabled(false);
				startMonitor.setEnabled(true);
			}
			else if(e.getActionCommand().equals("关于")){
				JOptionPane.showMessageDialog(null, "现教中心软件研发中心\n蓝豹小组\n论坛:http://218.22.221.166/index.jsp");
			}
		}	
	}
}

package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import conn.DBoperate_;

/**
 * 教室管理面板
 */
public class ClassManagePanel extends JFrame{
	private static final long serialVersionUID = 1L;
	DefaultTableModel model;
	JTable table;
	int markAddRow=0;
	boolean markAdd=false;
	JScrollPane spanel;
	MouseEvent_table  mouseEvent;
	String roomField[]={"room_name","room_startIp","room_endIp","room_machineNum","room_ip","room_sqlIp"};
	public ClassManagePanel(final ClassPanel cpanel){
		this.setTitle("教室管理");
		spanel = new JScrollPane();
		spanel.setBorder(BorderFactory.createTitledBorder("所有教室"));
		mouseEvent= new MouseEvent_table();
		table = createShowTable();
		table.addMouseListener(mouseEvent);
		spanel.addMouseListener(mouseEvent);
		spanel.getViewport().add(table);
		
		JPanel bottonPanel = createBottonPanel();
		
		Container content=this.getContentPane();
		content.setLayout(new BorderLayout());
		content.add(spanel,BorderLayout.CENTER);
		content.add(bottonPanel,BorderLayout.SOUTH);
		
		this.setBounds(300,320,500,400);
		this.setResizable(false);//设置最大化按钮不可用
		this.setVisible(true);
		this.validate();
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				cpanel.reprintTopPanel();
			}
		});
	}
	public JPanel createBottonPanel(){
		JPanel panel = new JPanel();
		Action_button action = new Action_button();
		JButton button_delete = new JButton("删除该教室");
		button_delete.addActionListener(action);
		JButton button_add = new JButton("添加教室");
		button_add.addActionListener(action);
		JButton button_refresh = new JButton("刷新");
		button_refresh.addActionListener(action);
		
		panel.add(button_delete);
		panel.add(button_add);
		panel.add(button_refresh);
		return panel;
	}
	public JTable createShowTable(){
		JTable table=null;
		ResultSet rs = DBoperate_.select("select * from machine_room");
		int row;
		String result[][];
		String names[] = {"教室","起始IP","结束IP","机器数量","教师机IP","数据库IP"};
		try {
			if(rs.last()){
				row=rs.getRow();
				result = new String[row][6];
				rs.beforeFirst();
				int n=0;
				while(rs.next()){
					result[n][0]=rs.getString(2);
					result[n][1]=rs.getString(5);
					result[n][2]=rs.getString(6);
					result[n][3]=rs.getString(3);
					result[n][4]=rs.getString(7);
					result[n][5]=rs.getString("room_sqlIp");
					n++;
				}
				model = new DefaultTableModel(result,names);
			}
			else {
				model = new DefaultTableModel();
			}
			rs.getStatement().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		table = new JTable(model);
		markAddRow=table.getRowCount();
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return table;
	}
	class Action_button implements ActionListener{
		String rowData[] = {"","","","","",""};
		Action_button(){
		}
		public void actionPerformed(ActionEvent e) {
			int selectedRowNum = table.getSelectedRow();
			if(e.getActionCommand().equals("添加教室")){
				if(!markAdd){
					model.addRow(rowData);	
					markAddRow=table.getRowCount()-1;
					markAdd = true;
				}
			}
			else if(e.getActionCommand().equals("删除该教室")){
				if(selectedRowNum!=-1){
					if(JOptionPane.showConfirmDialog(null, "确定删除吗?", "删除教室", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
						String sql="delete  from machine_room where room_name='"+table.getValueAt(selectedRowNum, 0)+"'";
						DBoperate_.delete(sql);
						if(selectedRowNum==markAddRow){
							markAdd = false;
						}
						model.removeRow(selectedRowNum);
					}

				}
			}
			else if(e.getActionCommand().equals("刷新")){
				spanel.getViewport().remove(table);
				table=createShowTable();
				table.addMouseListener(mouseEvent);
				spanel.getViewport().add(table);
				spanel.validate();
				markAdd = false;
			}
		}
	}
	class MouseEvent_table implements MouseListener{
		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {
			
		}

		public void mouseExited(MouseEvent e) {
			
		}

		public void mousePressed(MouseEvent e) {
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();
			//System.out.println(table.isEditing());
			if(e.getSource() instanceof JScrollPane){
				if(table.isEditing()){
					String value=((JTextField)(table.getEditorComponent())).getText();
					String roomName=table.getValueAt(row, 0).toString();
					String room_startIp=table.getValueAt(row, 1).toString();
					String room_endIp=table.getValueAt(row, 2).toString();
					String room_machineNum=table.getValueAt(row, 3).toString();
					String room_ip=table.getValueAt(row, 4).toString();
					String room_sqlIp=table.getValueAt(row, 5).toString();
					switch(col){
					case 0:roomName=value;break;
					case 1:room_startIp=value;break;
					case 2:room_endIp=value;break;
					case 3:room_machineNum=value;break;
					case 4:room_ip=value;break;
					case 5:room_sqlIp=value;break;
					}
					table.removeEditor();
					table.setValueAt(value, row, col);
					if(!markAdd){
						String sql="update machine_room set "+roomField[col]+"='"+value+"' where room_name='"+roomName+"'";
						DBoperate_.update(sql);
					}
					else if(roomName.equals("") || room_startIp.equals("") || room_endIp.equals("") ||room_machineNum.equals("")
							|| room_ip.equals("") || room_sqlIp.equals("")){
						JOptionPane.showMessageDialog(null, "信息请填写完整!");
					}
					else {
						String sql="insert into machine_room(room_name,room_startIp,room_endIp,room_machineNum,room_ip,room_sqlIp) " +
								"values('"+roomName+"','"+room_startIp+"','"+room_endIp+"','"+room_machineNum+"','"+room_ip+"','"+room_sqlIp+"')";
						if(!DBoperate_.insert(sql)){
							JOptionPane.showMessageDialog(null, "添加失败!");
							return;
						}
						markAdd=false;
					}
				}
				else {
					if(markAdd){
						model.removeRow(markAddRow);
						markAdd=false;
					}
				}
			    System.out.println("js");
			}
			else if(e.getSource() instanceof JTable){
				 System.out.println(markAdd);
				if(!markAdd){
					
				}
				else {
					if(row ==markAddRow ){
						return;
					}
					String roomName=table.getValueAt(markAddRow, 0).toString();
					String room_startIp=table.getValueAt(markAddRow, 1).toString();
					String room_endIp=table.getValueAt(markAddRow, 2).toString();
					String room_machineNum=table.getValueAt(markAddRow, 3).toString();
					String room_ip=table.getValueAt(row, 4).toString();
					String room_sqlIp=table.getValueAt(row, 5).toString();
					if(roomName.equals("") && room_startIp.equals("") && room_endIp.equals("") && room_machineNum.equals("") && room_ip.equals("")){
						model.removeRow(markAddRow);
						markAdd=false;
					}
					else if(roomName.equals("") || room_startIp.equals("") || room_endIp.equals("") ||room_machineNum.equals("") ||
							room_ip.equals("") || room_sqlIp.equals("")){
						JOptionPane.showMessageDialog(null, "信息请填写完整!");
					}
					else {
						String sql="insert into machine_room(room_name,room_startIp,room_endIp,room_machineNum,room_ip,room_sqlIp) " +
						"values('"+roomName+"','"+room_startIp+"','"+room_endIp+"','"+room_machineNum+"','"+room_ip+"','"+room_sqlIp+"')";
						if(!DBoperate_.insert(sql)){
							JOptionPane.showMessageDialog(null, "添加失败!");
							return;
						}
						markAdd=false;
					}
				}
				System.out.println("jt");		
			}
		}

		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	public static void main(String[] args) {
		//new ClassManagePanel();
		//DBoperate.close();
	}
}

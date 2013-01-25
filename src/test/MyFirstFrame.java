package test;

import java.awt.*;
public class MyFirstFrame extends Frame{
	private static final long serialVersionUID = 1L;
	private Button quit=new Button("Quit");
	@SuppressWarnings("deprecation")
	public MyFirstFrame(){
		super("Test Window");
		add(quit);
		pack();
		show();
	}
	@SuppressWarnings("unused")
	public static void main(String args[]){
		MyFirstFrame mft=new MyFirstFrame();
	}
}
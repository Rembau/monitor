package jpcap_arp_kq;

import java.util.TreeSet;

import jpcap.*;
import jpcap.packet.*;
class Dump_ARP implements PacketReceiver {
	private String localHost;
	private TreeSet<String> ts=new TreeSet<String>(); 
	Dump_ARP (NetworkInterface networkInterface){
		this.localHost=networkInterface.addresses[0].address.toString();
		System.out.println(this.localHost);
	}
	public void receivePacket(Packet packet) {
		//System.out.println(packet.sec+"  "+packet.usec);
		if(getType(packet).equals("ARP")){
			if((((ARPPacket)packet).getTargetProtocolAddress().toString()).equals(this.localHost)){
				System.out.println(packet);
				ts.add(((ARPPacket)packet).getSenderProtocolAddress().toString().substring(1));
			}
		}
	}
	public TreeSet<String> getOnlineIP(){
		return this.ts;
	}
	public void printP(String p,Packet packet){//��ӡЭ����ϸ��Ϣ
		//System.out.println(p);
		if(p.equals("TCP")){
			printTCP(packet);
		}
		/*else if(p.equals("UDP")){
			printUDP(packet);
		}*/
	}
	public void printTCP(Packet packet){   //��ӡtcp
		TCPPacket tcpP=(TCPPacket)packet;
		EthernetPacket eP=(EthernetPacket)packet.datalink;
		System.out.println("ԴIP:"+tcpP.src_ip+" Դ�˿�:"+tcpP.src_port+" Ŀ��IP:"+tcpP.dst_ip+"Ŀ�Ķ˿�:"+tcpP.dst_port);
		System.out.println("ԴMAC��ַ:"+eP.getSourceAddress()+" Ŀ��MAC��ַ:"+eP.getDestinationAddress());
		System.out.println("����: \n");
		for(int i=0;i<tcpP.data.length;i++){
			System.out.print((char)tcpP.data[i]);
		}
		System.out.println();
	}
	public void printUDP(Packet packet){   //��ӡudp
		UDPPacket udpP=(UDPPacket)packet;
		EthernetPacket eP=(EthernetPacket)packet.datalink;
		System.out.println("ԴIP:"+udpP.src_ip+" Դ�˿�:"+udpP.src_port+" Ŀ��IP:"+udpP.dst_ip+"Ŀ�Ķ˿�:"+udpP.dst_port);
		System.out.println("ԴMAC��ַ:"+eP.getSourceAddress()+" Ŀ��MAC��ַ:"+eP.getDestinationAddress());
		System.out.println("����: \n");
		for(int i=0;i<udpP.data.length;i++){
			System.out.print((char)udpP.data[i]);
		}
	}
	public String getType(Packet packet){  //��ȡЭ������
		String type=null;
		if(packet instanceof TCPPacket){
			type="TCP";
		}
		else if(packet instanceof UDPPacket){
			type="UDP";
		}
		else if(packet instanceof ARPPacket){
			type="ARP";
		}
		else if(packet instanceof ICMPPacket){
			type="ICMP";
		}
		else if(packet instanceof IPPacket){
			type="IP";
		}
		else type="δ֪";
		return type;
	}
	
	public void printAllByte(Packet packet){    //��ӡ�������ֽ�
		System.out.println(packet.caplen);
		int i=0;
		for(i=0;i<packet.data.length;i++){
			System.out.print(packet.data[i]+"_");
		}
		System.out.println();
		System.out.println(i);
		for(i=0;i<packet.header.length;i++){
			System.out.print(packet.header[i]+"_");
		}
		System.out.println();
		System.out.println(i);
	}
	public void print(NetworkInterface []devices){   //��ӡ���� ��Ϣ
		System.out.println("usage: java Tcpdump <select a number from the following>");
		
		for (int i = 0; i < devices.length; i++) {
			System.out.println(i+" :"+devices[i].name + "(" + devices[i].description+")");
			System.out.println("    data link:"+devices[i].datalink_name + "("
					+ devices[i].datalink_description+")");
			System.out.print("    MAC address:");
			for (byte b : devices[i].mac_address)
				System.out.print(Integer.toHexString(b&0xff) + ":");
			System.out.println();
			for (NetworkInterfaceAddress a : devices[i].addresses)
				System.out.println("    address:"+a.address + " " + a.subnet + " "
						+ a.broadcast);
		}
	}
	public static void main(String[] args) throws Exception {
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[1], 2000, true, 20);
		Dump_ARP d=new Dump_ARP(devices[1]);
		jpcap.loopPacket(-1, d);
	}
}


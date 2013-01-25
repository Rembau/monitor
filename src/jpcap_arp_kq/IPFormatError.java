package jpcap_arp_kq;

public class IPFormatError extends Exception {
	private static final long serialVersionUID = 1L;
	String ip;
	public IPFormatError(String ip){
		this.ip=ip;
	}
	public String getMessage(){
		return ip+"∏Ò Ω¥ÌŒÛ";
	}
}

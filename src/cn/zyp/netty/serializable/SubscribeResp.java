package cn.zyp.netty.serializable;

import java.io.Serializable;

public class SubscribeResp implements Serializable {

	private static final long serialVersionUID = 1L;

	private int sunReqID;
	private int respCode;
	private String desc;

	public SubscribeResp() {
		super();
	}

	public SubscribeResp(int respCode, String desc) {
		super();
		this.respCode = respCode;
		this.desc = desc;
	}

	public int getSunReqID() {
		return sunReqID;
	}

	public void setSunReqID(int sunReqID) {
		this.sunReqID = sunReqID;
	}

	public int getRespCode() {
		return respCode;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SubscribeResp [sunReqID=" + sunReqID + ", respCode=" + respCode + ", desc=" + desc + "]";
	}
	
	

}

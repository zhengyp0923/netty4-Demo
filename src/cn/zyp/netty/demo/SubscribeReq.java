package cn.zyp.netty.demo;

import java.io.Serializable;

public class SubscribeReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private int suReqID;
	private String userName;
	private String productName;
	private String phoneNumber;
	private String address;

	public SubscribeReq() {
		super();
	}

	public SubscribeReq(int suReqID, String userName, String productName, String phoneNumber, String address) {
		super();
		this.suReqID = suReqID;
		this.userName = userName;
		this.productName = productName;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public int getSuReqID() {
		return suReqID;
	}

	public void setSuReqID(int suReqID) {
		this.suReqID = suReqID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "SubscribeReq [suReqID=" + suReqID + ", userName=" + userName + ", productName=" + productName
				+ ", phoneNumber=" + phoneNumber + ", address=" + address + "]";
	}
	
	

}

package com.lochend.pharmacylocation.entity;

import java.text.DecimalFormat;

public class PharmacyDetails extends PharmacyBase implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _phoneNumber;
	private String _fax;
	private String _webSite;
	private String _email;
	private String markerId;
	
	public String getPhoneNumber() {
		return _phoneNumber;
	}
	public void setPhoneNumber(String _phoneNumber) {
		this._phoneNumber = _phoneNumber;
	}
	public String getFax() {
		return _fax;
	}
	public void setFax(String _fax) {
		this._fax = _fax;
	}
	public String getWebSite() {
		return _webSite;
	}
	public void setWebSite(String _webSite) {
		this._webSite = _webSite;
	}
	public String getEmail() {
		return _email;
	}
	public void setEmail(String _email) {
		this._email = _email;
	}
	
	public String getMarkerId() {
		return markerId;
	}
	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}
}

package com.lochend.pharmacylocation;

public class DrawerItem {

	String ItemName;
	int imgResID;
	String title;
	boolean isSpinner;
	private boolean isHeader;
	private boolean navigate;

	public DrawerItem(String itemName, int imgResID, boolean pIsHeader, boolean pNavigate) {
		ItemName = itemName;
		this.imgResID = imgResID;
		this.isHeader = pIsHeader;
		this.navigate = pNavigate;
	}

	public DrawerItem(boolean isSpinner) {
		this(null, 0, false, false);
		this.isSpinner = isSpinner;
	}

	public DrawerItem(String title) {
		this(null, 0, false, false);
		this.title = title;
	}
	
	public DrawerItem(String title, boolean isHeader) {
		this(null, 0, isHeader, false);
		this.title = title;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public int getImgResID() {
		return imgResID;
	}

	public void setImgResID(int imgResID) {
		this.imgResID = imgResID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSpinner() {
		return isSpinner;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public boolean isNavigate() {
		return navigate;
	}

	public void setNavigate(boolean navigate) {
		this.navigate = navigate;
	}

}

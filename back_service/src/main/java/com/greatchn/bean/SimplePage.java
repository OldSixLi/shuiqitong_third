package com.greatchn.bean;

import java.util.List;

public class SimplePage {
	private int pageNum;
	private int pageSize;
	private int pageCount;
	private int rowCount;

	private List<?> data;
	
	public SimplePage(Page page, List<?> data) {
		this.data = data;
		if (page != null) {
			this.pageNum = page.getCurrentPage();
			this.pageCount = page.getTotalPage();
			this.pageSize = page.getShowCount();
			this.rowCount = (int) page.getTotalResult();
		}
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

}

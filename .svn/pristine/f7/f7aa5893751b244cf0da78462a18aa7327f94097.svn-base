package com.greatchn.bean;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * <p>
 * 通用分页对象
 * </p>
 * 
 * @author ZLi 2016-11-22
 * 
 * @modify LWang 2016.11.25 将pd增加不转换为json的属性
 * 
 */
public class Page {
	static int DEFAULT_SHOW_COUNT = 10; // 默认一页显示数量
	private int showCount = DEFAULT_SHOW_COUNT; // 每页显示记录数
	private int totalPage; // 总页数
	private long totalResult; // 总记录数
	private int currentPage = 1; // 当前页(默认为第一页)

	private List<Object> result = new ArrayList<>();

	@JSONField(serialize=false)
	private PageData pd = new PageData();

	public Page() {
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public long getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(long totalResult) {
		this.totalResult = totalResult;
		if (showCount <= 0) {
			showCount = DEFAULT_SHOW_COUNT;
		}
		this.totalPage = (int) totalResult / showCount + (totalResult % showCount == 0 ? 0 : 1);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {

		this.showCount = showCount;
	}

	public PageData getPd() {
		return pd;
	}

	public void setPd(PageData pd) {
		this.pd = pd;
	}

	public List<Object> getResult() {
		return result;
	}

	public void setResult(List<Object> result) {
		this.result = result;
	}

}

package com.pda.patrol.entity;

import java.io.Serializable;
import java.util.List;

public class PagedListEntity<E> implements Serializable {
	// 当前页数
	private int pageNo;
	// 分页数
	private int pageSize;
	// 分页总数
	private int pageCount;
	// 总记录数
	private int recordCount;
	// 列表
	private List<E> list;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

}

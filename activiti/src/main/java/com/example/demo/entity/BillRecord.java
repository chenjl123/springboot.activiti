package com.example.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="bill_record")
public class BillRecord {
	@Id
	@GenericGenerator(name="generator",strategy="native")
	@GeneratedValue(generator="generator")
	@Column(name="id")
	private int id;
	
	@Column(name="proc_inst_id_")
	private String procInstId;
	
	@Column(name="job_number")
	private String jobNumber;
	
	@Column(name="days")
	private int days;
	
	@Column(name="reason")
	private String reason;
	
	@Column(name="audit_reason")
	private String auditReason;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="status")
	private String status;

	public int getId() {
		return id;
	}

	public String getAuditReason() {
		return auditReason;
	}

	public void setAuditReason(String auditReason) {
		this.auditReason = auditReason;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

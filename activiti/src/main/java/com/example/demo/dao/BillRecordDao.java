package com.example.demo.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.BillRecord;

public interface BillRecordDao extends JpaRepository<BillRecord, Long> {
	@Query("from bill_record where job_number=?1 order by create_date desc")
	List<BillRecord> listByNumber(String jobNumber);
	
	@Transactional
	@Modifying
	@Query("update bill_record set status = ?1, audit_reason = ?2 where proc_inst_id_ = ?3")
	void updateStatus(String status, String reason, String proc_id);
}

package com.example.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.BillRecordDao;
import com.example.demo.entity.BillRecord;
import com.example.demo.service.ActivityConsumerService;

@Controller
public class ActivityController {
	
	@Autowired
	private ActivityConsumerService service;
	
	@Autowired
	private BillRecordDao dao;
	
	@GetMapping("/index")
	public String index(HttpServletRequest request, Model model){
		return "index";
	}
	
	/**
	 * 请假申请
	 * @param request
	 * @return
	 */
	@PostMapping("/employeeApply")
	@ResponseBody
	public String employeeApply(HttpServletRequest request){
		/*
		 * 获取请求参数
		 */
		String jobNumber = request.getParameter("jobNumber"); // 工号
		String leaveDays = request.getParameter("leaveDays"); // 请假天数
		String leaveReason = request.getParameter("leaveReason"); // 请假原因
		
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("days", leaveDays);
        map.put("date", new Date());
        map.put("reason", leaveReason);
        map.put("jobNumber", jobNumber);
        
        return service.employeeApply(map);
	}
	
	/**
	 * 查询任务
	 * @param request
	 * @return
	 */
	@GetMapping(value="/toShowTask")
	@ResponseBody
	public List<Map<String, String>> toShowTask(HttpServletRequest request){
		return service.taskList();
	}
	
	/**
	 * 部门经理审核
	 * @param request
	 * @return
	 */
	@ResponseBody
	@PostMapping("/divisionManagerAudit")
	public String divisionManagerAudit(HttpServletRequest request){
		String taskId = request.getParameter("taskId");
		String auditReason = request.getParameter("mauditReason");
		String status = request.getParameter("mstatus");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", taskId);
		map.put("mauditReason", auditReason);
		map.put("mstatus", status);
		return service.divisionManagerAudit(map);
	}
	
	/**
	 * 上级审核
	 * @param request
	 * @return
	 */
	@PostMapping("/higherLevelAudit")
	@ResponseBody
	public String higherLevelAudit(HttpServletRequest request){
		String taskId = request.getParameter("taskId");
		String auditReason  = request.getParameter("hauditReason");
		String status = request.getParameter("hstatus");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskId", taskId);
		map.put("hauditReason", auditReason);
		map.put("hstatus", status);
		return service.higherLevelAudit(map);
	}
	
	/**
	 * 部门经理审核页面
	 * @return
	 */
	@GetMapping("/viewTaskManager")
	public String viewTaskManager(){
		return "managerAudit";
	}
	
	/**
	 * 上级领导审核页面
	 * @return
	 */
	@GetMapping("/viewTaskHigher")
	public String viewTaskHigher(){
		return "higherAudit";
	}
	
	/**
	 * 根据任务ID获取任务
	 * @param taskId
	 * @return
	 */
	@GetMapping("/taskGet")
	@ResponseBody
	public Map<String, Object> taskGet(@RequestParam("taskId") String taskId){
		return service.taskGet(taskId);
	}
	
	/**
	 * 获取我的申请列表
	 * @param jobNumber
	 * @return
	 */
	@GetMapping("applyList")
	@ResponseBody
	public List<BillRecord> applyList(@RequestParam("jobNumber") String jobNumber){
		return dao.listByNumber(jobNumber);
	}
	
	/**
	 * 通过工号获取待办任务
	 * @param jobNumber
	 * @return
	 */
	@GetMapping("listByUser")
	@ResponseBody
	public List<Map<String, Object>> listByUser(@RequestParam("jobNumber") String jobNumber){
		return service.listByUser(jobNumber);
	}
	
	/**
	 * 通过工号获取审批记录
	 * @param jobNumber
	 * @return
	 */
	@GetMapping("hiTaskList")
	@ResponseBody
	public List<Map<String, Object>> hiTaskList(@RequestParam("jobNumber") String jobNumber){
		return service.hiTaskList(jobNumber);
	}
}

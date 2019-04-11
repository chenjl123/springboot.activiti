package com.example.demo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Task;

public interface ActivityConsumerService {
	/**
	 * 启动流程
	 * @return
	 */
	public String startActivityDemo();
	
	/**
	 * 查看流程图
	 * @param instanceId
	 * @param response
	 */
	public void showImg(String instanceId, HttpServletResponse response);
	
	/**
	 * 请假申请
	 * @param map
	 */
	public String employeeApply(Map<String, Object> map);
	
	/**
	 * 查询任务
	 * @return
	 */
	public List<Map<String, String>> taskList();
	
	/**
	 * 部门经理审核
	 * @param map
	 * @return
	 */
	public String divisionManagerAudit(Map<String, Object> map);
	
	/**
	 * 上级审核
	 * @param map
	 * @return
	 */
	public String higherLevelAudit(Map<String, Object> map);
	
	/**
	 * 根据任务ID获取任务
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> taskGet(String taskId);
	
	/**
	 * 根据员工工号获取当前待办任务
	 * @param jobNumber
	 * @return
	 */
	public List<Map<String, Object>> listByUser(String jobNumber);
	
	/**
	 * 通过工号获取历史审批记录
	 * @param jobNumber
	 * @return
	 */
	public List<Map<String, Object>> hiTaskList(String jobNumber);
}

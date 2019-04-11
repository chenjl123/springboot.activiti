package com.example.demo.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.BillRecordDao;
import com.example.demo.entity.BillRecord;
import com.example.demo.service.ActivityConsumerService;
import com.example.demo.tool.ActivitiUtils;

@Service
public class ActivityConsumerServiceImpl implements ActivityConsumerService {
	private static final Logger logger = LoggerFactory.getLogger(ActivityConsumerServiceImpl.class);

	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/** 流程运行时相关的服务 */
	@Autowired
	private RuntimeService runtimeService;
	
	/** 节点任务相关操作接口 */
	@Autowired
	private TaskService taskService;
	
	/** 历史记录相关服务接口 */
	@Autowired
	private HistoryService historyService;
	
	/** 流程定义和部署相关的存储服务 */
	@Autowired
	private RepositoryService repositoryService;
	
	/** 流程图生成器 */
	//@Autowired
	//private ProcessDiagramGenerator processDiagramGenerator;
	
	@Autowired
	private BillRecordDao dao;
	
	@Override
	public String startActivityDemo() {
		// TODO Auto-generated method stub
		System.out.println("method startActivityDemo begin....");
		 Map<String,Object> map = new HashMap<String,Object>();
         map.put("apply","zhangsan");
         map.put("approve","lisi");
         
         ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess", map);
         
        System.out.println("启动流程实例成功:{}" + instance);
        System.out.println("流程实例ID:{}" + instance.getId());
        System.out.println("流程定义ID:{}" + instance.getProcessDefinitionId());
         
	    //通过查询正在运行的流程实例来判断
	    ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
	    //根据流程实例ID来查询
	    List<ProcessInstance> runningList = processInstanceQuery.processInstanceId(instance.getProcessInstanceId()).list();
	    System.out.println("根据流程ID查询条数:{}" + runningList.size());
		
	    Task task = taskService.createTaskQuery().processInstanceBusinessKey(instance.getId()).singleResult();
	    // 返回流程ID
		return task.getId();
	}

	@Override
	public void showImg(String instanceId, HttpServletResponse response) {
		// TODO Auto-generated method stub
		/*
		 * 参数校验
		 */
		System.out.println("查看完整流程图！流程实例ID:{}"+ instanceId);
		if(StringUtils.isBlank(instanceId)) return;
		
		
		/*
		 *  获取流程实例
		 */
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
		if(processInstance == null) {
			System.out.println("流程实例ID:{}没查询到流程实例！" + instanceId);
			return;
		}
		
		// 根据流程对象获取流程对象模型
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		
		
		/*
		 *  查看已执行的节点集合
		 *  获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
		 */
		// 构造历史流程查询
		HistoricActivityInstanceQuery historyInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId);
		// 查询历史节点
		List<HistoricActivityInstance> historicActivityInstanceList = historyInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();
		if(historicActivityInstanceList == null || historicActivityInstanceList.size() == 0) {
			System.out.println("流程实例ID:{}没有历史节点信息！" + instanceId);
			outputImg(response, bpmnModel, null, null);
			return;
		}
		// 已执行的节点ID集合(将historicActivityInstanceList中元素的activityId字段取出封装到executedActivityIdList)
		List<String> executedActivityIdList = historicActivityInstanceList.stream().map(item -> item.getActivityId()).collect(Collectors.toList());
		
		/*
		 *  获取流程走过的线
		 */
		// 获取流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
		List<String> flowIds = ActivitiUtils.getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
		
		
		/*
		 * 输出图像，并设置高亮
		 */
		outputImg(response, bpmnModel, flowIds, executedActivityIdList);
	}
	
	/**
	 * <p>输出图像</p>
	 * @param response 响应实体
	 * @param bpmnModel 图像对象
	 * @param flowIds 已执行的线集合
	 * @param executedActivityIdList void 已执行的节点ID集合
	 * @author FRH
	 * @time 2018年12月10日上午11:23:01
	 * @version 1.0
	 */
	private void outputImg(HttpServletResponse response, BpmnModel bpmnModel, List<String> flowIds, List<String> executedActivityIdList) {
		InputStream imageStream = null;
		try {
			//imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds);
			//imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", true, "png");
			// 输出资源内容到相应对象
			byte[] b = new byte[1024];
			int len;
			while ((len = imageStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			response.getOutputStream().flush();
		}catch(Exception e) {
			System.out.println("流程图输出异常！"+ e);
		} finally { // 流关闭
			//StreamUtils.closeInputStream(imageStream);
			try {
				imageStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String employeeApply(Map<String, Object> map) {
		// TODO Auto-generated method stub
		map.put("userId", "001");
		ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess", map);
		/*
		 *  查询任务
		 */
		Task task = taskService.createTaskQuery().taskAssignee("001")
				.orderByTaskCreateTime().desc().list().get(0);
		if(task == null) {
			//提交失败
			return "fail";
		}
		
		/*
		 * 参数传递并提交申请
		 */
		BillRecord record = new BillRecord();
		record.setCreateDate(new Date());
		record.setDays(Integer.parseInt(map.get("days").toString()));
		record.setJobNumber(map.get("jobNumber").toString());
		record.setProcInstId(instance.getId());
		record.setReason(map.get("reason").toString());
		record.setStatus("0");
		dao.saveAndFlush(record);
		
		map.put("higherId", "002");
        taskService.complete(task.getId(), map);
        
        Task higherTask = taskService.createTaskQuery().taskAssignee("002")
        		.orderByTaskCreateTime().desc().list().get(0);
        //higherTask.setDescription("");
        taskService.saveTask(higherTask);
        logger.info("执行【员工申请】环节，流程推动到【002】环节");
        
        /*
         * 返回成功
         */
		return "success";
	}
	
	@Override
	public List<Map<String, String>> taskList() {
		// TODO Auto-generated method stub
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		/*
		 * 获取请求参数
		 */
		//taskService.getVariables(taskId)
		List<Task> taskList = taskService.createTaskQuery().list();
		if(taskList == null || taskList.size() == 0) {
			logger.info("查询任务列表为空！");
			return resultList;
		}
		
		/*
		 * 查询所有任务，并封装
		 */
		for(Task task : taskList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("taskId", task.getId());
			map.put("name", task.getName());
			map.put("createTime", task.getCreateTime().toString());
			map.put("assignee", task.getAssignee());
			map.put("instanceId", task.getProcessInstanceId());
			map.put("executionId", task.getExecutionId());
			map.put("definitionId", task.getProcessDefinitionId());
			resultList.add(map);
		}
		return resultList;
	}

	@Override
	public String divisionManagerAudit(Map<String, Object> map) {
		// TODO Auto-generated method stub
		if(StringUtils.isBlank(map.get("taskId").toString())) return "fail";
		/*
		 * 查找任务
		 */
		Task task = taskService.createTaskQuery().taskId(map.get("taskId").toString()).singleResult();
		if(task == null) {
			logger.info("审核任务ID:{}查询到任务为空！", map.get("taskId").toString());
			return "fail";
		}
		/*
		 * 设置局部变量参数，完成任务
		 */
 
		//审批
		dao.updateStatus(map.get("mstatus").toString(), map.get("mauditReason").toString(), task.getProcessInstanceId());
	    taskService.complete(task.getId(), map);
		return "success";
	}

	@Override
	public String higherLevelAudit(Map<String, Object> map) {
		// TODO Auto-generated method stub
		logger.info("上级审核任务ID:{}", map.get("taskId").toString());
		if(StringUtils.isBlank(map.get("taskId").toString())) return "fail";
		
		/*
		 * 查找任务
		 */
		Task task = taskService.createTaskQuery().taskId(map.get("taskId").toString()).singleResult();
		if(task == null) {
			logger.info("审核任务ID:{}查询到任务为空！", map.get("taskId").toString());
			return "fail";
		}
		
		
		/*
		 * 设置局部变量参数，完成任务
		 */
		if("0".equals(map.get("hstatus").toString())){
			//审核通过
			map.put("leaderId", "003");
			taskService.complete(task.getId(), map);
			Task managerTask = taskService.createTaskQuery().taskAssignee("003")
		        		.orderByTaskCreateTime().desc().list().get(0);
		        taskService.saveTask(managerTask);
		}else{
			//审核不通过
			dao.updateStatus(map.get("hstatus").toString(), map.get("hauditReason").toString(), task.getProcessInstanceId());
			map.put("leaderId", "");
		    taskService.complete(task.getId(), map);
		    taskService.deleteTask(task.getId());
			runtimeService.deleteParticipantUser(task.getProcessInstanceId(), "结束");
		}
		return "success";
	}

	@Override
	public Map<String, Object> taskGet(String taskId) {
		// TODO Auto-generated method stub
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String, Object> paramMap = taskService.getVariables(taskId);
		Map<String, Object> temp = new HashMap<>();
		temp.put("task", task);
		temp.put("data", paramMap);
		return temp;
	}

	@Override
	public List<Map<String, Object>> listByUser(String jobNumber) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> mapList  = new ArrayList<>();
		List<Task> taskList = taskService.createTaskQuery().taskAssignee(jobNumber).orderByTaskCreateTime().desc().list();
		for(Task task : taskList){
			Map<String, Object> map =  taskService.getVariables(task.getId());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map.put("create_date", format.format(task.getCreateTime()));
			map.put("taskId", task.getId());
			mapList.add(map);
		}
		return mapList;
	}
	
	@Override
	public List<Map<String, Object>> hiTaskList(String jonNumber){
		List<Map<String, Object>> mlist = new ArrayList<>();
		//历史任务
		List<HistoricTaskInstance> htaskList = historyService.createHistoricTaskInstanceQuery().taskAssignee(jonNumber).finished().list();
		Map<String, Object> map = null;
		for(HistoricTaskInstance task : htaskList){
			//历史变量值
			List<HistoricVariableInstance> vlist = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
			map = new HashMap<>();
			for(HistoricVariableInstance hvar : vlist){
				map.put(hvar.getVariableName(), hvar.getValue());
			}
			mlist.add(map);
		}
		return mlist;
	}

}

package com.nice.demo.batch.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

//@Configuration
//@EnableBatchProcessing
@RestController
@RequestMapping("/api/batch")
@EnableAutoConfiguration
public class BatchController {
	
	Logger logger = LoggerFactory.getLogger(BatchController.class);
	
	ResponseEntity<?> entity = null;
	
	private static final String BATCH_NAME = "BaseJobStep";
	
//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;
//	@Autowired
//	private SimpleJobLauncher jobLauncher;
	
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	JobExplorer jobExplorer;

	@Autowired
	JobOperator jobOperator;

	@Autowired
	JobRepository jobRepository;

	
	@Autowired
	@Qualifier("memberCheckJob")
	Job memberCheckJob;

	@ApiOperation(value="Batch Test[START]", notes="Batch Test[START]")
	@PutMapping(value="/startjob")
	public ResponseEntity<?> batchStart() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> jobInstances = new HashMap<String, Object>();
		List<HashMap<String, Object>> stepsInfo = new ArrayList<HashMap<String, Object>>();
		
		try {
			JobExecution jobExecution = startBatchJobs();
			JobInstance jobInstance = jobExecution.getJobInstance();
			logger.info(">>>>>>>>>>>>>>>>> JobExecution "+ jobExecution.toString());
			logger.info(">>>>>>>>>>>>>>>>> JobInstance "+ jobInstance.toString());
			
			jobInstances.put("id", jobInstance.getId());
			jobInstances.put("name", jobInstance.getJobName());
			jobInstances.put("parameters", jobExecution.getJobParameters());
			jobInstances.put("startTime", jobExecution.getStartTime());
			jobInstances.put("endTime", jobExecution.getEndTime());
			jobInstances.put("isRunning", jobExecution.getStatus().isRunning());
			jobInstances.put("exitStatus", jobExecution.getExitStatus().getExitCode());
			
			Iterator<StepExecution> stepExecutions = jobExecution.getStepExecutions().iterator();
			logger.info(">>>>>>>>>>>>>>>>> Iterator<StepExecution> "+ stepExecutions.hasNext());
			//1:N (JOB:STEP)
			StepExecution stepExecution = null;
			while (stepExecutions.hasNext()) {
				HashMap<String, Object> stepInfo = new HashMap<String, Object>();
				stepExecution = stepExecutions.next();
				logger.info(">>>>>>>>>>>>>>>>> StepExecution "+ stepExecution.toString());

				stepInfo.put("stepId", stepExecution.getId());
				stepInfo.put("stepName", stepExecution.getStepName());
				stepInfo.put("readCount", stepExecution.getReadCount());
				stepInfo.put("writeCount", stepExecution.getWriteCount());
				stepInfo.put("readSkipCount", stepExecution.getReadSkipCount());
				stepInfo.put("processSkipCount", stepExecution.getProcessSkipCount());
				stepInfo.put("writeSkipCount", stepExecution.getWriteSkipCount());
				stepInfo.put("totalSkipCount", stepExecution.getSkipCount());
				stepInfo.put("commitCount", stepExecution.getCommitCount());
				stepInfo.put("rollbackCount", stepExecution.getRollbackCount());
				stepInfo.put("exitStatus", stepExecution.getExitStatus().getExitCode());

				stepsInfo.add(stepInfo);
			}
			
			map.put("jobInstances", jobInstances);
			map.put("stepsInfo", stepsInfo);
			
			entity = new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@ApiOperation(value="Batch Test[Stop]", notes="Batch Test[Stop]")
	@PutMapping(value="/stopjob")
	public ResponseEntity<?> batchStop() {
//		try {
//			stopBatchJobs();
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
		return entity;
	}
	
	@Bean
	public SimpleJobLauncher simpleJobLauncher() {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(simpleAsyncTaskExecutor());
		return jobLauncher;
	}

	public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		simpleAsyncTaskExecutor.setConcurrencyLimit(10);
		return simpleAsyncTaskExecutor;
	}
	
	public JobExecution startBatchJobs() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		logger.info("start startBatchJobs...");

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		
//		JobExecution jobExecution = simpleJobLauncher().run(memberCheckJob, jobParameters);
		JobExecution jobExecution = jobLauncher.run(memberCheckJob, jobParameters);
//		try {
//			/* job 강제정지 */
//			jobOperator.stop(jobExecution.getId());
//		} catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return jobExecution;
	}
	
	public JobExecution stopBatchJobs(JobExecution jobExecution) throws Exception {
		logger.info("stop stopBatchJobs...");
		if (jobExecution.getStatus() == BatchStatus.STARTED || jobExecution.getStatus() == BatchStatus.STARTING) {
			jobOperator.stop(jobExecution.getId());
			logger.info("###########Stopped#########");
			logger.info(jobExecution.getStatus() + "ID :" + jobExecution.getId());
			logger.info("###########Stopped#########");
		}
		return jobExecution;
	}
}

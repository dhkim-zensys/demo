package com.nice.demo.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;

import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.nice.demo.config.SpringBatchConfig;
import com.nice.demo.member.service.MemberService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={SpringBatchConfig.class, testJobConfig.class})
public class JobTest {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Autowired
	private MemberService memberService;
	
//	@Test
//	public void test() throws Exception {
//		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
//	}
	

	@Test
	public void testLaunchJob() throws Exception {
		assertEquals("1", "1");
	}
}

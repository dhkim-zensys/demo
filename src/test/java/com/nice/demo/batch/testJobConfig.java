package com.nice.demo.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class testJobConfig {

	@Bean
	public JobLauncherTestUtils jobLauncherTestUtils() {  
		return new JobLauncherTestUtils();  
	}

}

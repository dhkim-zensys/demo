package com.nice.demo.config;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.nice.demo.batch.member.processor.RESTMemberCheckProcessor;
import com.nice.demo.batch.member.reader.RESTMemberCheckReader;
import com.nice.demo.batch.member.writer.RESTMemberCheckWriter;
import com.nice.demo.member.domain.Member;

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(BatchJobConfig.class);
	
	public static final int CHUNK_AND_PAGE_SIZE = 100;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	@Primary
	public JpaTransactionManager jpaTransactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
	
	@Bean
	@Primary
	ItemReader<List<Member>> memberCheckReader() {
		return new RESTMemberCheckReader(restTemplate());
	}
	
	@Bean
	@Primary
	ItemProcessor<List<Member>, List<Member>> memberCheckProcessor() {
		return new RESTMemberCheckProcessor();
	}
	
	@Bean
	@Primary
	ItemWriter<List<Member>> memberCheckWriter() {
		return new RESTMemberCheckWriter();
	}
	
//	@Bean(name = "memberCheckJob")
	public Job memberCheckJob(@Qualifier("memberCheckStep") Step memberCheckStep) {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(CHUNK_AND_PAGE_SIZE);
		threadPoolTaskExecutor.afterPropertiesSet();
		
		Flow splitFlow = new FlowBuilder<Flow>("realEstateTradeStepSplitFlow")
				.split(threadPoolTaskExecutor)
				.add(new FlowBuilder<Flow>("memberCheckStepFlow").start(memberCheckStep).build())
				.build();
		
		return jobBuilderFactory.get("회원 데이터 수집")
								.start(splitFlow)
								.end()
								.build();
	}
	
	@Bean
    public Step memberCheckStep(JpaTransactionManager transactionManager
                           ,ItemReader<List<Member>> realEstateAptTradeReader
                           , ItemProcessor<List<Member>, List<Member>> realEstateAptTradeProcessor
                           , ItemWriter<List<Member>> realEstateAptTradeWriter) {
        return stepBuilderFactory.get("회원 데이터 수집 스텝")
                .transactionManager(transactionManager)
                .<List<Member>, List<Member>>chunk(1)
                .reader(realEstateAptTradeReader)
                .processor(realEstateAptTradeProcessor)
                .writer(realEstateAptTradeWriter)
                .build();
    }
}

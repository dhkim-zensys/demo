package com.nice.demo.config;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemReader;

import com.nice.demo.batch.member.processor.RESTMemberCheckProcessor;
import com.nice.demo.member.domain.Grade;
import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;
import com.nice.demo.member.repository.MemberRepository;
import com.nice.demo.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SpringBatchConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringBatchConfig.class);
	
	public static final int CHUNK_AND_PAGE_SIZE = 100;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private DataSource dataSource;
	
	private final String JOB_NAME = "SampleJob"; 
	private final String STEP_NAME = "SampleStep"; 
	private final JobBuilderFactory jobBuilderFactory; 
	private final StepBuilderFactory stepBuilderFactory;
	private Resource outputResource = new FileSystemResource("output/output.txt");
	
//	@Bean
//	@Primary
//	public JpaTransactionManager jpaTransactionManager() {
//		final JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setDataSource(dataSource);
//		return transactionManager;
//	}
	
	@Bean
	@Primary
	public BatchListener listener() {
		return new BatchListener();
	}
	
	@Bean(name = "memberCheckJob")
	public Job job(@Qualifier("step") Step step,BatchListener listener) {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(CHUNK_AND_PAGE_SIZE);
		threadPoolTaskExecutor.afterPropertiesSet();
		
		Flow splitFlow = new FlowBuilder<Flow>("StepSplitFlow")
				.split(threadPoolTaskExecutor)
				.add(new FlowBuilder<Flow>("StepFlow").start(step).build())
				.build();
		
//		chunk 1개씩 처리할경우 모든 Low가 저장될때까지 반복실행 / 아래설정시 DB -> File 처리시 가장마지막 데이터만 저장되는 현상있음
//		return jobBuilderFactory.get(JOB_NAME)
//				.start(step(listener)).on("CONTINUE").to(step(listener)).on("FINISHED").end()
//				.end()
//				.build();
		
		return jobBuilderFactory.get(JOB_NAME)
				.start(splitFlow)
				.end()
				.build();

	}
	
//	기본 Step 실행후 종료
	@Bean
	public Step step2() {
		return stepBuilderFactory.get(STEP_NAME)
				.tasklet((stepContribution, chunkContext) -> {
					log.info(">>>>>>>>>>>>>>>>>>>>>>>>"+STEP_NAME + "Step1 Started");
					return RepeatStatus.FINISHED;
				}).build();
	}
	
	@Bean
//	public Step step(JpaTransactionManager transactionManager, BatchListener listener) {
	public Step step(BatchListener listener) {
		return stepBuilderFactory.get(STEP_NAME)
//								 .transactionManager(transactionManager)
//								 .listener(listener)
								 .<Member, Member>chunk(100)	//<I,O> I type 10건 단위로 O에 반환
								 .reader(itemReader())			//DB에서 조건의 데이터 레코드 Reader
								 .processor(itemProcessor())	// 비즈니스로직
//								 .writer(itemWriter())
								 .writer(FlatFileItemWriter())	//처리된 데이터를 File에 저장 (DB->FILE)
								 .build();
	}
	
	
	@Bean
	@StepScope
	public ListItemReader<Member> itemReader() {
		logger.info(">>>>>>>>>>>>>>>>> START ListItemReader");
		List<Member> oldMember = memberService.findByLastlogBeforeAndStatusLike(30,MemberStatus.ACTIVE);
//		return new ListItemReader<>(oldMember);
		
		return new ListItemReader<Member>(oldMember) {
			
			private StepExecution stepExecution;
			
			@BeforeStep
			public void saveStepExecution(StepExecution stepExecution) {
				this.stepExecution = stepExecution;
				this.stepExecution.getExecutionContext().put("items", new ArrayList<>());
				logger.info(">>>>>>>>>>>>>>>>> saveStepExecution" + stepExecution.toString());
			}
		};
		
	}

	@Bean
	@StepScope
	public ItemProcessor<Member, Member> itemProcessor() {
		logger.info(">>>>>>>>>>>>>>>>> START itemProcessor");
		return Member::setInactive;
		
//		return new ItemProcessor<Member, Member>(){
//			@Override
//			public Member process(Member member) throws Exception{
//				member.setStatus(MemberStatus.INACTIVE);
//				member.setGrade(Grade.BASIC);
//				return member;
//			}
//		};
	}
	
	@Bean
	@StepScope
	public FlatFileItemWriter<Member> FlatFileItemWriter() {
		logger.info(">>>>>>>>>>>>>>>>> START FlatFileItemWriter");
		
		FlatFileItemWriter<Member> writer =  new FlatFileItemWriter<Member>();
		writer.setResource(outputResource);
		DelimitedLineAggregator<Member> delLineAgg = new DelimitedLineAggregator<Member>();
		delLineAgg.setDelimiter(",");
		BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<Member>();
		fieldExtractor.setNames(new String[] {"id", "age", "email"});
		delLineAgg.setFieldExtractor(fieldExtractor);
		writer.setLineAggregator(delLineAgg);
		
		
		return writer;
	}
	
//	@Bean
//	public ItemWriter<Member> itemWriter() {
//		logger.info(">>>>>>>>>>>>>>>>> START itemWriter");
//		return ((List<? extends Member> memberList) -> memberRepository.saveAll(memberList));
//	}
}

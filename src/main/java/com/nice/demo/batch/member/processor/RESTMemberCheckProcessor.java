package com.nice.demo.batch.member.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

import com.nice.demo.batch.member.reader.RESTMemberCheckReader;
import com.nice.demo.member.domain.Grade;
import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;

@Configuration
//@StepScope
public class RESTMemberCheckProcessor  implements ItemProcessor<List<Member>, List<Member>> {
	private static final Logger logger = LoggerFactory.getLogger(RESTMemberCheckProcessor.class);
	
	@Override
	public List<Member> process(List<Member> list) throws Exception{
		logger.info(">>>>>>>>>>>>>>>>> START ItemProcessor");
		logger.info(">>>>>>>>>>>>>>>>> KMS TEST s");
		List<Member> outdata = new ArrayList<Member>();
		for (Member member : list) {
			member.setStatus(MemberStatus.INACTIVE);
			member.setGrade(Grade.BASIC);
			outdata.add(member);
		}
		return list;
		
	}
}
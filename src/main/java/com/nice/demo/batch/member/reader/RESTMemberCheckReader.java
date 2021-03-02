package com.nice.demo.batch.member.reader;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;
import com.nice.demo.member.repository.MemberRepository;
import com.nice.demo.member.service.MemberService;

@Configuration
@StepScope
public class RESTMemberCheckReader implements ItemReader<List<Member>> {
	
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;
	
	private RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(RESTMemberCheckReader.class);

	public RESTMemberCheckReader(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Override
	public List<Member> read() throws Exception {
		logger.info(">>>>>>>>>>>>>>>>> START ItemReader");
		List<Member> oldMember = memberService.findByLastlogBeforeAndStatusLike(30,MemberStatus.ACTIVE);
//		List<Member> oldMember = memberService.findAll();
		int cnt = oldMember.size();
		if(cnt == 0) {
			logger.info(">>>>>>>>>>>>>>>>> oldMember SIZE 0");
			return null;
		}
		return oldMember;
	}
}

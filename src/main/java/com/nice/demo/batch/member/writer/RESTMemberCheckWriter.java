package com.nice.demo.batch.member.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.repository.MemberRepository;

@Configuration
public class RESTMemberCheckWriter implements ItemWriter<List<Member>>{
	private static final Logger logger = LoggerFactory.getLogger(RESTMemberCheckWriter.class);
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public void write(List<? extends List<Member>> lists) throws Exception {
		logger.info(">>>>>>>>>>>>>>>>> START ItemWriter");
		for (List<Member> list : lists) {
			logger.info("휴먼회원 데이터 수신, ADW 트랜잭션 시작. [변경 사용자 count : " + list.size() + "]");
			memberRepository.saveAll(list);
			
		}
	}
}

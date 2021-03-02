package com.nice.demo.config;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;
import com.nice.demo.member.service.MemberService;

@Configuration
public class BatchListener {
	
	@Autowired
	MemberService memberService;
	
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
    	List<Member> oldMember = memberService.findByLastlogBeforeAndStatusLike(30,MemberStatus.ACTIVE);
        if(oldMember.size() > 0) {
            return new ExitStatus("CONTINUE");
        }
        else {
            return new ExitStatus("FINISHED");
        }
    }
}

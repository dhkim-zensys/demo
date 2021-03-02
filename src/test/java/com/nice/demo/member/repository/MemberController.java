package com.nice.demo.member.repository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.service.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class MemberController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MockMvc mockMvc;
	
	@Test
	public void getMemberList() throws Exception{
		String nowMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.info("================ START ["+nowMethod+"]================");
		 mockMvc.perform(get("/api/members"))
		 .andExpect(status().isOk())
		 .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		 .andDo(print());
		 logger.info("================ END ["+nowMethod+"]================");
	}
	
//	@Test
//	public void registerMember() throws Exception{
//		String nowMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
//		logger.info("================ START ["+nowMethod+"]================");
//		
//		Member member = new Member();
//		member.setAge(99);
//		member.setId("testuser");
//		member.setEmail("testuser@gmail.com");
//		
//		 mockMvc.perform(post("/api/registerMember"))
//		 .andExpect(status().isOk())
//		 .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//		 .andDo(print());
//		 logger.info("================ END ["+nowMethod+"]================");
//	}

}

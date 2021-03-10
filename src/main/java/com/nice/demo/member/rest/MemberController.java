package com.nice.demo.member.rest;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.service.MemberService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	ResponseEntity<?> entity = null;
	
    
//	@ApiImplicitParams({
//		@ApiImplicitParam(name="id", value="member ID", example="user03", required=true),
//		@ApiImplicitParam(name="age", value="member age", example="21", required=true),
//		@ApiImplicitParam(name="email", value="member email", example="user03@gmil.com", required=true)
//	})
	@ApiOperation(value="사용자 등록", notes="사용자 등록")
	@PostMapping(value="/register")
	public ResponseEntity<?> registerMember(@RequestBody Member member) {
		try {
			if(member != null) {
				String id = member.getId();
				int cnt = memberService.countById(id);
				if(cnt >= 1) {
					entity = new ResponseEntity<String>("PK ERROR", HttpStatus.BAD_REQUEST);
				}else {
					memberService.save(member);
					entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
				}
			}else {
				entity = new ResponseEntity<String>("NO DATA", HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@ApiOperation(value="사용자리스트 조회", notes="사용자리스트 조회")
	@GetMapping(value = "/members")
	public ResponseEntity<?> getMemberList(){
		try {
			List<Member> list = memberService.findAll();
			
			HashMap<String, Object> map = new HashMap<>();
			map.put("list", list);
			
			entity = new ResponseEntity<List<Member> >(list, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	
	@ApiOperation(value="사용자리스트 조회2", notes="사용자리스트 조회2")
	@GetMapping(value = "/members2")
	public ResponseEntity<?> getMemberList(){
		try {
			List<Member> list = memberService.findAll();
			
			HashMap<String, Object> map = new HashMap<>();
			map.put("list", list);
			
			entity = new ResponseEntity<List<Member> >(list, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	
	@ApiOperation(value="사용자정보 조회", notes="사용자정보 조회")
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> getMember(@PathVariable("id") String id){
		try {
			Member member = memberService.findById(id);
			if(member != null) {
				entity = new ResponseEntity<Member>(member, HttpStatus.OK);
			}else {
				entity = new ResponseEntity<String>("NO DATA", HttpStatus.OK);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	@ApiOperation(value="사용자 삭제", notes="사용자 삭제")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteMember(@PathVariable("id") String id){
		try {
			int cnt = memberService.deleteById(id);
			if(cnt == 1) {
				entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
			}else {
				entity = new ResponseEntity<String>("NO DATA", HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@ApiOperation(value="사용자정보 수정", notes="사용자정보 수정")
	@PutMapping(value="/{id}")
	public ResponseEntity<?> udpateMember(
			@PathVariable("id") String id,
			@RequestBody Member member) {
		try {
			if(id != null && member != null && (id.equals((String)member.getId()))) {
				int cnt = memberService.countById(id);
				if(cnt == 1) {
					memberService.save(member);
					entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
				}else {
					entity = new ResponseEntity<String>("FAIL", HttpStatus.BAD_REQUEST);
				}
			}else {
				entity = new ResponseEntity<String>("NO DATA", HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
}

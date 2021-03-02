package com.nice.demo.member.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;
import com.nice.demo.member.repository.MemberRepository;
import com.nice.demo.util.CommonUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepository;
	
	public List<Member> findAll() {
		List<Member> list = memberRepository.findAll();
		return list;
	}
	
	public Member findById(String id) {
		Member member = memberRepository.findById(id);
		return member;
	}
	
	public int countById(String id) {
		int cnt = memberRepository.countById(id);
		return cnt;
	}
	
	public void save(Member member) {
		memberRepository.save(member);
	}
	
	public void update(Member member) {
		memberRepository.save(member);
	}

	public int deleteById(String id) {
		int cnt = memberRepository.deleteById(id);
		return cnt;
	}
	
	public List<Member> findByLastlogBetween(){
		Date before = null;
		before = CommonUtil.Fn_FindBeforeDate(30);
		 
		List<Member> list = memberRepository.findByLastlogBefore(before);
		return list;
	}
	
	public List<Member> findByLastlogBeforeAndStatusLike(int defore, MemberStatus search){
		Date before = null;
		before = CommonUtil.Fn_FindBeforeDate(defore);

		List<Member> list = memberRepository.findByLastlogBeforeAndStatusLike(before,search);
		return list;
	}
	
}

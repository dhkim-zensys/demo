package com.nice.demo.member.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nice.demo.member.domain.Member;
import com.nice.demo.member.domain.MemberStatus;

public interface MemberRepository extends JpaRepository<Member, Long> {
	
	Member findById(String id);
	
	int deleteById(String id);
	
	int countById(String id);
	
	List<Member> findByLastlogBefore(Date before);

	List<Member> findByLastlogBeforeAndStatusLike(Date before,MemberStatus search);
}

package com.nice.demo.upbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nice.demo.upbit.model.Notice;



public interface NoticeRepository extends JpaRepository<Notice, Long> {
	
	Notice findById(String id);
	
	int deleteById(String id);
	
	int countById(String id);

	@Query("SELECT max(id) FROM Notice u")
	String selectMax();
}

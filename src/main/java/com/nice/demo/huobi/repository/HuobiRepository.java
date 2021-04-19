package com.nice.demo.huobi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nice.demo.huobi.model.Base;

public interface HuobiRepository extends JpaRepository<Base, Long> {
	
	Base findByCurrency(String currency);
	
	int countByCurrency(String currency);
	
}

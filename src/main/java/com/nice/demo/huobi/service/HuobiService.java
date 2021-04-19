package com.nice.demo.huobi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nice.demo.huobi.model.Base;
import com.nice.demo.huobi.repository.HuobiRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HuobiService {
	
	@Autowired
	private HuobiRepository huobiRepository;
	
	public int countByCurrency(String currency) {
		int cnt = huobiRepository.countByCurrency(currency);
		return cnt;
	}
	
	
	public void save(Base base) {
		huobiRepository.save(base);
	}
	

}

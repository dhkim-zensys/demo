package com.nice.demo.upbit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nice.demo.upbit.model.Account;
import com.nice.demo.upbit.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	        
	
	public List<Account> findAll() {
		
		
		
		
		
		List<Account> list = accountRepository.findAll();
		
		
		
		
		
		
		return list;
	}
	
	
}

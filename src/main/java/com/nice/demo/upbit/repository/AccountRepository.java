package com.nice.demo.upbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nice.demo.upbit.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	

}

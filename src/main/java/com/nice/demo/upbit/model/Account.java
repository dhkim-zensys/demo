package com.nice.demo.upbit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account {
	
	@Id // PK 필드
	@NotNull
	@ApiParam(value = "currency", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String currency;			//화폐를 의미하는 영문 대문자 코드
	
	@NotNull
	@ApiParam(value = "balance", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String balance;				//주문가능 금액/수량
	
	@NotNull
	@ApiParam(value = "locked", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String locked;				//주문 중 묶여있는 금액/수량
    
	@NotNull
	@ApiParam(value = "avg_buy_price", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String avg_buy_price;		//매수평균가
    
	@NotNull
	@ApiParam(value = "avg_buy_price_modified", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private boolean avg_buy_price_modified;		//매수평균가 수정 여부
    
	@NotNull
	@ApiParam(value = "unit_currency", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String unit_currency;		//	평단가 기준 화폐

}

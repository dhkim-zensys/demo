package com.nice.demo.huobi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@Entity
@Table(name = "history")
public class History {
	
	@Id // PK 필드
	@NotNull
	@ApiParam(value = "currency", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String currency;			//화폐를 의미하는 영문 대문자 코드
	
	
	@NotNull
	@ApiParam(value = "amount", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String amount;			//현재 금액
	
	
	@NotNull
	@ApiParam(value = "rt", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String rt;				//환율
	

}

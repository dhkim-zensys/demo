package com.nice.demo.member.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import ch.qos.logback.core.status.Status;
import io.swagger.annotations.ApiParam;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member implements Serializable{
	private static final long serialVersionUID = 810457109758530244L;
	
	@Id // PK 필드
	@NotNull
	@ApiParam(value = "member ID", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String id;
	
	@Column(columnDefinition="INT", nullable = false, insertable=true, updatable=true)
	@ApiParam(value = "member age", required = true)
	private int age;
	
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=true)
	@ApiParam(value = "member email", required = true)
	private String email;
	
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=false, updatable=false)
	@ApiParam(value = "member regdate", required = true)
	private Date regdate;
	
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=false, updatable=false)
	@ApiParam(value = "member lastlog", required = true)
	private Date lastlog;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=true)
	private MemberStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=true)
	private Grade grade;
	
	public Member setInactive() {
		status = MemberStatus.INACTIVE;
		return this;
	}
}

package com.nice.demo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@Entity
@Table(name = "Notice")
public class Notice implements Serializable{
	
	private static final long serialVersionUID = 4562354681575392324L;

	@Id // PK 필드
	@NotNull
	@ApiParam(value = "it", required = true)
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=false)
	private String id;
	
	@Column(columnDefinition="varchar(255)", nullable = false, insertable=true, updatable=true)
	@ApiParam(value = "title", required = true)
	private String title;

	@Column(columnDefinition="varchar(255)", nullable = true, insertable=true, updatable=true)
	@ApiParam(value = "created_at", required = true)
	private String created_at;
	
	@Column(columnDefinition="varchar(255)", nullable = true, insertable=true, updatable=true)
	@ApiParam(value = "updated_at", required = true)
	private String updated_at;
}

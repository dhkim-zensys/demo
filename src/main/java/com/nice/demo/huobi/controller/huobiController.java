package com.nice.demo.huobi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nice.demo.huobi.model.Base;
import com.nice.demo.huobi.service.HuobiService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class huobiController {
	
	@Autowired
	HuobiService huobiService;

	ResponseEntity<?> entity = null;
	
    
	
	@ApiOperation(value="기본 환율 입력", notes="기본 환율 입력")
	@PostMapping(value = "/v1/register")
	public ResponseEntity<?> register(@RequestBody Base base){

		try {
			if(base != null) {
				String currency = base.getCurrency();
				int cnt = huobiService.countByCurrency(currency);
				if(cnt >= 1) {
					entity = new ResponseEntity<String>("PK ERROR", HttpStatus.BAD_REQUEST);
				}else {
					huobiService.save(base);
					entity = new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
				}
			}else {
				entity = new ResponseEntity<String>("NO DATA", HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    	
		return entity;
	}
	
		
}

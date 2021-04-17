package com.nice.demo.upbit.controller;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nice.demo.upbit.service.AccountService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class UpbitController {
	
	@Value("${upbit.open.api.access.key}") 
	private String accessKey; 
	
	@Value("${upbit.open.api.secret.key}") 
	private String secretKey;
	
	@Value("${upbit.open.api.server.url}") 
	private String serverUrl;
	
	@Autowired
	AccountService accountService;
	
	ResponseEntity<?> entity = null;
	
    @ApiOperation(value="자산 조회", notes="자산 조회")
	@GetMapping(value = "/v1/accounts")
	public ResponseEntity<?> accounts(){
    	
    	
         Algorithm algorithm = Algorithm.HMAC256(secretKey);
         String jwtToken = JWT.create()
                 .withClaim("access_key", accessKey)
                 .withClaim("nonce", UUID.randomUUID().toString())
                 .sign(algorithm);

         String authenticationToken = "Bearer " + jwtToken;

         try {
             HttpClient client = HttpClientBuilder.create().build();
             HttpGet request = new HttpGet(serverUrl + "/v1/accounts");
             request.setHeader("Content-Type", "application/json");
             request.addHeader("Authorization", authenticationToken);

             HttpResponse response = client.execute(request);
             //HttpEntity upbitEntity = response.getEntity();

             
             String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
             
             System.out.println(jsonResponse);
             
            		 
             entity = new ResponseEntity<String>(jsonResponse, HttpStatus.OK);
             
             
         } catch (IOException e) {
             e.printStackTrace();
             entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
         }
    	
    	
		return entity;
	}
	
	
	
		
}
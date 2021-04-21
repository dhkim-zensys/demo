package com.nice.demo.upbit.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nice.demo.upbit.model.Account;
import com.nice.demo.upbit.model.UpbitAccount;
import com.nice.demo.upbit.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AccountService {
	
	@Value("${upbit.open.api.access.key}") 
	private String accessKey; 
	
	@Value("${upbit.open.api.secret.key}") 
	private String secretKey;
	
	@Value("${upbit.open.api.server.url}") 
	private String serverUrl;
	
	@Autowired
	private AccountRepository accountRepository;
	
	public UpbitAccount accounts(String currency){
		
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
            
            //System.out.println(jsonResponse);
            
            ObjectMapper objectMapper = new ObjectMapper();
            UpbitAccount[] orderlist = objectMapper.readValue(jsonResponse, UpbitAccount[].class);
            
            for(UpbitAccount account : orderlist) {
            	//log.info("acccount.............조회.........{}",account);
            	if(currency.equals(account.getCurrency())){
            		return account;
            	}
            }

          
            
            
        } catch (IOException e) {
            e.printStackTrace();            
        }
   	
        return null;
	
	}
	
	public void orders(String market, String side, String volume, String price, String ord_type ) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		
		

        HashMap<String, String> params = new HashMap<>();
        params.put("market", market );
        params.put("side", side );
        params.put("volume", volume );
        params.put("price", price );
        params.put("ord_type", "limit");

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(serverUrl + "/v1/orders");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);
            request.setEntity(new StringEntity(new Gson().toJson(params)));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	

	
	
	public List<Account> findAll() {
		
		List<Account> list = accountRepository.findAll();
		
		return list;
	}
	
	
}

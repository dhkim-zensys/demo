package com.nice.demo.upbit.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nice.demo.upbit.model.UpbitAccount;
import com.nice.demo.upbit.model.UpbitOrderBook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UpbitService {
	
	@Autowired
	private AccountService accountService; 
	
	private boolean bTrade = false; 
	
	public void tradeStart(String currency, int count) throws URISyntaxException, ClientProtocolException, IOException {
		
		
		String market = "KRW-" + currency;
		
		
		URI uri = new URIBuilder("https://api.upbit.com/v1/orderbook")
				.addParameter("markets", market)
				.build();
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		
		bTrade = true;		
		
		for( int i = 0 ; i < count ; i++) {
			
			HttpResponse response;
			HttpEntity entity;
			String content;
			
			if( bTrade) {
				
				
				//https://api.upbit.com/v1/orderbook
				//호가 정보 조회
				response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
				entity = response.getEntity(); 
				content = EntityUtils.toString(entity); 
				
				log.info("content...{}",content);
				
				ObjectMapper objectMapper = new ObjectMapper();
				UpbitOrderBook[] orderlist = objectMapper.readValue(content, UpbitOrderBook[].class);
				
				UpbitOrderBook order = orderlist[0];
				

				Double ask_price = order.getOrderbook_units().get(0).getAsk_price();	//파는 가격	        
				Double bid_price = order.getOrderbook_units().get(0).getBid_price();	//사는 가격	
				
				log.info("ask_price.......{}    bid_price....{}", ask_price, bid_price);
				
				//매수하다 10000원
				//orders(String market, String side, String volume, String price, String ord_type )
				//bid 매수, ask 매도   limit : 지정가 주문
				double amount = 10000L;
				String str_bid_volume = String.format("%.8f",amount/bid_price);
				String str_bid_price = String.format("%.0f", bid_price);
				//String.format("%,.3f", money)
				log.info("order bid.......price{}    volume....{} {}", str_bid_price, str_bid_volume, amount/bid_price);
				
				
				try {
					accountService.orders(market, "bid", str_bid_volume, str_bid_price, "limit");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				
				UpbitAccount account = null;
				//자산조회 있으면 진행
				while(true&&bTrade) {
					
					account = accountService.accounts(currency);
					if( account != null) {
						log.info("account.............{}", account);
						break;
					}
					log.info("account.........아직 없어{} 0.2초 대기...", currency);
					
					try {
						Thread.sleep(200); //0.2초 대기
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				
				//매도
				if(account != null) {
					
					
					//호가정보 다시 구하기
					//https://api.upbit.com/v1/orderbook
					//호가 정보 조회
					response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
					entity = response.getEntity(); 
					content = EntityUtils.toString(entity); 
					
					objectMapper = new ObjectMapper();
					orderlist = objectMapper.readValue(content, UpbitOrderBook[].class);
					
					order = orderlist[0];
					
					double ask_price1 = order.getOrderbook_units().get(0).getAsk_price();	//파는 가격
					
					if( ask_price1 > ask_price ) ask_price = ask_price1;		//현재 파는 가격이 높으면 change
					
					
					String str_ask_volume = String.valueOf(account.getBalance());
					String str_ask_price = String.format("%.0f", ask_price);
					//bid 매수, ask 매도   limit : 지정가 주문
					try {
						log.info("order ask.......price{}    volume....{}", str_ask_price, str_ask_volume);
						accountService.orders(market, "ask", str_ask_volume, str_ask_price, "limit");
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					
					
					//자산조회 없으면 진행
					while(true&&bTrade) {						
						account = accountService.accounts(currency);
						if( account == null) {
							log.info("account.............없음");
							break;
						}
						
						log.info("account.........있어{} 0.4초 대기...", currency);
						
						try {
							Thread.sleep(400); //0.4초 대기
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
				
			}else {
				log.info("bTrade...{}",bTrade);
				break;
			}
			
			
			try {
				Thread.sleep(1000); //1초 대기
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return;
	}
	
	public void tradeEnd(String currency) {
		
		bTrade = false;
		
		
		return;
	}
	
		
	
}

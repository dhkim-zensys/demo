package com.nice.demo.upbit.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nice.demo.upbit.model.UpbitAccount;
import com.nice.demo.upbit.model.UpbitMarketData;
import com.nice.demo.upbit.model.UpbitOrderBook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UpbitService {
	
	@Autowired
	private AccountService accountService; 
	
	@Autowired
	private MarketService marketService;
	
	@Autowired
	private NotificationManager telegram;
	
	private boolean bTrade = false; 
	
	@Scheduled(cron="0 0 12 * * *") 
	public void moveTradeStart() throws URISyntaxException, ClientProtocolException, IOException {
		
		moveTradeStart("BTC");		//1
		moveTradeStart("XRP");		//2
		moveTradeStart("DAWN");		//3
		moveTradeStart("BTT");		//4
		moveTradeStart("DOGE");		//5
		moveTradeStart("STRK");		//6
		moveTradeStart("VET");		//7
		moveTradeStart("HIVE");		//8
		moveTradeStart("SRM");		//9
		moveTradeStart("ETC");		//10
	}
	
	/**
	 *  매일 12시 시작
	 *  
	 *  1. 전일 하루 변동을 체크한다.
	 *  2. 상승장이여야 한다.
	 *  3. 목표가를 정한다.
	 *  4. 1분마다 가격을 정하고 정해진 목표가에 도달시 매수한다.
	 *  5. 자산조회를 한다.
	 *  6. 잘 매수했으면 텔레그램 메시지를 보낸다.
	 *  
	 *  7. 해당 자산이 있으면 모두 매도한다.
	 *  
	 *  
	 * @param currency
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	public void moveTradeStart(String currency) throws URISyntaxException, ClientProtocolException, IOException {
		
		
		String market = "KRW-" + currency;
		
		
		bTrade = true;		
		
		
		//2. 전일 변동량을 체크한다.
		//https://api.upbit.com/v1/candles/minutes/60?market=KRW-BTC&count=24
		UpbitMarketData hours_24 = marketService.getHours(currency);		//24시간 전에 데이타 summary
		
		double trade_price = hours_24.getTrade_price();		//현재가
		
		log.info("[{}] 현재가.......{}", currency, String.format("%.8f", trade_price));
		
		//3. 상승장인지 체크한다.
		double trade_price_5days_avg = marketService.get5DaysAvg(currency);
		
		log.info("[{}] 5일 평균가.......{}", currency, String.format("%.8f", trade_price_5days_avg));
		
		
		//현재 시간이 낮 11시 50분이면 종료
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		Date startTime = new Date();
		
		Calendar cal = Calendar.getInstance();

		cal.setTime(startTime);
		log.info("[{}] 현재시각..............{}",currency, format1.format(cal.getTime()));
		
		//cal.setTime(date);

		cal.add(Calendar.HOUR, 23 );
		cal.add(Calendar.MINUTE, 50);
		
		String endTime = format1.format(cal.getTime()); 
		
		log.info("[{}] 종료예정시각..............{}",currency, endTime );
		
		//목표가 설정
		double low_price = hours_24.getLow_price();				//최저가
		double high_price = hours_24.getHigh_price();			//최고가
		double opening_price = hours_24.getOpening_price();		//시가
		
		double target_price = opening_price + ( high_price - low_price ) * 0.5;
		
		
		log.info("[{}] 매수목표가.......{}, 전일 차이 {}", currency, String.format("%.8f", target_price), String.format("%.8f", high_price - low_price));
		
		double buy_price = 0L;
		double sell_price = 0L;
		
		telegram.sendPrivate("["+currency+"] 변동성 돌파 매수 목표가 감시 시작.... 타겟금액:" + String.format("%.8f", target_price) + ", 현재가:"+ String.format("%.8f", opening_price) );
		
		while(true) {
			
			Date time = new Date();
			
			String now = format1.format(time);
			log.info(now);
			
			//목표가에 도달하는지 체크
			UpbitOrderBook order = marketService.getNowData(currency);
			Double ask_price = order.getOrderbook_units().get(0).getAsk_price();	//파는 가격	        
			Double bid_price = order.getOrderbook_units().get(0).getBid_price();	//사는 가격	
			
			log.info("[{}] target_price {}, ask_price {}, bid_price {}", currency, String.format("%.8f", target_price), String.format("%.8f", ask_price), String.format("%.8f", bid_price));
		
			
			if( time.compareTo(cal.getTime())>=0) {		//현재시간이 시작시간 + 23시 50분 보다 크면 종료
				
				
				//현재가
				
				
				//매수금액
				if(buy_price > 0  ) {
					log.info("[{}] 변동성 돌파 종료 ... 매도{} 수익률{}",currency, String.format("%.8f", sell_price), String.format("%.8f", sell_price/buy_price*100) );
					telegram.sendPrivate("["+currency +"] 변동성 돌파 매도{"+String.format("%.8f", sell_price)+"} 수익률{" + String.format("%.8f", sell_price/buy_price*100) +"}");
					
				}else {
					log.info("[{}] 변동성 돌파 종료 ... 매수매도 없음",currency );
					telegram.sendPrivate("["+currency +"] 변동성 돌파 종료..매수매도 없음");
				}

				
				break;
			}
			
			
			//목표가 매수
			if(  bid_price >  trade_price_5days_avg) {			//현재가격이 5일 평균가보다 높고
				
				if(bid_price >= target_price ) {		//현재가가격이 목표매수가보다 높다면 매수
					
					buy_price = bid_price;
					
					log.info("[{}] 변동성 돌파 매수{}",currency, String.format("%.8f", buy_price) );
					
					telegram.sendPrivate("["+currency+"] 변동성 돌파 매수" + String.format("%.8f", buy_price) );
					
					
				}
				
			}
			
			
			
			try {
				Thread.sleep(60*1000); //1분 대기
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		return;
	}
	
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

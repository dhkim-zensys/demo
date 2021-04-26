package com.nice.demo.upbit.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nice.demo.upbit.model.UpbitMarketData;
import com.nice.demo.upbit.model.UpbitOrderBook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MarketService {
	
	public UpbitMarketData getHours(String currency) throws URISyntaxException, ClientProtocolException, IOException{
		
		String market = "KRW-" + currency;
		
		
		URI uri = new URIBuilder("https://api.upbit.com/v1/candles/minutes/60")
				.addParameter("market", market)
				.addParameter("count", "26")
				.build();
		
		log.info("uri..............{}",uri);
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		
		HttpResponse response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
		HttpEntity entity = response.getEntity(); 
		String content = EntityUtils.toString(entity);
		
		log.info("content...{}",content);
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		UpbitMarketData[] list = objectMapper.readValue(content, UpbitMarketData[].class);
		
		UpbitMarketData rtnData = list[0];		// 현재 데이타 
		
		rtnData.setHigh_price(list[1].getHigh_price());	// 24시간데이터
		rtnData.setLow_price(list[1].getLow_price());	//24시간데이터
		
		for(int i = 1 ; i < list.length ; i++ ) {		//24번 반복
			
			UpbitMarketData minuteData = list[i];
			
			//high_price
			Double high_price = minuteData.getHigh_price();
			if( high_price > rtnData.getHigh_price()  ) rtnData.setHigh_price(high_price);
			
			//low_price
			Double low_price = minuteData.getLow_price();
			if(low_price < rtnData.getLow_price() ) rtnData.setLow_price(low_price);
			
		}
		
		return rtnData; 
	}
	
	public double get5DaysAvg(String currency) throws URISyntaxException, ClientProtocolException, IOException{
		
		String market = "KRW-" + currency;
		
		//https://api.upbit.com/v1/candles/days?count=5&market=KRW-BTC
		URI uri = new URIBuilder("https://api.upbit.com/v1/candles/days")
				.addParameter("market", market)
				.addParameter("count", "6")
				.build();
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		
		HttpResponse response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
		HttpEntity entity = response.getEntity(); 
		String content = EntityUtils.toString(entity);
		
		log.info("content...{}",content);
		
		
		ObjectMapper objectMapper = new ObjectMapper();
		UpbitMarketData[] list = objectMapper.readValue(content, UpbitMarketData[].class);
		
		//UpbitMarketData rtnData = list[0];		// 현재 데이타 
		
		double sum_trade_price = 0L;
		
		for(int i = 1 ; i < list.length ; i++ ) {		//5번 반복
			
			UpbitMarketData daysData = list[i];
			
			log.info("5일 종가......{}{}",daysData.getCandle_date_time_kst(),String.format("%.8f", daysData.getTrade_price()));
			
			sum_trade_price += daysData.getTrade_price();
			
		}
		
		return  sum_trade_price /5 ;

	}
	
	
	public UpbitOrderBook getNowData(String currency) throws URISyntaxException, ClientProtocolException, IOException{
		
		String market = "KRW-" + currency;
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		
		URI uri = new URIBuilder("https://api.upbit.com/v1/orderbook")
				.addParameter("markets", market)
				.build();
		
		//https://api.upbit.com/v1/orderbook
		//호가 정보 조회
		HttpResponse response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
		HttpEntity entity = response.getEntity(); 
		String content = EntityUtils.toString(entity); 
		
		log.info("content...{}",content);
		
		ObjectMapper objectMapper = new ObjectMapper();
		UpbitOrderBook[] orderlist = objectMapper.readValue(content, UpbitOrderBook[].class);
		
		return orderlist[0];
	}

}

package com.nice.demo.upbit.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UpbitSchedulerService {
	
	@Autowired
	private UpbitService upbitService; 
	
	@Scheduled(cron="0 1 12 * * *")
	public void moveTradeStart1() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("BTC");		//1		
	}
	
	@Scheduled(cron="0 2 12 * * *")
	public void moveTradeStart2() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("XRP");		//2
	}
	
	@Scheduled(cron="0 3 12 * * *")
	public void moveTradeStart3() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("DAWN");		//3
	}
	
	@Scheduled(cron="0 4 12 * * *")
	public void moveTradeStart4() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("BTT");		//4
	}
	
	@Scheduled(cron="0 5 12 * * *")
	public void moveTradeStart5() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("DOGE");		//5
	}
	
	
	@Scheduled(cron="0 6 12 * * *")
	public void moveTradeStart6() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("STRK");		//6
	}
	
	@Scheduled(cron="0 7 12 * * *")
	public void moveTradeStart7() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("VET");		//7
	}
	
	@Scheduled(cron="0 8 12 * * *")
	public void moveTradeStart8() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("HIVE");		//8
	}
	
	@Scheduled(cron="0 9 12 * * *")
	public void moveTradeStart9() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("SRM");		//9
	}
	
	@Scheduled(cron="0 10 12 * * *")
	public void moveTradeStart10() throws URISyntaxException, ClientProtocolException, IOException {
		
		upbitService.moveTradeStart("ETC");		//10
	}
	
	
		
	
}

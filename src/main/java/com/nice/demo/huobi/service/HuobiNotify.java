package com.nice.demo.huobi.service;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nice.demo.upbit.repository.NoticeRepository;
import com.nice.demo.upbit.service.NotificationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HuobiNotify {
	


	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NotificationManager telegram;
	
	@Scheduled(fixedRate = 1800000)
    public void cronJobSch() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      Date now = new Date();
      String strDate = sdf.format(now);
      System.out.println("Java cron job expression:: " + strDate);
      
      try {
		getNotify();
	  } catch (URISyntaxException | IOException e) {
	      // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
      
   }
	/**
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void getNotify() throws URISyntaxException, ClientProtocolException, IOException {


		URI uri = new URI("https://api-cloud.huobi.co.kr/market/depth"); 
		uri = new URIBuilder(uri)
				.addParameter("symbol", "usdtkrw")
				.addParameter("type", "step1")
				.build(); 
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		
		HttpResponse response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
		
		HttpEntity entity = response.getEntity(); 
		
		String content = EntityUtils.toString(entity); 
		
		//System.out.println(content);
		
		ObjectMapper mapper = new ObjectMapper(); 
		//String json = "{ \"name\" : \"mkgil\" , \"age\" : 25 }"; 
		Map<String, Object> map = new HashMap<>(); 
		map = mapper.readValue(content, new TypeReference<Map<String, Object>>(){}); 
		
		log.info(map.get("tick").toString());
		
		Map tick = (Map)map.get("tick");
		
		List bids = (List) tick.get("bids");
		List asks = (List) tick.get("asks");
		List asks1 = (List) asks.get(0);
		
		
		log.info("asks:{}", asks1.get(0));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    Date now = new Date();
	    String strDate = sdf.format(now);
	    
	    double usdtkrw = (Double)asks1.get(0);
	    double usd = 1115;
	    double rt = (double) ((usdtkrw-usd)/usd*100); 
	    

	      
	    log.info(String.format("%1$,.2f", rt));  
		telegram.sendPrivate(strDate +"\n" + "usdt/krw:"+ asks1.get(0) + "원,usd:1115원, 김프:"+String.format("%1$,.2f", rt)+"%");
		
		


	}	

}



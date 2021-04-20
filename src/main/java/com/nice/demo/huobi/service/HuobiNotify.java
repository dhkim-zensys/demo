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
	private NotificationManager telegram;
	
	@Autowired
	private HuobiService huobiService;
	
	
	@Scheduled(fixedRate = 1800000)
	//@Scheduled(fixedRate = 10000)
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

		
		
		
		//후오비 데더 가격 가져오기
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
	    
	    
	    //String usdstr = huobiService.findById("usd");
	    //double usd = Double.parseDouble(usdstr);
	    
	    
	    //환율 가져오기
	  	String usdkrw_rt_url = "https://quotation-api-cdn.dunamu.com/v1/forex/recent";
	  		
	    
	    uri = new URI(usdkrw_rt_url); 
		uri = new URIBuilder(uri)
				.addParameter("codes", "FRX.KRWUSD")
				.build();
		
		httpClient = HttpClientBuilder.create().build(); 
		response = httpClient.execute(new HttpGet(uri)); // post 요청은 HttpPost()를 사용하면 된다. 
		entity = response.getEntity(); 
		content = EntityUtils.toString(entity); 
		
		List list = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>(){});
		
		double d_usd = (double) ((Map<String,Object>) list.get(0)).get("basePrice");
		
		String str_usd = String.valueOf(((Map<String,Object>) list.get(0)).get("basePrice"));
		
		log.info(str_usd);
		
		double rt = (double) ((usdtkrw-d_usd)/d_usd*100); 
	      
	    log.info(String.format("%1$,.2f", rt));  
		telegram.sendPrivate(strDate +"\n" + "usdt/krw:"+ asks1.get(0) + "원,usd:"+ str_usd + "원, 김프:"+String.format("%1$,.2f", rt)+"%");
		
		


	}	

}



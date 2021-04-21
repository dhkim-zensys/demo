package com.nice.demo.upbit.service;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import com.nice.demo.upbit.model.Notice;
import com.nice.demo.upbit.repository.NoticeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UpbitNotify {
	


	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NotificationManager telegram;
	
	//@Scheduled(fixedRate = 30000)
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
	
	public void getNotify() throws URISyntaxException, ClientProtocolException, IOException {


		URI uri = new URI("https://api-manager.upbit.com/api/v1/notices"); 
		uri = new URIBuilder(uri)
				.addParameter("page", "1")
				.addParameter("per_page", "20")
				.addParameter("thread_name", "general")
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
		
		System.out.println(map.get("data"));
		
		Map<String,Object> data = (Map)map.get("data");
		
		List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
		
		Notice notice = new Notice();
		
		
		int maxId;
		try {
			maxId = Integer.parseInt(noticeRepository.selectMax());
		} catch (NumberFormatException e) {
			maxId = 1;
		}
		

		for( Map<String,Object> row : list ) {
			
			int id = (Integer)row.get("id");
			
			//텔레그램 통보
			//telegram.send((String)row.get("title"));
			
			if( id > maxId) {
				
				notice.setId(String.valueOf(row.get("id")));
				notice.setTitle((String)row.get("title"));
				notice.setCreated_at((String)row.get("created_at"));
				notice.setCreated_at((String)row.get("updated_at"));
				
				noticeRepository.save(notice);
				
//				LocalDate date = LocalDate.now(); // 2018-06-06
//				LocalTime time = LocalTime.now(); // 02:34:50.223
//				LocalDateTime dateTime = LocalDateTime.now(); // 2018-06-06T02:34:50.223
//				ZonedDateTime dateTimeInKr = ZonedDateTime.now(); // 2018-06-06T02:34:50.223+09:00[Asia/Seoul]

				ZonedDateTime createTime = ZonedDateTime.parse((String)row.get("created_at"));
				
				DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss z");
				String formattedString2 = createTime.format(formatter2);
				
				log.info( formattedString2 + "\n" + (String)row.get("title") + "\n" + "https://www.upbit.com/service_center/notice?id="+String.valueOf(row.get("id")));
				
				//텔레그램 통보
				telegram.send(formattedString2 +"\n" + (String)row.get("title") + "\n" + "https://www.upbit.com/service_center/notice?id="+String.valueOf(row.get("id")));
			}
			
			
		}

	}	

}


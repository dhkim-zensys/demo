package com.nice.demo.upbit.model;

import lombok.Data;

@Data
public class UpbitMarketData {
	
	private String market;
	
	private String candle_date_time_utc;
	
	private String candle_date_time_kst;
	
	private Double opening_price;
	
	private Double high_price;
	
	private Double low_price;
	
	private Double trade_price;
	
	private String timestamp;
	
	
	private Double candle_acc_trade_price;
	
	private Double candle_acc_trade_volume;
	
	private Double prev_closing_price;
	
	private Double change_price;
	
	//change_price	전일 종가 대비 변화 금액	Double
	//change_rate	전일 종가 대비 변화량	Double
	//converted_trade_price	종가 환산 화폐 단위로 환산된 가격(요청에 convertingPriceUnit 파라미터 없을 시 해당 필드 포함되지 않음.)	Double
	
	private Double change_rate;
	
	private Double converted_trade_price;
	
	private int unit;

}

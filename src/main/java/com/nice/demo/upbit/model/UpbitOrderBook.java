package com.nice.demo.upbit.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class UpbitOrderBook {

    @SerializedName("market")
    private String market;

    @SerializedName("timestamp")
    private Long timestamp;

    @SerializedName(value = "total_ask_size")
    private Double total_ask_size;

    @SerializedName("total_bid_size")
    private Double total_bid_size;

    @SerializedName("orderbook_units")
    private List<UpbitOrderbookItem> orderbook_units;

    private String stream_type;
}

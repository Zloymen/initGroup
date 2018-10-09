package com.initgrupp.data;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private LocalDateTime dateTime;
    private Integer orderNumber;
    private String customerName;
    private List<Item> items;
}

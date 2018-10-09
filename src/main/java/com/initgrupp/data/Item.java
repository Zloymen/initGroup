package com.initgrupp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String name;
    private Integer price;
    private BigDecimal quantity;
    private Integer sum;
}

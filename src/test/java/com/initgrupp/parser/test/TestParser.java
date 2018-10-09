package com.initgrupp.parser.test;

import com.initgrupp.data.Item;
import com.initgrupp.data.Order;
import com.initgrupp.error.UnknownTagException;
import com.initgrupp.parser.ParserTLV;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class TestParser extends Assert {

    private  byte[] sources = {(byte)0x01, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0xA8, (byte)0x32, (byte)0x92, (byte)0x56,
            (byte)0x02, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x04, (byte)0x71, (byte)0x02, (byte)0x03, (byte)0x00,
            (byte)0x0B, (byte)0x00, (byte)0x8E, (byte)0x8E, (byte)0x8E, (byte)0x20, (byte)0x90, (byte)0xAE, (byte)0xAC,
            (byte)0xA0, (byte)0xE8, (byte)0xAA, (byte)0xA0, (byte)0x04, (byte)0x00, (byte)0x1D, (byte)0x00, (byte)0x0B,
            (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x84, (byte)0xEB, (byte)0xE0, (byte)0xAE, (byte)0xAA, (byte)0xAE,
            (byte)0xAB, (byte)0x0C, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x20, (byte)0x4E, (byte)0x0D, (byte)0x00,
            (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x0E, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x40,
            (byte)0x9C};

    private Order order;
    private ParserTLV parserTLV = new ParserTLV();


    @Before
    public void before(){
        order = new Order();
        order.setOrderNumber(160004);
        order.setCustomerName("ООО Ромашка");
        order.setDateTime(LocalDateTime.of(2016,1,10,10,30));
        Item item = new Item("Дырокол", 20000, BigDecimal.valueOf(2), 40000);
        order.setItems(new ArrayList<>());
        order.getItems().add(item);
    }

    @Test
    public void testToConverter(){

        List<Order> orders = parserTLV.parserOrder(sources);
        assertEquals(orders.size(), 1);
        assertEquals(orders.get(0), order);
    }

    @Test
    public void testToManyConverter(){
        List<Order> orders = parserTLV.parserOrder(ArrayUtils.addAll(sources, sources));
        assertEquals(orders.size(), 2);
        assertEquals(orders.get(0), order);
        assertEquals(orders.get(1), order);
    }

    @Test(expected = UnknownTagException.class)
    public void testToUnknownTagException() {
        byte[] badSources = Arrays.copyOf(sources, sources.length);
        badSources[0] = (byte)0x05;
        parserTLV.parserOrder(badSources);
    }

}

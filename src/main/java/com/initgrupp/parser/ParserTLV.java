package com.initgrupp.parser;

import com.initgrupp.data.Item;
import com.initgrupp.data.Order;
import com.initgrupp.error.BadTLVstructureException;
import com.initgrupp.error.UnknownTagException;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserTLV {

    private static final ByteBuffer BUFFER_INTEGER = ByteBuffer.allocate(Integer.BYTES);
    private static final Charset TO_CHARSET = Charset.forName("cp866");

    public List<Order> parserOrder(byte[] buffer){
        int i = 0;
        List<Order> orders = new ArrayList<>();
        Order order = null;
        while( i < buffer.length){
            TLVItem item = getTLV(i, buffer);
            if(item.getTag() == 1){
                order = new Order();
                orders.add(order);
            }
            if(order == null) throw new BadTLVstructureException();
            fillOrder(order, item);
            i = i + 4 + item.getLength();
        }
        return orders;
    }

    private int bytesToInt(byte[] bytes) {
        if(4 - bytes.length > 0){
            byte[] n = new byte[4 - bytes.length];
            Arrays.fill(n, (byte)0x0);
            bytes = ArrayUtils.addAll(n, bytes);
        }
        BUFFER_INTEGER.put(bytes, 0, bytes.length);
        BUFFER_INTEGER.flip();
        return BUFFER_INTEGER.getInt();
    }

    private String bytesToString(byte[] bytes) {
        return new String(bytes, TO_CHARSET);
    }

    private TLVItem getTLV(int i, byte[] buffer){
        TLVItem item = new TLVItem();
        byte[] byteTag = Arrays.copyOfRange(buffer, i, i + 2);
        i += 2;
        byte[] byteLenght = Arrays.copyOfRange(buffer, i, i + 2);
        i += 2;
        item.setTag(convertInt(byteTag));
        int size = convertInt(byteLenght);
        item.setLength(size);
        item.setValue(Arrays.copyOfRange(buffer, i, i + size));
        return item;
    }

    private void fillOrder(Order order, TLVItem item){

        switch (item.getTag()){
            case 1:
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(convertInt(item.getValue()) * 1000L), ZoneId.of("UTC"));
                order.setDateTime(date);
                break;
            case 2:
                order.setOrderNumber(convertInt(item.getValue()));
                break;
            case 3:
                order.setCustomerName(bytesToString(item.getValue()));
                break;
            case 4:
                parseItems(order, item.getValue());
                break;
            default:
                throw new UnknownTagException();
        }
    }

    private void fillItem(Item item, TLVItem tlvItem){

        switch (tlvItem.getTag()){
            case 11:
                item.setName(bytesToString(tlvItem.getValue()));
                break;
            case 12:
                item.setPrice(convertInt(tlvItem.getValue()));
                break;
            case 13:
                byte [] value = tlvItem.getValue();
                byte prec = value[0];
                value = ArrayUtils.remove(value, 0);
                item.setQuantity(convertBigDecimal(value, prec & 0xFF ));
                break;
            case 14:
                item.setSum(convertInt(tlvItem.getValue()));
                break;
            default:
                throw new UnknownTagException();
        }
    }

    private void parseItems(Order order, byte[] byteValue){
        List<Item> items = new ArrayList<>();
        order.setItems(items);
        Item item = null;
        int i = 0;
        while(i < byteValue.length){
            TLVItem tlvItem = getTLV(i, byteValue);
            if(tlvItem.getTag() == 11){
                item = new Item();
                items.add(item);
            }
            if(item == null) throw new BadTLVstructureException();
            fillItem(item, tlvItem);
            i = i + 4 + tlvItem.getLength();
        }
    }


    private int convertInt(byte[] rno){
        if(4 - rno.length > 0){
            byte[] n = new byte[4 - rno.length];
            Arrays.fill(n, (byte)0x0);
            rno = ArrayUtils.addAll(rno, n);
        }
        return (rno[3]<<24)&0xff000000| (rno[2]<<16)&0x00ff0000| (rno[1]<< 8)&0x0000ff00| (rno[0])&0x000000ff;
    }

    private BigDecimal convertBigDecimal(byte[] rno, int prec){
        if(4 - rno.length > 0){
            byte[] n = new byte[4 - rno.length];
            Arrays.fill(n, (byte)0x0);
            rno = ArrayUtils.addAll(rno, n);
        }
        float value = (rno[3]<<24)&0xff000000| (rno[2]<<16)&0x00ff0000| (rno[1]<< 8)&0x0000ff00| (rno[0])&0x000000ff;
        return BigDecimal.valueOf(value).setScale(prec);
    }

}

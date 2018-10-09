package com.initgrupp.parser;

import lombok.Data;

@Data
class TLVItem {
    /**
     * tag - 2 байта, little endian, определяет тип поля;
     */
    private Integer tag;
    /**
     * - length - 2 байта, little endian, определяет длину значения в байтах;
     */
    private Integer length;
    /**
     * - value - length байт, тип данных зависит от тега.
     */
    private byte[] value;
}

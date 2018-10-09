package com.initgrupp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.initgrupp.data.Order;
import com.initgrupp.error.ParamNotFoundException;
import com.initgrupp.parser.ParserTLV;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
public class Converter {

    public static void main(String[] args) throws Exception {

        //createSourceFile();
        if(args.length == 0 || StringUtils.isAnyEmpty(args[0], args[1])) throw new ParamNotFoundException();
        byte[] buffer = readSourceFile(args[0]);
        ParserTLV parserTLV = new ParserTLV();
        List<Order> orders = parserTLV.parserOrder(buffer);
        orders.forEach(item -> log.info(item.toString()));

        writeJSON(orders, args[1]);

    }

    public static ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    public static void writeJSON(List<Order> orders, String filename ){
        ObjectMapper mapper = serializingObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), orders);
            //mapper.writeValue(new File(filename), orders);
        } catch (IOException e) {
            log.error("error write json", e);

        }
    }

    public static byte[] readSourceFile(String file) throws IOException {

        try(FileInputStream fin=new FileInputStream(file)){
            byte[] buffer = new byte[fin.available()];

            fin.read(buffer, 0, fin.available());

            return buffer;
        }
        catch(IOException ex){

            log.error("Error write:", ex.getMessage());
            throw ex;
        }
    }

    public static void createSourceFile(){
        byte[] sources = {(byte)0x01, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0xA8, (byte)0x32, (byte)0x92, (byte)0x56,
                (byte)0x02, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x04, (byte)0x71, (byte)0x02, (byte)0x03, (byte)0x00,
                (byte)0x0B, (byte)0x00, (byte)0x8E, (byte)0x8E, (byte)0x8E, (byte)0x20, (byte)0x90, (byte)0xAE, (byte)0xAC,
                (byte)0xA0, (byte)0xE8, (byte)0xAA, (byte)0xA0, (byte)0x04, (byte)0x00, (byte)0x1D, (byte)0x00, (byte)0x0B,
                (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x84, (byte)0xEB, (byte)0xE0, (byte)0xAE, (byte)0xAA, (byte)0xAE,
                (byte)0xAB, (byte)0x0C, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x20, (byte)0x4E, (byte)0x0D, (byte)0x00,
                (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x0E, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x40,
                (byte)0x9C};

        try(FileOutputStream fos = new FileOutputStream("sources.io")) {

            fos.write(sources, 0, sources.length );
        } catch(IOException ex){

            log.error("Error write:", ex.getMessage());
        }
        log.info("The file has been written");
    }
}

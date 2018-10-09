package com.initgrupp.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.initgrupp.Converter;
import com.initgrupp.data.Order;
import com.initgrupp.parser.ParserTLV;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Slf4j
public class ParserProcessor implements Processor {

    private ObjectMapper mapper = Converter.serializingObjectMapper();
    private ParserTLV parserTLV = new ParserTLV();

    @Override
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        File fileToProcess = message.getBody(File.class);

        String inFileName = FilenameUtils.getBaseName(fileToProcess.getName());

        try(FileInputStream fin = new FileInputStream(fileToProcess)){
            byte[] buffer = new byte[fin.available()];

            fin.read(buffer, 0, fin.available());

            List<Order> orders = parserTLV.parserOrder(buffer);

            exchange.getOut().setBody(mapper.writerWithDefaultPrettyPrinter().writeValueAsString( orders));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
            exchange.getOut().setHeader("CamelFileName", String.format("output_%s.json", inFileName));
        }

    }
}

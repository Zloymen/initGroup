package com.initgrupp;

import com.initgrupp.camel.ParserProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ConverterWithCamel {

    public static void main(String args[]) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			public void configure() {
				from("file:data/inbox?noop=true").process(new ParserProcessor()).to("file:data/outbox");
			}
		});
		context.start();
		Thread.sleep(10000);
		context.stop();
    }
}

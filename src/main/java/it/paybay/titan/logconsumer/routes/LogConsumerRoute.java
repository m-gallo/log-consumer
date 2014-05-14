package it.paybay.titan.logconsumer.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;


public class LogConsumerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("activemq:queue:{{logs.queue}}")
		.marshal().json(JsonLibrary.Jackson)
		.convertBodyTo(String.class)
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				String jsonString = exchange.getIn().getBody(String.class);
				
				exchange.getIn().setBody(jsonString.replace(".", "-"));
				
			}
		})
		.to("log:input")
		.to("mongodb:mongoClient?database={{mongodb.database}}&createCollection=true&collection={{mongodb.collection}}&operation=insert");
	}

}

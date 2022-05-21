package com.cub.eai.route;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelSagaRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		from("timer:clock?period=5s")
        .saga()
            .setHeader("id", header(Exchange.TIMER_COUNTER))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .log("Executing saga #${header.id}")
            .multicast()
            	.to("http4://localhost:8081/api/train/buy/seat")
            	.to("http4://localhost:8082/api/flight/buy");
	}

}

package com.cub.eai.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class CamelSagaPaymentRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		rest().post("/pay")
        	.param().type(RestParamType.query).name("type").required(true).endParam()
        	.param().type(RestParamType.header).name("id").required(true).endParam()
        	.route()
        	.saga()
            	.propagation(SagaPropagation.MANDATORY)
            	.option("id", header("id"))
            	.compensation("direct:cancelPayment")
            .log("Paying ${header.type} for order #${header.id}")
            .choice()
            	.when(x -> Math.random() >= 0.85)
                .throwException(new RuntimeException("Random failure during payment"))
            .end();

		from("direct:cancelPayment")
        	.log("Payment #${header.id} has been cancelled");
	}

}

package com.cub.eai.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class CamelSagaFlightRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		restConfiguration().port(8383);

        rest().post("/flight/buy")
                .param().type(RestParamType.header).name("id").required(true).endParam()
                .route()
                .saga()
                    .propagation(SagaPropagation.MANDATORY)
                    .option("id", header("id"))
                    .compensation("direct:cancelPurchase")
                .log("Buying flight #${header.id}")
                .to("http4://localhost:8080/api/pay?bridgeEndpoint=true&type=flight")
                .log("Payment for flight #${header.id} done");

        from("direct:cancelPurchase")
                .log("Flight purchase #${header.id} has been cancelled");
	}

}

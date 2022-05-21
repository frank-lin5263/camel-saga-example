package com.cub.eai.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class CamelSagaTrainRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		rest().post("/train/buy/seat")
			.param().type(RestParamType.header).name("id").required(true).endParam()
			.route()
			.saga()
				.propagation(SagaPropagation.SUPPORTS)
				.option("id", header("id"))
				.compensation("direct:cancelPurchase")
			.log("Buying train seat #${header.id}")
			.to("http4://localhost:8080/api/pay?bridgeEndpoint=true&type=train")
			.log("Payment for train #${header.id} done");

		from("direct:cancelPurchase")
			.log("Train purchase #${header.id} has been cancelled");
	}

}

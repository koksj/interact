package com.rabbitmq.jca.inflow;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;

public class MessageConnection {

	private MessageEndpointFactory endpointFactory;

	public MessageConnection(MessageEndpointFactory endpointFactory) {
		this.endpointFactory = endpointFactory;
	}

	public RabbitMqMessageListener getRabbitMqMessageListener() {

		RabbitMqMessageListener rabbitMqMessageListener = null;
		
		try {
			MessageEndpoint messageEndpoint = endpointFactory.createEndpoint(null);
			
			if(messageEndpoint instanceof RabbitMqMessageListener ) {
				rabbitMqMessageListener = (RabbitMqMessageListener) messageEndpoint;
			}
			
		} catch (UnavailableException e) {
		
			e.printStackTrace();
		}
		
		return rabbitMqMessageListener;
		
	}
}

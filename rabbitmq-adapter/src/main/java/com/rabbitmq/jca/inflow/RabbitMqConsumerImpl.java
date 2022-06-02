package com.rabbitmq.jca.inflow;



import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.resource.spi.endpoint.MessageEndpointFactory;

import org.jboss.logging.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMqConsumerImpl extends EndPoint implements Consumer {

	private final static Logger logger = Logger.getLogger(RabbitMqConsumerImpl.class);
	private MessageEndpointFactory endpointFactory;

	public RabbitMqConsumerImpl(MessageEndpointFactory endpointFactory, String userName, String password,
			String hostName, int portNumber, String queueName, int instance) {
		super(queueName, userName, password, hostName, portNumber, instance);
		this.endpointFactory = endpointFactory;
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		logger.debug("Consumer " + consumerTag + " registered");
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		logger.debug("Consumer " + consumerTag + " handleCancelOk");
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		logger.debug("Consumer " + consumerTag + " handleCancel");
	}

	@Override
	public void handleDelivery(String string, Envelope envelope, BasicProperties properties, byte[] body) {
		MessageConnection messageConnection = new MessageConnection(endpointFactory);
		RabbitMqMessageListener rabbitMqMessageListener = messageConnection.getRabbitMqMessageListener();
		logger.debug("Received message on instance: " + getInstance());
		try {
			if (channel.isOpen()) {
				rabbitMqMessageListener.onMessage(new String(body, "UTF-8"));
				// channel.basicAck(envelope.getDeliveryTag(), false);
			} else {
				logger.info("Channel closed, message not acknowledged. instance: " + getInstance());
			}
		} catch (AlreadyClosedException e) {
			logger.error("AlreadyClosedException, message not acknowledged on instance: " + getInstance() + " " + e);
			logger.error("AlreadyClosedException caught :" + e);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException caught: " + e);
		} catch (IOException e) {
			logger.error("IOException caught: " + e);
		}

	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

		logger.debug("Consumer " + consumerTag + "shutdown reason " + sig.getReason());
		logger.debug("Consumer " + consumerTag + " handleShutdownSignal");
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		logger.debug("Consumer " + consumerTag + " handleRecoverOk");
	}

	@Override
	public void release() {

	}

	@Override
	public void run() {
		try {
			channel.basicConsume(getQueueName(), true, this);
		} catch (IOException e1) {
			logger.error("IOException: " + e1);
		}

	}

}

package com.rabbitmq.jca;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqPublisher {

	/** The logger */
	private final static Logger logger = Logger.getLogger(RabbitMqPublisher.class);
	
	private String rabbitHost;

	private int portNumber;

	private String username;

	private String password;

	private String queueName;

	private Connection connection;
	private ConnectionFactory factory;
	private Channel channel;

	public RabbitMqPublisher() {

	}

	public String getRabbitHost() {
		return rabbitHost;
	}

	public void setRabbitHost(String rabbitHost) {
		this.rabbitHost = rabbitHost;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void init() {

		try {
			factory = new ConnectionFactory();
			factory.setHost(rabbitHost);
			factory.setVirtualHost("/");
			factory.setUsername(username);
			factory.setPassword(password);
			factory.setPort(portNumber);
			connection = factory.newConnection();
			factory.setAutomaticRecoveryEnabled(true);
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
		} catch (IOException e) {
			logger.error("Init IOException: ", e);
		} catch (TimeoutException e) {
			logger.error("Init IOException: ", e);
		}

	}

	public void sendMessage(byte[] message) throws IOException, TimeoutException {

		channel.basicPublish("", queueName, null, message);
	}

	public void close() {

		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				logger.error("IOException: ", e);
			} catch (TimeoutException e) {
				logger.error("TimeoutException: ", e);
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (IOException e) {
					logger.error("IOException: ", e);
				}
			}
		}

	}

}

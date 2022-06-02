package com.rabbitmq.jca.inflow;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jboss.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class EndPoint implements RabbitMqConsumer {

	private final static Logger logger = Logger.getLogger(EndPoint.class);
	
	private String userName;
	private String password;
	private String hostName;
	private int portNumber;
	private String queueName;
	private ConnectionFactory factory;
	protected Channel channel;
	protected Connection connection;
	private int instance;

	public EndPoint(String queueName, String userName, String password, String hostName, int portNumber,int instance) {

		this.userName = userName;
		this.password = password;
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.queueName = queueName;
		this.instance = instance;
		
		start();

	}

	public void start() {

		// Create a connection factory
		factory = new ConnectionFactory();

		factory.setHost(hostName);
		factory.setVirtualHost("/");
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setPort(portNumber);

		factory.setAutomaticRecoveryEnabled(true);
		factory.setTopologyRecoveryEnabled(true);
		factory.setNetworkRecoveryInterval(10000);	
		factory.setRequestedHeartbeat(60);

		// getting a connection
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.basicQos(10);
			channel.queueDeclare(queueName, true, false, false, null);			
		} catch (IOException e) {
			logger.debug("IOException" + e);
		} catch (TimeoutException e) {
			logger.debug("TimeoutException" + e);
		}

	}

	/**
	 * Close channel and connection. Not necessary as it happens implicitly any way.
	 * 
	 * @throws IOException
	 */
	public void close()  {

		try {
			this.channel.close();
		} catch (TimeoutException e) {
			logger.error("Error TimeoutException closing failed  channel: ",e);  
		} catch (IOException e) {
			logger.error("Error IOException closing failed channel: ",e);  
		}

		try {
			this.connection.close();
		} catch (IOException e) {
			logger.error("Error IOException closing failed connection: ",e);  
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getQueueName() {
		System.out.print("queueName: " + queueName);
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

}

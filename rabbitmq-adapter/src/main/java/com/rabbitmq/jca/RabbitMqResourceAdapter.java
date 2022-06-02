/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.rabbitmq.jca;

import java.util.concurrent.ConcurrentHashMap;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.jboss.logging.Logger;

import com.rabbitmq.jca.inflow.RabbitMqActivation;
import com.rabbitmq.jca.inflow.RabbitMqActivationSpec;
import com.rabbitmq.jca.inflow.RabbitMqConsumer;
import com.rabbitmq.jca.inflow.RabbitMqConsumerImpl;

/**
 * RabbitMqResourceAdapter
 *
 * @version $Revision: $
 */
@Connector(reauthenticationSupport = false, transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction)
public class RabbitMqResourceAdapter implements ResourceAdapter, java.io.Serializable {

	/** The serial version UID */
	private static final long serialVersionUID = 1L;
	/** The logger */
	private final static Logger logger = Logger.getLogger(RabbitMqResourceAdapter.class);

	private RabbitMqConsumer rabbitMqConsumer;
	private WorkManager workManager;

	/** The activations by activation spec */
	private ConcurrentHashMap<RabbitMqActivationSpec, RabbitMqActivation> activations;

	/** rabbitHost */
	@ConfigProperty(defaultValue = "127.0.0.1")
	private String rabbitHost;

	/** username */
	@ConfigProperty(defaultValue = "guest")
	private String username;

	/** password */
	@ConfigProperty(defaultValue = "guest")
	private String password;

	@ConfigProperty(defaultValue = "5672")
	private int portNumber;

	@ConfigProperty(defaultValue = "queue")
	private String queueName;

	@ConfigProperty(defaultValue = "16")
	private int numberRabbitMqConsumers;

	/**
	 * Default constructor
	 */
	public RabbitMqResourceAdapter() {
		this.activations = new ConcurrentHashMap<RabbitMqActivationSpec, RabbitMqActivation>();
	}

	/**
	 * Set rabbitHost
	 * 
	 * @param rabbitHost The value
	 */
	public void setRabbitHost(String rabbitHost) {
		this.rabbitHost = rabbitHost;
	}

	/**
	 * Get rabbitHost
	 * 
	 * @return The value
	 */
	public String getRabbitHost() {
		return rabbitHost;
	}

	/**
	 * Set username
	 * 
	 * @param username The value
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Get username
	 * 
	 * @return The value
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Set password
	 * 
	 * @param password The value
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get password
	 * 
	 * @return The value
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * This is called during the activation of a message endpoint.
	 *
	 * @param endpointFactory A message endpoint factory instance.
	 * @param spec            An activation spec JavaBean instance.
	 * @throws ResourceException generic exception
	 */
	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
			throws ResourceException {

		RabbitMqActivation activation = new RabbitMqActivation(this, endpointFactory, (RabbitMqActivationSpec) spec);
		activations.put((RabbitMqActivationSpec) spec, activation);
		activation.start();

		for (int i = 0; i < numberRabbitMqConsumers; i++) {
			try {
				rabbitMqConsumer = new RabbitMqConsumerImpl(endpointFactory, getUsername(), getPassword(),
						getRabbitHost(), getPortNumber(), getQueueName(), 0);
				workManager.scheduleWork(rabbitMqConsumer);
			} catch (WorkException e) {

			}

		}

		logger.info("endpointActivation()");

	}

	/**
	 * This is called when a message endpoint is deactivated.
	 *
	 * @param endpointFactory A message endpoint factory instance.
	 * @param spec            An activation spec JavaBean instance.
	 */
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		RabbitMqActivation activation = activations.remove(spec);
		if (activation != null)
			activation.stop();

		workManager = null;

		logger.info("endpointDeactivation()");

	}

	/**
	 * This is called when a resource adapter instance is bootstrapped.
	 *
	 * @param ctx A bootstrap context containing references
	 * @throws ResourceAdapterInternalException indicates bootstrap failure.
	 */
	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		this.workManager = ctx.getWorkManager();

		logger.info("start()");
	}

	/**
	 * This is called when a resource adapter instance is undeployed or during
	 * application server shutdown.
	 */
	public void stop() {
		logger.info("stop()");

	}

	/**
	 * This method is called by the application server during crash recovery.
	 *
	 * @param specs An array of ActivationSpec JavaBeans
	 * @throws ResourceException generic exception
	 * @return An array of XAResource objects
	 */
	public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
		logger.info("getXAResources()");
		return null;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		if (rabbitHost != null)
			result += 31 * result + 7 * rabbitHost.hashCode();
		else
			result += 31 * result + 7;
		if (username != null)
			result += 31 * result + 7 * username.hashCode();
		else
			result += 31 * result + 7;
		if (password != null)
			result += 31 * result + 7 * password.hashCode();
		else
			result += 31 * result + 7;
		return result;
	}

	/**
	 * Indicates whether some other object is equal to this one.
	 * 
	 * @param other The reference object with which to compare.
	 * @return true if this object is the same as the obj argument, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof RabbitMqResourceAdapter))
			return false;
		boolean result = true;
		RabbitMqResourceAdapter obj = (RabbitMqResourceAdapter) other;
		if (result) {
			if (rabbitHost == null)
				result = obj.getRabbitHost() == null;
			else
				result = rabbitHost.equals(obj.getRabbitHost());
		}
		if (result) {
			if (username == null)
				result = obj.getUsername() == null;
			else
				result = username.equals(obj.getUsername());
		}
		if (result) {
			if (password == null)
				result = obj.getPassword() == null;
			else
				result = password.equals(obj.getPassword());
		}
		return result;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getNumberRabbitMqConsumers() {
		return numberRabbitMqConsumers;
	}

	public void setNumberRabbitMqConsumers(int numberRabbitMqConsumers) {
		this.numberRabbitMqConsumers = numberRabbitMqConsumers;
	}

}

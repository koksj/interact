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

import javax.resource.ResourceException;

import org.jboss.logging.Logger;

/**
 * RabbitMqConnectionImpl
 *
 * @version $Revision: $
 */
public class RabbitMqConnectionImpl implements RabbitMqConnection {
	/** The logger */
	private final static Logger logger = Logger.getLogger(RabbitMqConnectionImpl.class);

	/** ManagedConnection */
	private RabbitMqManagedConnection mc;

	/** ManagedConnectionFactory */
	private RabbitMqManagedConnectionFactory mcf;

	/**
	 * Default constructor
	 * 
	 * @param mc  RabbitMqManagedConnection
	 * @param mcf RabbitMqManagedConnectionFactory
	 */
	public RabbitMqConnectionImpl(RabbitMqManagedConnection mc, RabbitMqManagedConnectionFactory mcf) {
		this.mc = mc;
		this.mcf = mcf;
	}

	/**
	 * Call onMessage
	 * 
	 * @param message byte
	 * @param message byte
	 * @return byte[]
	 * @throws ResourceException
	 */
	public void onMessage(byte[] message) throws ResourceException  {

		try {
			mc.onMessage(message);
		} catch (ResourceException e) {
			mc.closeHandle(this);
			logger.error("ResourceException Message: " + e.getMessage());
			logger.error("ResourceException Cause: " + e.getCause());
			throw new ResourceException(e);
		}
	}

	/**
	 * Close
	 */
	public void close() {
		if (mc != null) {
			mc.closeHandle(this);
			mc = null;
		}

	}

	/**
	 * Set ManagedConnection
	 */
	void setManagedConnection(RabbitMqManagedConnection mc) {
		this.mc = mc;
	}

}

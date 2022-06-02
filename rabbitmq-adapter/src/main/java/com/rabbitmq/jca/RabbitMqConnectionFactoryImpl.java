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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.jboss.logging.Logger;

/**
 * RabbitMqConnectionFactoryImpl
 *
 * @version $Revision: $
 */
public class RabbitMqConnectionFactoryImpl implements RabbitMqConnectionFactory {
	/** The serial version UID */
	private static final long serialVersionUID = 1L;

	/** The logger */
	private final static Logger logger = Logger.getLogger(RabbitMqConnectionFactoryImpl.class);

	/** Reference */
	private Reference reference;

	/** ManagedConnectionFactory */
	private RabbitMqManagedConnectionFactory mcf;

	/** ConnectionManager */
	private ConnectionManager connectionManager;

	/**
	 * Default constructor
	 */
	public RabbitMqConnectionFactoryImpl() {

	}

	/**
	 * Default constructor
	 * 
	 * @param mcf       ManagedConnectionFactory
	 * @param cxManager ConnectionManager
	 */
	public RabbitMqConnectionFactoryImpl(RabbitMqManagedConnectionFactory mcf, ConnectionManager cxManager) {
		this.mcf = mcf;
		this.connectionManager = cxManager;
	}

	/**
	 * Get connection from factory
	 *
	 * @return RabbitMqConnection instance
	 * @exception ResourceException Thrown if a connection can't be obtained
	 */
	@Override
	public RabbitMqConnection getConnection() throws ResourceException {
		logger.trace("getConnection()");
		return (RabbitMqConnection) connectionManager.allocateConnection(mcf, null);
	}

	/**
	 * Get the Reference instance.
	 *
	 * @return Reference instance
	 * @exception NamingException Thrown if a reference can't be obtained
	 */
	@Override
	public Reference getReference() throws NamingException {
		logger.trace("getReference()");
		return reference;
	}

	/**
	 * Set the Reference instance.
	 *
	 * @param reference A Reference instance
	 */
	@Override
	public void setReference(Reference reference) {
		logger.trace("setReference()");
		this.reference = reference;
	}

}

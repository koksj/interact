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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.jboss.logging.Logger;

/**
 * RabbitMqManagedConnection
 *
 * @version $Revision: $
 */
public class RabbitMqManagedConnection implements ManagedConnection {

	/** The logger */
	private final static Logger logger = Logger.getLogger(RabbitMqManagedConnection.class);

	/** The logwriter */
	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private RabbitMqManagedConnectionFactory mcf;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connections */
	private Set<RabbitMqConnectionImpl> connections;
	
	

	/**
	 * Default constructor
	 * 
	 * @param mcf mcf
	 */
	public RabbitMqManagedConnection(RabbitMqManagedConnectionFactory mcf) {
				
		this.mcf = mcf;
		this.logwriter = null;
		this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
		this.connections = new HashSet<RabbitMqConnectionImpl>();
		
	}

	/**
	 * Creates a new connection handle for the underlying physical connection
	 * represented by the ManagedConnection instance.
	 *
	 * @param subject       Security context as JAAS subject
	 * @param cxRequestInfo ConnectionRequestInfo instance
	 * @return generic Object instance representing the connection handle.
	 * @throws ResourceException generic exception if operation fails
	 */
	public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		logger.info("getConnection()");
		RabbitMqConnectionImpl connection = new RabbitMqConnectionImpl(this, mcf);
		connections.add(connection);
		return connection;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * connection handle with a ManagedConneciton instance.
	 *
	 * @param connection Application-level connection handle
	 * @throws ResourceException generic exception if operation fails
	 */
	public void associateConnection(Object connection) throws ResourceException {
		logger.info("associateConnection()");

		if (connection == null)
			throw new ResourceException("Null connection handle");

		if (!(connection instanceof RabbitMqConnectionImpl))
			throw new ResourceException("Wrong connection handle");

		RabbitMqConnectionImpl handle = (RabbitMqConnectionImpl) connection;
		handle.setManagedConnection(this);
		connections.add(handle);
	}

	/**
	 * Application server calls this method to force any cleanup on the
	 * ManagedConnection instance.
	 *
	 * @throws ResourceException generic exception if operation fails
	 */
	public void cleanup() throws ResourceException {
		logger.info("cleanup()");
		for (RabbitMqConnectionImpl connection : connections) {
			connection.setManagedConnection(null);
		}
		connections.clear();

	}

	/**
	 * Destroys the physical connection to the underlying resource manager.
	 *
	 * @throws ResourceException generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		logger.info("destroy()");

	}

	/**
	 * Adds a connection event listener to the ManagedConnection instance.
	 *
	 * @param listener A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		logger.info("addConnectionEventListener()");
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.add(listener);
	}

	/**
	 * Removes an already registered connection event listener from the
	 * ManagedConnection instance.
	 *
	 * @param listener already registered connection event listener to be removed
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		logger.info("removeConnectionEventListener()");
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/**
	 * Close handle
	 *
	 * @param handle The handle
	 */
	void closeHandle(RabbitMqConnection handle) {	
	
		connections.remove((RabbitMqConnectionImpl) handle);
		ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(handle);
		for (ConnectionEventListener cel : listeners) {
			cel.connectionClosed(event);
		}

	}

	/**
	 * Gets the log writer for this ManagedConnection instance.
	 *
	 * @return Character output stream associated with this Managed-Connection
	 *         instance
	 * @throws ResourceException generic exception if operation fails
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		logger.info("getLogWriter()");
		return logwriter;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 *
	 * @param out Character Output stream to be associated
	 * @throws ResourceException generic exception if operation fails
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logger.info("setLogWriter()");
		logwriter = out;
	}

	/**
	 * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
	 *
	 * @return LocalTransaction instance
	 * @throws ResourceException generic exception if operation fails
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		throw new NotSupportedException("getLocalTransaction() not supported");
	}

	/**
	 * Returns an <code>javax.transaction.xa.XAresource</code> instance.
	 *
	 * @return XAResource instance
	 * @throws ResourceException generic exception if operation fails
	 */
	public XAResource getXAResource() throws ResourceException {
		throw new NotSupportedException("getXAResource() not supported");
	}

	/**
	 * Gets the metadata information for this connection's underlying EIS resource
	 * manager instance.
	 *
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException generic exception if operation fails
	 */
	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		logger.info("getMetaData()");
		return new RabbitMqManagedConnectionMetaData();
	}

	/**
	 * Call onMessage
	 * 
	 * @param message byte
	 * @param message byte
	 * @return byte[]
	 * @throws ResourceException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public void onMessage(byte[] message) throws ResourceException  {
		logger.debug("Msg: " + new String(message));
		try {
			RabbitMqPublisher rabbitMqSubscriber = new RabbitMqPublisher();
			rabbitMqSubscriber.setRabbitHost(mcf.getRabbitHost());
			rabbitMqSubscriber.setPortNumber(mcf.getPortNumber());
			rabbitMqSubscriber.setUsername(mcf.getUsername());
			rabbitMqSubscriber.setPassword(mcf.getPassword());
			rabbitMqSubscriber.setQueueName(mcf.getQueueName());
			rabbitMqSubscriber.init();
			rabbitMqSubscriber.sendMessage(message);
			rabbitMqSubscriber.close();
		} catch (IOException | TimeoutException e) {
			 logger.error("IOException | TimeoutException Message: "+ e.getMessage());
			 logger.error("IOException | TimeoutException Cause: "+ e.getCause());
			 throw new ResourceException(e);
		}
		logger.debug("Wrote msg to FI.BONAZA.OUT" );

		logger.info("RabbitMqManagedConnection onMessage()");
	}

}

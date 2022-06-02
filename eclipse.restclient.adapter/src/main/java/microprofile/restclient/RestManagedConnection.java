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
package microprofile.restclient;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

/**
 * RestManagedConnection
 *
 * @version $Revision: $
 */
public class RestManagedConnection implements ManagedConnection {

	/** The logger */
	private final static Logger logger = Logger.getLogger(RestManagedConnection.class);

	/** The logwriter */
	private PrintWriter logwriter;

	/** ManagedConnectionFactory */
	private RestManagedConnectionFactory mcf;

	/** Listeners */
	private List<ConnectionEventListener> listeners;

	/** Connections */
	private Set<RestConnectionImpl> connections;

	/**
	 * Default constructor
	 * 
	 * @param mcf mcf
	 */
	public RestManagedConnection(RestManagedConnectionFactory mcf) {
		this.mcf = mcf;
		this.logwriter = null;
		this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
		this.connections = new HashSet<RestConnectionImpl>();
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
		logger.trace("getConnection()");
		RestConnectionImpl connection = new RestConnectionImpl(this, mcf);
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
		logger.trace("associateConnection()");

		if (connection == null)
			throw new ResourceException("Null connection handle");

		if (!(connection instanceof RestConnectionImpl))
			throw new ResourceException("Wrong connection handle");

		RestConnectionImpl handle = (RestConnectionImpl) connection;
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
		logger.trace("cleanup()");
		for (RestConnectionImpl connection : connections) {
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
		logger.trace("destroy()");

	}

	/**
	 * Adds a connection event listener to the ManagedConnection instance.
	 *
	 * @param listener A new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		logger.trace("addConnectionEventListener()");
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
		logger.trace("removeConnectionEventListener()");
		if (listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/**
	 * Close handle
	 *
	 * @param handle The handle
	 */
	void closeHandle(RestConnection handle) {
		connections.remove((RestConnectionImpl) handle);
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
		logger.trace("getLogWriter()");
		return logwriter;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 *
	 * @param out Character Output stream to be associated
	 * @throws ResourceException generic exception if operation fails
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logger.trace("setLogWriter()");
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
		logger.trace("getMetaData()");
		return new RestManagedConnectionMetaData();
	}

	/**
	 * Call me
	 */
	void callMe() {
		logger.trace("callMe()");

	}

	public Response sendRestRequest(String json) {
		logger.trace("sendRestRequest(String json)");
		logger.trace("Received parameter: " + json);
		RestClient restClient = new RestClientImpl(mcf.getUsername(), mcf.getPassword(), mcf.getRestUrl(), json);

		return restClient.sendNdeSmsNotification();
	}

}
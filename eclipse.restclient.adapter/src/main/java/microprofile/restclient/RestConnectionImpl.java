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

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

/**
 * RestConnectionImpl
 *
 * @version $Revision: $
 */
public class RestConnectionImpl implements RestConnection {
	/** The logger */
	private final static Logger logger = Logger.getLogger(RestConnectionImpl.class);

	/** ManagedConnection */
	private RestManagedConnection mc;

	/** ManagedConnectionFactory */
	private RestManagedConnectionFactory mcf;

	/**
	 * Default constructor
	 * 
	 * @param mc  RestManagedConnection
	 * @param mcf RestManagedConnectionFactory
	 */
	public RestConnectionImpl(RestManagedConnection mc, RestManagedConnectionFactory mcf) {
		this.mc = mc;
		this.mcf = mcf;
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
	void setManagedConnection(RestManagedConnection mc) {
		this.mc = mc;
	}

	/**
	 * Call me
	 */
	public void callMe() {
		if (mc != null) {
			mc.callMe();
		}
	}

	@Override
	public Response sendRestRequest(String json) {
		logger.debug("Received parameter: " + json);
		Response response = null;
		if (mc != null) {
			response = mc.sendRestRequest(json);
		}
		return response;
	}

}

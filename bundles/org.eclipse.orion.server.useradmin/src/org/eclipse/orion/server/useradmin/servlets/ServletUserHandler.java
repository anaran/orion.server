/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.orion.server.useradmin.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.orion.internal.server.servlets.ProtocolConstants;
import org.eclipse.orion.internal.server.servlets.ServletResourceHandler;
import org.eclipse.orion.server.useradmin.UserServiceHelper;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

/**
 * General user handler.
 */
public class ServletUserHandler extends ServletResourceHandler<String>{
	
	public static VersionRange VERSION1 = new VersionRange("[1,2)"); //$NON-NLS-1$

	private final ServletResourceHandler<String> genericUserHandler;
	private final ServletResourceHandler<String> userHandlerV1;

	final ServletResourceHandler<IStatus> statusHandler;

	ServletUserHandler(UserServiceHelper userServiceHelper, ServletResourceHandler<IStatus> statusHandler) {
		this.statusHandler = statusHandler;
		genericUserHandler = new GenericUserHandler(userServiceHelper, statusHandler);
		userHandlerV1 = new UserHandlerV1(userServiceHelper, statusHandler);
	}

	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response, String userPathInfo) throws ServletException {
		String versionString = request.getHeader(ProtocolConstants.HEADER_ORION_VERSION);
		Version version = versionString == null ? null : new Version(versionString);
		ServletResourceHandler<String> handler;
		if (version != null && VERSION1.isIncluded(version))
			handler = userHandlerV1;
		else
			handler = genericUserHandler;
		return handler.handleRequest(request, response, userPathInfo);
	}
}
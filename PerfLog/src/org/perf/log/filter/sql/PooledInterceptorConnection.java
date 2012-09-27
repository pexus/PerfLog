/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/PooledInterceptorConnection.java 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.perf.log.filter.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import net.sf.log4jdbc.ConnectionSpy;

public class PooledInterceptorConnection implements PooledConnection {
	public PooledInterceptorConnection(PooledConnection pConnection) {
		super();
		this.parent = pConnection;
	}

		
	protected PooledConnection parent;

	@Override
	public void addConnectionEventListener(ConnectionEventListener theListener) {
		parent.addConnectionEventListener( theListener );


	}

	@Override
	public void addStatementEventListener(StatementEventListener listener) {
		parent.addStatementEventListener(listener);

	}

	@Override
	public void close() throws SQLException {
		parent.close();


	}

	@Override
	public Connection getConnection() throws SQLException {
		 return new ConnectionSpy( parent.getConnection() ); // -- log4jdbc entry point!!!

	}

	@Override
	public void removeConnectionEventListener(
			ConnectionEventListener theListener) {
		 parent.removeConnectionEventListener( theListener );

	}

	@Override
	public void removeStatementEventListener(StatementEventListener listener) {
		parent.removeStatementEventListener(listener);


	}

	

}

/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/MySQLConnectionPoolDataSourceInterceptor.java 
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

import java.sql.SQLException;
import javax.sql.PooledConnection;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;



public class MySQLConnectionPoolDataSourceInterceptor extends
		MysqlConnectionPoolDataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MySQLConnectionPoolDataSourceInterceptor() {
		super();
		// do any inits.. - TODO..

	}
	
	@Override
	public synchronized PooledConnection getPooledConnection()
			throws SQLException {
		
		return new PooledInterceptorConnection(super.getPooledConnection());
	}

	@Override
	public synchronized PooledConnection getPooledConnection(String s, String s1)
			throws SQLException {
		
		return new PooledInterceptorConnection(super.getPooledConnection(s, s1));
	}



	
}

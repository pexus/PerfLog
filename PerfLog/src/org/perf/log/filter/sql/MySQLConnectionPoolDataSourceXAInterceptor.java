/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/MySQLConnectionPoolDataSourceXAInterceptor.java 
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
import javax.sql.XAConnection;

import org.apache.derby.iapi.jdbc.ResourceAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;


public class MySQLConnectionPoolDataSourceXAInterceptor extends
		MysqlXADataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MySQLConnectionPoolDataSourceXAInterceptor() {
		super();
		
	}

	@Override
	public XAConnection getXAConnection() throws SQLException {
		
		return new XAInterceptorConnection(super.getXAConnection());
	}

	@Override
	public XAConnection getXAConnection(String u, String p) throws SQLException {
		
		return new XAInterceptorConnection(super.getXAConnection(u, p));
	}

}

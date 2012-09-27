/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/DB2ConnectionPoolDataSourceXAInterceptor.java 
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

public class DB2ConnectionPoolDataSourceXAInterceptor extends
	com.ibm.db2.jcc.DB2XADataSource	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DB2ConnectionPoolDataSourceXAInterceptor() {
		super();

	}

	@Override
	public XAConnection getXAConnection() throws SQLException {
		
		return new XAInterceptorConnection(super.getXAConnection());
	}

	@Override
	public XAConnection getXAConnection(String arg0, String arg1)
			throws SQLException {
		
		return new XAInterceptorConnection(super.getXAConnection(arg0, arg1));
	}

}

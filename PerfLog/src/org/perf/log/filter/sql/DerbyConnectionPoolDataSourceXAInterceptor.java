/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/DerbyConnectionPoolDataSourceXAInterceptor.java 
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
import org.apache.derby.jdbc.EmbeddedXADataSource;

public class DerbyConnectionPoolDataSourceXAInterceptor extends
		EmbeddedXADataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DerbyConnectionPoolDataSourceXAInterceptor() {
		super();
		
	}

	@Override
	protected XAConnection createXAConnection(ResourceAdapter arg0,
			String arg1, String arg2, boolean arg3) throws SQLException {
		// TODO Auto-generated method stub
		return new XAInterceptorConnection(super.createXAConnection(arg0,
				arg1, arg2, arg3));
	}

}

/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/context/PerfLogContextTrackingData.java 
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
package org.perf.log.context;

/**
 * This object contains data that is passed from one JVM to another JVM
 * Currently this is supported via web service calls only
 * Other forms of inter-JVM calls e.g. remote EJB, message driven beans etc. 
 * can be supported in future.
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author pradeep
 *
 */
public class PerfLogContextTrackingData {
	
	
	/**
	 * 
	 */
	public PerfLogContextTrackingData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new perflog context tracking data.
	 *
	 * @param guid the guid
	 * @param createTimeInMillisStr the create time in millis
	 * @param hostId the host id
	 * @param jvmCloneId the jvm clone id
	 * @param jvmDepth the jvm depth
	 * @param sessionId the session id
	 * @param userId the user id
	 */
	public PerfLogContextTrackingData(String guid, String createTimeInMillisStr,
			String hostId, String hostIp, String jvmCloneId, String jvmDepthStr,
			String sessionId, String userId) {
		super();
		this.guid = guid;
		this.createTimeInMillisStr = createTimeInMillisStr;
		if(createTimeInMillisStr!=null)
			this.createTimeInMillis = new Long(createTimeInMillisStr).longValue();
		else
			this.createTimeInMillis = 0;
		
		this.callingJvmHostId = hostId;
		this.callingJvmHostIp = hostIp;
		this.callingJvmCloneId = jvmCloneId;
		this.callingJvmDepthStr = jvmDepthStr;
		if(jvmDepthStr != null)
			this.callingJvmDepth = new Integer(jvmDepthStr).intValue();
		else
			this.callingJvmDepth=0;
		this.sessionId = sessionId;
		this.userId = userId;
	}
	
	/**
	 * Instantiates a new perflog context tracking data.
	 *
	 * @param jsonDataString the json data string
	 */
	public PerfLogContextTrackingData(String jsonDataString) {
		initFromJSONData(jsonDataString);
	}
	String guid;
	String createTimeInMillisStr;
	long   createTimeInMillis;
	String callingJvmHostId;
	String callingJvmHostIp;
	String callingJvmCloneId;
	String callingJvmDepthStr;
	int callingJvmDepth;
	String sessionId;
	String userId;
	
	public void initFromJSONData(String jsonDataString) {
		JSONObject perfLogContextTrackingDataJSONObj = null;
		// deserialize the JSON Object
		JSONParser parser = new JSONParser();
		Object dataVal=null;

		if (jsonDataString != null) {
			try {
				perfLogContextTrackingDataJSONObj = (JSONObject) parser.parse(jsonDataString);
				// createTimeInMillisStr
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("createTimeInMillisStr")) != null) {
					setCreateTimeInMillisStr(((String)dataVal).toString());
				}
				else 
					setCreateTimeInMillisStr(null);
				
				// guid
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("guid")) != null) {
					setGuid(((String)dataVal).toString());
				}
				else 
					setGuid(null);
				
				// callingJvmHostId
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("callingJvmHostId")) != null) {
					setCallingJvmHostId(((String)dataVal).toString());
				}
				else 
					setCallingJvmHostId(null);
				
				// callingJvmHostIp
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("callingJvmHostIp")) != null) {
					setCallingJvmHostIp(((String)dataVal).toString());
				}
				else 
					setCallingJvmHostIp(null);
				
				// jvmCloneId - calling JVM clone ID 
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("callingJvmCloneId")) != null) {
					setCallingJvmCloneId(((String)dataVal).toString());
				}
				else 
					setCallingJvmCloneId(null);
				// jvmDepth
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("callingJvmDepthStr")) != null) {
					setCallingJvmDepthStr(((String)dataVal).toString());
				}
				else 
					setCallingJvmDepthStr(null);
				
				
				
				
				// sessionId
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("sessionId")) != null) {
					setSessionId(((String)dataVal).toString());
				}
				else 
					setSessionId(null);
				// userId
				if ((dataVal = perfLogContextTrackingDataJSONObj.get("userId")) != null) {
					setUserId(((String)dataVal).toString());
				}
				else 
					setUserId(null);
				
				
				
				
			} catch (ParseException e) {

				e.printStackTrace();
			}

		}
	}
	
	
	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}
	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	/**
	 * @return the createTimeInMillisStr
	 */
	public String getCreateTimeInMillisStr() {
		return createTimeInMillisStr;
	}
	/**
	 * @param createTimeInMillisStr the createTimeInMillisStr to set
	 */
	public void setCreateTimeInMillisStr(String createTimeInMillisStr) {
		this.createTimeInMillisStr = createTimeInMillisStr;
		if(createTimeInMillisStr!=null)
			this.createTimeInMillis = new Long(createTimeInMillisStr).longValue();
		else
			this.createTimeInMillis = 0;
	}
	/**
	 * @return the jvmCloneId
	 */
	public String getJvmCloneId() {
		return callingJvmCloneId;
	}
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@SuppressWarnings("unchecked")
	public String toJSON() {
			
			JSONObject obj = new JSONObject();
			obj.put("createTimeInMillisStr", getCreateTimeInMillisStr());
			obj.put("guid", getGuid());			
			obj.put("callingJvmHostId", getCallingJvmHostId());
			obj.put("callingJvmHostIp", getCallingJvmHostIp());
			obj.put("callingJvmCloneId", getCallingJvmCloneId());
			obj.put("callingJvmDepthStr", getCallingJvmDepthStr());
			obj.put("sessionId",getSessionId());
			obj.put("userid",getUserId());
			return obj.toJSONString();
				
		}
	/* returns string representation of PerfLogContextTrackingData 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
	    
	    buf.append("PerfLogContexTrackingData:");
	    
	
	    buf.append(" :createTimeInMillisStr=");
	    buf.append(getCreateTimeInMillisStr());
	    buf.append(" :guid=");
	    buf.append(getGuid());
	    buf.append(" :callingJvmHostId=");
	    buf.append(getCallingJvmHostId());
	    buf.append(" :callingJvmHostIp=");
	    buf.append(getCallingJvmHostIp());
	    buf.append(" :callingJvmCloneId=");
	    buf.append(getCallingJvmCloneId());
	    buf.append(" :callingJvmDepthStr=");
	    buf.append(getCallingJvmDepthStr());
	    buf.append(" :sessionId=");
	    buf.append(getSessionId());
	    buf.append(" :userId=");
	    buf.append(getUserId());
	    
	   	return buf.toString();
	}

	/**
	 * @return the callingJvmHostId
	 */
	public String getCallingJvmHostId() {
		return callingJvmHostId;
	}

	/**
	 * @param callingJvmHostId the callingJvmHostId to set
	 */
	public void setCallingJvmHostId(String callingJvmHostId) {
		this.callingJvmHostId = callingJvmHostId;
	}

	/**
	 * @return the callingJvmCloneId
	 */
	public String getCallingJvmCloneId() {
		return callingJvmCloneId;
	}

	/**
	 * @param callingJvmCloneId the callingJvmCloneId to set
	 */
	public void setCallingJvmCloneId(String callingJvmCloneId) {
		this.callingJvmCloneId = callingJvmCloneId;
	}

	/**
	 * @return the callingJvmDepthStr
	 */
	public String getCallingJvmDepthStr() {
		return callingJvmDepthStr;
	}

	/**
	 * @param callingJvmDepthStr the callingJvmDepthStr to set
	 */
	public void setCallingJvmDepthStr(String callingJvmDepthStr) {
		this.callingJvmDepthStr = callingJvmDepthStr;
		if(callingJvmDepthStr != null)
			this.callingJvmDepth = new Integer(callingJvmDepthStr).intValue();
		else
			this.callingJvmDepth=0;
	}

	/**
	 * @return the callingJvmHostIp
	 */
	public String getCallingJvmHostIp() {
		return callingJvmHostIp;
	}

	/**
	 * @param callingJvmHostIp the callingJvmHostIp to set
	 */
  public void setCallingJvmHostIp(String callingJvmHostIp) {
		this.callingJvmHostIp = callingJvmHostIp;
	}

	public long getCreateTimeInMillis() {
		return createTimeInMillis;
	}

	public int getCallingJvmDepth() {
		return callingJvmDepth;
	}
}

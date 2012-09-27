/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/Logger.java 
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

package org.perf.log.app.logger;

/**
 * A Sample Logger interface that uses JDK Util Logging levels
 *
 */
public interface Logger {
	
  
  public void setLoggerName(String loggerName);
  public void setLevel(java.util.logging.Level level);
  /**
   * Log a message at the TRACE level.
   *
   */
  public void trace(String msg);

  
  /**
   * Log an exception (throwable) at the TRACE level with an
   * accompanying message. 
   * 
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   * 
   */ 
  public void trace(String msg, Throwable t);
 
  
  /**
   * Log a message at the DEBUG level.
   *
   * @param msg the message string to be logged
   */
  public void debug(String msg);
  
  
  /**
   * Log an exception (throwable) at the DEBUG level with an
   * accompanying message. 
   * 
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */ 
  public void debug(String msg, Throwable t);
 
  
  /**
   * Log a message at the INFO level.
   *
   * @param msg the message string to be logged
   */
  public void info(String msg);
  

  /**
   * Log an exception (throwable) at the INFO level with an
   * accompanying message. 
   * 
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log 
   */
  public void info(String msg, Throwable t);

  /**
   * Log a message at the WARN level.
   *
   * @param msg the message string to be logged
   */
  public void warn(String msg);

 /**
   * Log an exception (throwable) at the WARN level with an
   * accompanying message. 
   * 
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log 
   */
  public void warn(String msg, Throwable t);
  

  /**
   * Log a message at the ERROR level.
   *
   * @param msg the message string to be logged
   */
  public void error(String msg);
  
 /**
   * Log an exception (throwable) at the ERROR level with an
   * accompanying message. 
   * 
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */
  public void error(String msg, Throwable t);

}

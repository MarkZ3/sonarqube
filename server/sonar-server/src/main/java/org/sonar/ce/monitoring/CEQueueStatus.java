/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.monitoring;

public interface CEQueueStatus {

  /**
   * Sets the count of reports waiting for processing at startup. This method can be called only once.
   *
   * @param initialPendingCount the count of reports, must be {@literal >=} 0
   *
   * @return the new count of batch reports waiting for processing (which is the same as the argument)
   *
   * @throws IllegalStateException if this method has already been called or is called after {@link #getPendingCount()}
   * @throws IllegalArgumentException if the argument is {@literal <} 0
   */
  long initPendingCount(long initialPendingCount);

  /**
   * Adds 1 to the count of received batch reports and 1 to the count of batch reports waiting for processing.
   * <p>
   * Calling this method is equivalent to calling {@link #addReceived(long)} with {@code 1} as argument but will
   * trigger no parameter check. So, it can be faster.
   * </p>
   *
   * @return the new count of received batch reports
   *
   * @see #getReceivedCount()
   * @see #getPendingCount()
   *
   * @throws IllegalStateException if {@link #initPendingCount(long)} has not been called yet
   */
  long addReceived();

  /**
   * Adds {@code numberOfReceived} to the count of received batch reports and {@code numberOfReceived} to the count of
   * batch reports waiting for processing.
   *
   * @return the new count of received batch reports
   *
   * @see #getReceivedCount()
   * @see #getPendingCount()
   *
   * @throws IllegalStateException if {@link #initPendingCount(long)} has not been called yet
   * @throws IllegalArgumentException if {@code numberOfReceived} is less or equal to 0
   */
  long addReceived(long numberOfReceived);

  /**
   * Adds 1 to the count of batch reports under processing and removes 1 from the count of batch reports waiting for
   * processing.
   *
   * @return the new count of batch reports under processing
   *
   * @see #getInProgressCount()
   * @see #getPendingCount()
   *
   * @throws IllegalStateException if {@link #initPendingCount(long)} has not been called yet
   */
  long addInProgress();

  /**
   * Adds 1 to the count of batch reports which processing ended successfully and removes 1 from the count of batch
   * reports under processing. Adds the specified time to the processing time counter.
   *
   * @param processingTime duration of processing in ms
   *
   * @return the new count of batch reports which processing ended successfully
   *
   * @see #getSuccessCount()
   * @see #getInProgressCount()
   *
   * @throws IllegalArgumentException if processingTime is < 0
   */
  long addSuccess(long processingTime);

  /**
   * Adds 1 to the count of batch reports which processing ended with an error and removes 1 from the count of batch
   * reports under processing. Adds the specified time to the processing time counter.
   *
   * @param processingTime duration of processing in ms
   *
   * @return the new count of batch reports which processing ended with an error
   *
   * @see #getErrorCount()
   * @see #getInProgressCount()
   *
   * @throws IllegalArgumentException if processingTime is < 0
   */
  long addError(long processingTime);

  /**
   * Count of received batch reports since instance startup
   */
  long getReceivedCount();

  /**
   * Count of batch reports waiting for processing since startup, including reports received before instance startup.
   */
  long getPendingCount();

  /**
   * Count of batch reports under processing.
   */
  long getInProgressCount();

  /**
   * Count of batch reports which processing ended with an error since instance startup.
   */
  long getErrorCount();

  /**
   * Count of batch reports which processing ended successfully since instance startup.
   */
  long getSuccessCount();

  /**
   * Time spent processing batch reports since startup.
   */
  long getProcessingTime();
}

/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wealdtech;

import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.primitives.Longs;

import static com.wealdtech.Preconditions.*;

/**
 * A sharded and time-localized ID system that uses generics
 * to ensure type safety.
 * <p/>
 * The Weald ID contains three components: a shard ID, a
 * timestamp and an individual ID.  The components are:
 * <ul>
 * <li>Shard ID: an ID which translates to a particular shard</li>
 * <li>Timestamp: a millisecond-based timestamp, offset from the Unix epoch to allow for a greater range</li>
 * <li>IiD: an internal ID given the shard and timestamp</li>
 * It is envisaged that the WID will be generated by a database so that the ID part can be constrained using a
 * sequence or similar.
 * </ul>
 * The ranges of valid values for each of these components are as follows:
 * TODO
 */
public class WID<T> implements Comparable<WID<T>>
{
  // The epoch of our timestamp, relative to the actual epoch
  public static final long EPOCH = 1325376000000L;

  // Masks for the pieces of the ID
  private static final long SHARDMASK = 0xfff7000000000000L;
  private static final int SHARDOFFSET = 51;
  private static final int SHARDSIZE = 12;
  public static final long MAX_SHARD = (1L << SHARDSIZE) - 1;

  private static final long TIMESTAMPMASK = 0x0003fffffffffc00L;
  private static final int TIMESTAMPOFFSET = 10;
  private static final int TIMESTAMPSIZE = 41;
  public static final long MAX_TIMESTAMP = (1L << TIMESTAMPSIZE) - 1;

  private static final long IIDMASK = 0x00000000000003ffL;
  private static final int IIDSIZE = 10;
  public static final long MAX_IID = (1L << IIDSIZE) - 1;

  private final long id;

  public WID(final long wid)
  {
    this.id = wid;
  }

  /*
   * Obtain the shard ID from a WID.
   * <p/>
   * @return the shard ID
   */
  @JsonIgnore
  public long getShardId()
  {
    return (id & SHARDMASK) >> SHARDOFFSET;
  }

  /*
   * Obtain the timestamp from a WID.
   * <p/>
   * @return the timestamp in milliseconds
   */
  @JsonIgnore
  public long getTimestamp()
  {
    return ((id & TIMESTAMPMASK) >> TIMESTAMPOFFSET) + EPOCH;
  }

  /*
   * Obtain the internal ID from a WID.
   * <p/>
   * @return the internal ID
   */
  @JsonIgnore
  public long getIid()
  {
    return id & IIDMASK;
  }

  /**
   * Generate a simple long value for the ID
   * @return a simple long value for the ID
   */
  public long toLong()
  {
    return id;
  }

  public static <T> WID<T> fromString(final String input)
  {
    checkNotNull(input, "Passed NULL WID");
    try
    {
      return new WID<T>(Long.valueOf(input, 16));
    }
    catch (NumberFormatException nfe)
    {
      throw new DataError.Bad("Failed to parse WID \"" + input + "\"", nfe);
    }
  }

  /**
   * Create an ID given the component parts
   * @param shardId a shard ID, in the range 0 to 8191
   * @param timestamp the timestamp, in milliseconds from the epoch
   * @param id an ID, the range 0 to 1023
   * @return A new ID made out of the components
   */
  public static <T> WID<T> fromComponents(final long shardId, final long timestamp, final long id)
  {
    final long adjustedTimestamp = timestamp - EPOCH;
    checkArgument(shardId >=0 && shardId < MAX_SHARD, "Shard ID %s out of range %s", shardId);
    checkArgument(timestamp >= EPOCH && adjustedTimestamp < MAX_TIMESTAMP, "Timestamp %s out of range %s", timestamp, MAX_TIMESTAMP);
    checkArgument(id >=0 && id < MAX_IID, "ID %s out of range", id);
    return new WID<T>(((shardId << SHARDOFFSET) & SHARDMASK) |
                      ((adjustedTimestamp << TIMESTAMPOFFSET) & TIMESTAMPMASK) |
                      (id & IIDMASK));
  }

  /**
   * Generate a WID with random shard ID and ID.
   * @return a random WID
   */
  public static <T> WID<T> randomWID()
  {
    final Random random = new Random();
    final long shardId = random.nextInt((int)MAX_SHARD);
    final long timestamp = System.currentTimeMillis();
    final long id = random.nextInt((int)MAX_IID);
    return fromComponents(shardId, timestamp, id);
  }

  // Standard object methods

  @Override
  public String toString()
  {
    return Long.toHexString(this.id);
  }

  @Override
  public int hashCode()
  {
    return Longs.hashCode(this.id);
  }

  /**
   * Note that due to type erasure we can't confirm that
   * WID&lt;A&gt; != WID&lt;B&gt; when they hold the same
   * underling ID.  This should not matter given the ID
   * generation mechanism, but do be aware of this if you're
   * relying on this somewhere.
   */
  @Override
  public boolean equals(final Object that)
  {
    return (that instanceof WID) && (this.id == ((WID<?>)that).id);
  }

  @Override
  public int compareTo(final WID<T> that)
  {
    return (this.id < that.id ? -1 :
            (this.id > that.id ? 1 :
             0));
  }
}

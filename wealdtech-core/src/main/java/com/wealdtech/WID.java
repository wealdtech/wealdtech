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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.Longs;

import static com.wealdtech.Preconditions.*;

/**
 * A time-localized and sharded ID system that uses generics
 * to ensure type safety.
 * <p/>
 */
public class WID<T> implements Comparable<WID<T>>
{
  // The epoch of our timestamp, relative to the actual epoch
  private static final long EPOCH = 1325376000000L;

  // Masks for the pieces of the ID
  private static final long TIMESTAMPMASK = 0xffffffffff800000L;
  private static final int TIMESTAMPOFFSET = 23;
  private static final long SHARDMASK = 0x00000000007ffc00L;
  private static final int SHARDOFFSET = 10;
  private static final long MAX_SHARD = 8192;
  private static final long IDMASK = 0x00000000000003ffL;
  private static final long MAX_ID = 1024;

  private final long id;

  @JsonCreator
  public WID(@JsonProperty("id") final long wid)
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

  /**
   * Generate a simple long value for the ID
   * @return a simple long value for the ID
   */
  public long toLong()
  {
    return id;
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

  public static <T> WID<T> fromString(final String input)
  {
    // Handle both decimal and hexadecimal strings.
    // Due to the timestamp portion we should never see
    // a decimal string of less than 16 characters, so this
    // is a safe way of deciding which is which
    if (input.length() > 16)
    {
      return new WID<T>(Long.valueOf(input, 10));
    }
    else
    {
      return new WID<T>(Long.valueOf(input, 16));
    }
  }

  /**
   * Create an ID given the component parts
   * @param timestamp the timestamp, in milliseconds from the epoch
   * @param shardId a shard ID, in the range 0 to 8191
   * @param id an ID, the range 0 to 1023
   * @return A new ID made out of the components
   */
  public static <T> WID<T> fromComponents(final long timestamp, final long shardId, final long id)
  {
    checkArgument(timestamp >= EPOCH && timestamp < 3524399255552L, "Timestamp out of range");
    checkArgument(shardId >=0 && shardId < MAX_SHARD, "Shard ID {} out of range", shardId);
    checkArgument(id >=0 && id < MAX_ID, "ID {} out of range", id);
    return new WID<T>((((timestamp - EPOCH) << TIMESTAMPOFFSET) & TIMESTAMPMASK) |
                      ((shardId << SHARDOFFSET) & SHARDMASK) |
                      (id & IDMASK));
  }

  /**
   * Generate a WID with random shard ID and ID.
   * @return a random WID
   */
  public static <T> WID<T> randomWID()
  {
    final Random random = new Random();
    final long timestamp = System.currentTimeMillis();
    final long shardId = random.nextInt((int)MAX_SHARD);
    final long id = random.nextInt((int)MAX_ID);
    return fromComponents(timestamp, shardId, id);
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

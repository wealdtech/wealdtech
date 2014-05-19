/*
 *    Copyright 2013, 2014 Weald Technology Trading Limited
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;

import static com.wealdtech.Preconditions.checkArgument;
import static com.wealdtech.Preconditions.checkNotNull;

/**
 * A sharded and time-localized ID system that uses generics to ensure type safety.
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
 * <p/>
 * WIDs can have sub-IDs as well, which are simple longs.  Sub-IDs are optional.
 */
public class WID<T> implements Comparable<WID<T>>, Serializable
{
  private static final Random RANDOM = new Random();

  private static final long serialVersionUID = 6897379549693105270L;

  private static final String WID_SEPARATOR = ".";

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

  // Radix for WID - hex
  private static final int RADIX = 16;

  private final long id;

  private final Optional<Long> subId;

  public WID(final long id)
  {
    this(id, null);
  }

  public WID(final long id, final Long subId)
  {
    this.id = id;
    this.subId = Optional.fromNullable(subId);
  }

  /**
   * Obtain the shard ID from a WID.
   * <p/>
   * @return the shard ID
   */
  @JsonIgnore
  public long getShardId()
  {
    return (this.id & SHARDMASK) >> SHARDOFFSET;
  }

  /**
   * Obtain the timestamp from a WID.
   * <p/>
   * @return the timestamp in milliseconds
   */
  @JsonIgnore
  public long getTimestamp()
  {
    return ((this.id & TIMESTAMPMASK) >> TIMESTAMPOFFSET) + EPOCH;
  }

  /**
   * Obtain the internal ID from a WID.
   * <p/>
   * @return the internal ID
   */
  @JsonIgnore
  public long getIid()
  {
    return this.id & IIDMASK;
  }

  /**
   * Get the ID value as a long
   * @return a simple long value for the ID
   */
  public long getId()
  {
    return this.id;
  }

  /**
   * Get the subID value as an optional long
   * @return an optional long value for the subID
   */
  public Optional<Long> getSubId()
  {
    return this.subId;
  }

  public boolean hasSubId()
  {
    return this.subId.isPresent();
  }

  /**
   * Create an ID given a string representation.
   * <p/>The string representation is expected to be a hex value.
   * @param input a string representing the WID
   * @return The WID.
   */
  public static <T> WID<T> fromString(final String input)
  {
    checkNotNull(input, "Passed NULL WID");
    if (input.contains(WID_SEPARATOR))
    {
      final Iterator<String> ids = Splitter.on(WID_SEPARATOR).split(input).iterator();
      try
      {
        return new WID<>(Long.valueOf(ids.next(), RADIX), Long.valueOf(ids.next(), RADIX));
      }
      catch (NumberFormatException nfe)
      {
        throw new DataError.Bad("Failed to parse WID \"" + input + "\"", nfe);
      }
    }
    else
    {
      try
      {
        return new WID<>(Long.valueOf(input, RADIX));
      }
      catch (NumberFormatException nfe)
      {
        throw new DataError.Bad("Failed to parse WID \"" + input + "\"", nfe);
      }
    }
  }

  /**
   * Create an ID given the component parts
   * @param shardId a shard ID, in the range 0 to 8191
   * @param timestamp the timestamp, in milliseconds from the epoch
   * @param id an ID, in the range 0 to 1023
   * @return A new ID made out of the components
   */
  public static <T> WID<T> fromComponents(final long shardId, final long timestamp, final long id)
  {
    return fromComponents(shardId, timestamp, id, null);
  }

  /**
   * Create an ID given the component parts
   * @param shardId a shard ID, in the range 0 to 8191
   * @param timestamp the timestamp, in milliseconds from the epoch
   * @param id an ID, in the range 0 to 1023
   * @param subId a sub-ID; any valid long
   * @return A new ID made out of the components
   */
  public static <T> WID<T> fromComponents(final long shardId, final long timestamp, final long id, final Long subId)
  {
    final long adjustedTimestamp = timestamp - EPOCH;
    checkArgument(shardId >=0 && shardId < MAX_SHARD, "Shard ID %s out of range %s", shardId);
    checkArgument(timestamp >= EPOCH && adjustedTimestamp < MAX_TIMESTAMP, "Timestamp %s out of range %s", timestamp, MAX_TIMESTAMP);
    checkArgument(id >=0 && id < MAX_IID, "ID %s out of range", id);
    return new WID<>(((shardId << SHARDOFFSET) & SHARDMASK) |
                      ((adjustedTimestamp << TIMESTAMPOFFSET) & TIMESTAMPMASK) |
                      (id & IIDMASK), subId);
  }

  /**
   * Create an ID given a long representation.
   * @param id a long representing the WID
   * @return The WID.
   */
  public static <T> WID<T> fromLong(final Long id)
  {
    checkNotNull(id, "Passed NULL WID");
    return new WID<>(id, null);
  }

  /**
   * Create an ID given a long representation.
   * @param id a long representing the WID
   * @return The WID.
   */
  public static <T> WID<T> fromLongs(final Long id, final Long subId)
  {
    checkNotNull(id, "Passed NULL WID");
    return new WID<>(id, subId);
  }

  /**
   * Generate a WID with random shard ID and ID.
   * @return a new WID
   */
  public static <T> WID<T> generate()
  {
    return generate(RANDOM.nextInt((int)MAX_SHARD));
  }

  /**
   * Generate a WID with random shard ID and ID.
   * @return a new WID
   */
  public static <T> WID<T> generate(final int shardId)
  {
    final long timestamp = System.currentTimeMillis();
    final long id = RANDOM.nextInt((int)MAX_IID);
    return fromComponents(shardId, timestamp, id);
  }

  /**
   * Set the WID to have a specific subID.
   * @param subId The subID.  Can be NULL to remove an existing subId
   * @return a new WID with the specified subID
   */
  public WID<T> withSubId(final Long subId)
  {
    return new WID<>(this.getId(), subId);
  }

  /**
   * Recast a WID to another type
   * @param wid the existing wid
   * @param <T> the type to cast it to
   * @return a new WID of the correct type
   */
  @SuppressWarnings("unchecked")
  public static<T> WID<T> recast(WID<?> wid)
  {
    return (WID<T>)wid;
  }

  // Standard object methods

  @Override
  public String toString()
  {
    if (hasSubId())
    {
      return Long.toHexString(this.id) + WID_SEPARATOR + Long.toHexString(this.subId.get());
    }
    else
    {
      return Long.toHexString(this.id);
    }
  }

  @Override
  public int hashCode()
  {
    if (hasSubId())
    {
      return Objects.hashCode(this.id, this.subId.get());
    }
    else
    {
      return Longs.hashCode(this.id);
    }
  }

  /**
   * Note that due to type erasure we can't confirm that
   * WID&lt;A&gt; != WID&lt;B&gt; when they hold the same
   * underlying ID.  This should not matter given the ID
   * generation mechanism, but do be aware of this if you're
   * relying on this somewhere.
   */
  @Override
  public boolean equals(final Object that)
  {
    if (!(that instanceof WID))
    {
      return false;
    }

    final WID<?> cThat = (WID<?>)that;

    return this.id == cThat.id && this.getSubId().equals(cThat.getSubId());
  }

  @Override
  public int compareTo(@Nonnull final WID<T> that)
  {
    if (this.id > that.id)
    {
      return 1;
    }
    else if (this.id < that.id)
    {
      return -1;
    }
    else if (this.hasSubId() && !that.hasSubId())
    {
      return 1;
    }
    else if (!this.hasSubId() && that.hasSubId())
    {
      return -1;
    }
    else if (!this.hasSubId() && !that.hasSubId())
    {
      return 0;
    }
    else
    {
      return this.getSubId().get().compareTo(that.getSubId().get());
    }
  }
}

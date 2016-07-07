package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.wealdtech.WObject;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 */
public class Location extends WObject<Location> implements Comparable<Location>
{
  private static final String NAME = "name";
  private static final String LATITUDE = "latitude";
  private static final String LONGITUDE = "longitude";
  private static final String TEXT = "text";
  private static final String UNIT = "unit";
  private static final String BLOCK = "block";
  private static final String BUILDING = "building";
  private static final String STREET = "street";
  private static final String LOCALITY = "locality";
  private static final String CITY = "city";
  private static final String SUBREGION = "subregion";
  private static final String REGION = "region";
  private static final String COUNTRY = "country";
  private static final String POSTCODE = "postcode";
  private static final String TIMEZONE = "timezone";

  @JsonCreator
  public Location(final Map<String, Object> data)
  {
    super(data);
  }

  @JsonIgnore
  public Optional<String> getName()
  {
    return get(NAME, String.class);
  }

  /**
   * Latitude is provided as an integer, expressed in microdegrees
   *
   * @return the latitude, in microdegrees
   */
  @JsonIgnore
  public Optional<Integer> getLatitude()
  {
    return get(LATITUDE, Integer.class);
  }

  /**
   * Longitude is provided as an integer, expressed in microdegrees
   *
   * @return the longitude, in microdegrees
   */
  @JsonIgnore
  public Optional<Integer> getLongitude()
  {
    return get(LONGITUDE, Integer.class);
  }

  @JsonIgnore
  public Optional<String> getText()
  {
    return get(TEXT, String.class);
  }

  @JsonIgnore
  public Optional<String> getUnit()
  {
    return get(UNIT, String.class);
  }

  @JsonIgnore
  public Optional<String> getBlock()
  {
    return get(BLOCK, String.class);
  }

  @JsonIgnore
  public Optional<String> getBuilding()
  {
    return get(BUILDING, String.class);
  }

  @JsonIgnore
  public Optional<String> getStreet()
  {
    return get(STREET, String.class);
  }

  @JsonIgnore
  public Optional<String> getLocality()
  {
    return get(LOCALITY, String.class);
  }

  @JsonIgnore
  public Optional<String> getCity()
  {
    return get(CITY, String.class);
  }

  @JsonIgnore
  public Optional<String> getSubregion()
  {
    return get(SUBREGION, String.class);
  }

  @JsonIgnore
  public Optional<String> getRegion()
  {
    return get(REGION, String.class);
  }

  @JsonIgnore
  public Optional<String> getCountry()
  {
    return get(COUNTRY, String.class);
  }

  @JsonIgnore
  public Optional<String> getPostcode()
  {
    return get(POSTCODE, String.class);
  }

  @JsonIgnore
  public Optional<DateTimeZone> getTimezone()
  {
    return get(TIMEZONE, DateTimeZone.class);
  }

  /**
   * Validate the location against requirements
   *
   */
  protected void validate()
  {
    checkState(isLatitudeValid(), "Location failed validation: invalid latitude \"" + getLatitude().orNull() + "\"");
    checkState(isLongitudeValid(), "Location failed validation: invalid longitude \"" + getLongitude().orNull() + "\"");
  }

  /**
   * Check whether this location contains address information
   *
   * @return {@code True} if it does, {@code False} if it doesn't
   */
  public boolean containsAddress()
  {
    return getText().isPresent() || getUnit().isPresent() || getBlock().isPresent() || getBuilding().isPresent() ||
           getStreet().isPresent() || getLocality().isPresent() || getCity().isPresent() || getSubregion().isPresent() ||
           getRegion().isPresent() || getCountry().isPresent() || getPostcode().isPresent();
  }

  /**
   * Check whether this location contains latitude and longitude information
   *
   * @return {@code True} if it does, {@code False} if it doesn't
   */
  public boolean containsLatLng()
  {
    return getLatitude().isPresent() && getLongitude().isPresent();
  }

  @JsonIgnore
  private boolean isLatitudeValid()
  {
    boolean result = true;
    if (getLatitude().isPresent() && (Math.abs(getLatitude().get()) > 90000000))
    {
      result = false;
    }
    return result;
  }

  @JsonIgnore
  private boolean isLongitudeValid()
  {
    boolean result = true;
    if (getLongitude().isPresent() && (Math.abs(getLongitude().get()) > 180000000))
    {
      result = false;
    }
    return result;
  }

  /**
   * Provide an address on a single line
   *
   * @return The address
   */
  public String addressToString()
  {
    return addressToString(false);
  }

  /**
   * Provide an address
   *
   * @param multiLine <pre>True</tre> if the output should cover multiple lines
   *
   * @return The address
   */
  String addressToString(final boolean multiLine)
  {
    final String result;
    final List<String> elements = new ArrayList<>();

    if (getText().isPresent())
    {
      // We have a raw version of the address
      elements.add(getText().get());
    }
    else
    {
      // We have a formatted version of the address
      elements.add(getUnit().orNull());
      elements.add(getBlock().orNull());
      elements.add(getBuilding().orNull());
      elements.add(getStreet().orNull());
      elements.add(getLocality().orNull());
      elements.add(getCity().orNull());
      elements.add(getSubregion().orNull());
      elements.add(getRegion().orNull());
      elements.add(getCountry().orNull());
      elements.add(getPostcode().orNull());
    }

    if (multiLine)
    {
      result = Joiner.on("\n").skipNulls().join(elements);
    }
    else
    {
      result = Joiner.on(", ").skipNulls().join(elements);
    }

    return result;
  }

  // TODO Including these functions means we need another library (simplelatlng) on the client.  Do we really need these, and if so can we put them elsewhere?
  //  /**
  //   * Pick the best matches to this location from a list
  //   * @param candidates the candidate locations from which to select the best matches
  //   */
  //  public List<TwoTuple<Location, Integer>> bestMatches(final ImmutableCollection<Location> candidates)
  //  {
  //    final int matches = 5;
  //
  //    // Calculate the a score for each location
  //    final List<TwoTuple<Location, Integer>> results = Lists.newArrayList();
  //    for (final Location location : candidates)
  //    {
  //      final Integer score = matchValue(location);
  //      if (score > 0)
  //      {
  //        results.add(new TwoTuple<>(location, score));
  //      }
  //    }
  //
  //    // Return the first <matches> items, ordered by highest score
  //    return new Ordering<TwoTuple<Location, Integer>>()
  //    {
  //      @Override
  //      public int compare(final TwoTuple<Location, Integer> left, final TwoTuple<Location, Integer> right)
  //      {
  //        // Opposite order to usual because we want to order from highest score to lowest
  //        return Integer.compare(right.getT(), left.getT());
  //      }
  //    }.sortedCopy(results).subList(0, Math.min(matches, results.size()));
  //  }
  //
  //  /**
  //   * Calculate how close a match our location is to another location
  //   * @param that the other location
  //   * @return a value between -1000 and 1000, representing how close the value is.  A value of 0 means that there is no positive
  //   * areas of commonality between the two values.  A value less than 0 means that there are elements in the two locations which
  //   * suggest that they are different locations so even if they are close (for example next door) they should be considered distinct.
  //   */
  //  int matchValue(final Location that)
  //  {
  //    int result = 0;
  //    if (this.equals(that))
  //    {
  //      // Easy
  //      result = 1000;
  //    }
  //    else
  //    {
  //      if (this.getName().equals(that.getName()))
  //      {
  //        result += 500;
  //      }
  //      if (this.getLatitude().isPresent() && that.getLatitude().isPresent() &&
  //          this.getLongitude().isPresent() && that.getLongitude().isPresent())
  //      {
  //        final int distance = distanceTo(that);
  //        result += 1000 - distance;
  //      }
  //    }
  //    return Math.max(Math.min(result, 1000), -1000);
  //  }
  //
  //  /**
  //   * Return the distance to another location, in metres.  Both locations are required to have latitude and
  //   * longitude information
  //   * @param that the other location
  //   * @return the distance between the two locations, in metres
  //   */
  //  int distanceTo(final Location that)
  //  {
  //    checkNotNull(this.getLatitude().orNull(), "Latitude required to calculate distance");
  //    checkNotNull(that.getLatitude().orNull(), "Latitude required to calculate distance");
  //    checkNotNull(this.getLongitude().orNull(), "Longitude required to calculate distance");
  //    checkNotNull(that.getLongitude().orNull(), "Longitude required to calculate distance");
  //
  //    final LatLng thisLatLng = new LatLng(this.getLatitude().get(), this.getLongitude().get());
  //    final LatLng thatLatLng = new LatLng(that.getLatitude().get(), that.getLongitude().get());
  //    return (int)LatLngTool.distance(thisLatLng, thatLatLng, LengthUnit.METER);
  //  }

  // Builder boilerplate
  public static class Builder<P extends Builder<P>> extends WObject.Builder<Location, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final Location prior)
    {
      super(prior);
    }

    public P name(final String name)
    {
      data(NAME, name);
      return self();
    }

    public P latitude(final Integer latitude)
    {
      data(LATITUDE, latitude);
      return self();
    }

    public P longitude(final Integer longitude)
    {
      data(LONGITUDE, longitude);
      return self();
    }

    public P text(final String text)
    {
      data(TEXT, text);
      return self();
    }

    public P unit(final String unit)
    {
      data(UNIT, unit);
      return self();
    }

    public P block(final String block)
    {
      data(BLOCK, block);
      return self();
    }

    public P building(final String building)
    {
      data(BUILDING, building);
      return self();
    }

    public P street(final String street)
    {
      data(STREET, street);
      return self();
    }

    public P locality(final String locality)
    {
      data(LOCALITY, locality);
      return self();
    }

    public P city(final String city)
    {
      data(CITY, city);
      return self();
    }

    public P subregion(final String subregion)
    {
      data(SUBREGION, subregion);
      return self();
    }

    public P region(final String region)
    {
      data(REGION, region);
      return self();
    }

    public P country(final String country)
    {
      data(COUNTRY, country);
      return self();
    }

    public P postcode(final String postcode)
    {
      data(POSTCODE, postcode);
      return self();
    }

    public P timezone(final DateTimeZone timezone)
    {
      data(TIMEZONE, timezone);
      return self();
    }

    public Location build()
      {
        return new Location(data);
      }
  }

  public static Builder<?> builder()
  {
    return new Builder();
  }

  public static Builder<?> builder(final Location prior)
  {
    return new Builder(prior);
  }
}

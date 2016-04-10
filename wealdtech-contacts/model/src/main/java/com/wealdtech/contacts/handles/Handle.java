package com.wealdtech.contacts.handles;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.RangedWObject;
import com.wealdtech.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * Handle contains the details of a contact's presence.
 * This is the abstract superclass; details of specific handles are available in the subclasses.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(com.wealdtech.contacts.handles.AboutMeHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.DisqusHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.EmailHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.FacebookHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.FlickrHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.FourSquareHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.GoogleHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.GravatarHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.IrcHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.KloutHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.LinkedInHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.MediumHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.NameHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.NickNameHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.TelephoneHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.TripItHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.TwitterHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.VimeoHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.WebsiteHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.YouTubeHandle.class)})
public abstract class Handle<T extends Handle<T>> extends RangedWObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(Handle.class);

  // The type is used when deserializing
  protected static final String TYPE = "type";
  // The key uniquely defines the handle
  protected static final String KEY = "_key";
  // The spheres in which the handle operates
  protected static final String SPHERES = "spheres";

  @JsonCreator
  public Handle(final Map<String, Object> data){ super(data); }

  /**
   * @return the type of the handle.  This allows for correct serialization/deserialization
   */
  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  /**
   * @return the key for the handle.  The key uniquely identifies the handle
   */
  @JsonIgnore
  public String getKey(){ return get(KEY, String.class).get(); }

  private static final TypeReference<ImmutableSet<Sphere>> SPHERES_TYPE_REF = new TypeReference<ImmutableSet<Sphere>>(){};
  @JsonIgnore
  public ImmutableSet<Sphere> getSpheres() { return get(SPHERES, SPHERES_TYPE_REF).or(ImmutableSet.<Sphere>of()); }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    // Prepend type to the key and ensure it is lower-case
    data.put(KEY, (data.get(TYPE) + "::" + data.get(KEY)).toLowerCase(Locale.ENGLISH));

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TYPE), "Handle failed validation: must contain type");
    checkState(exists(KEY), "Handle failed validation: must contain key");
    checkState(Objects.equal(getKey(), getKey().toLowerCase()), "Handle failed validation: key must be lower-case");
  }

  public static class Builder<T extends Handle<T>, P extends Builder<T, P>> extends RangedWObject.Builder<T, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final T prior)
    {
      super(prior);
    }

    public P type(final String type)
    {
      data(TYPE, type);
      return self();
    }

    public P key(final String key)
    {
      data(KEY, key);
      return self();
    }

    public P spheres(final ImmutableSet<Sphere> spheres)
    {
      data(SPHERES, spheres);
      return self();
    }
  }

  /**
   * The sphere in which a handle operatres (professional, personal)
   */
  public static enum Sphere
  {
    /**
     * Professional sphere
     */
    PROFESSIONAL(1)
    /**
     * Personal sphere
     */
    , PERSONAL(2);

    public final int val;

    private Sphere(final int val)
    {
      this.val = val;
    }

    private static final ImmutableSortedMap<Integer, Sphere> _VALMAP;

    static
    {
      final Map<Integer, Sphere> levelMap = Maps.newHashMap();
      for (final Sphere contextType : Sphere.values())
      {
        levelMap.put(contextType.val, contextType);
      }
      _VALMAP = ImmutableSortedMap.copyOf(levelMap);
    }

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }

    @JsonCreator
    public static Sphere fromString(final String val)
    {
      try
      {
        return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because
        // this happens during invocation, and in that case the enum handler
        // will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A sphere \"" + val + "\" supplied is invalid");
      }
    }

    public static Sphere fromInt(final Integer val)
    {
      checkNotNull(val, "Sphere not supplied");
      final Sphere state = _VALMAP.get(val);
      checkNotNull(state, "Sphere is invalid");
      return state;
    }
  }
}
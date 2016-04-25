package com.wealdtech.contacts.handles;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.wealdtech.DataError;
import com.wealdtech.RangedWObject;
import com.wealdtech.contacts.Context;
import com.wealdtech.contacts.uses.Use;
import com.wealdtech.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

import static com.wealdtech.Preconditions.checkNotNull;
import static com.wealdtech.Preconditions.checkState;

/**
 * A Handle contains the details of how to reach a contact.  It might be an email address, a telephone number, or their name or
 * nickname.  Handles can be restricted to a subset of available spheres.
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
  // The type is used when deserializing
  protected static final String TYPE = "type";
  // The key uniquely defines the handle
  protected static final String KEY = "_key";
  // The spheres in which the handle operates
  protected static final String SPHERES = "spheres";
  private static final Logger LOG = LoggerFactory.getLogger(Handle.class);
  private static final TypeReference<ImmutableSet<Sphere>> SPHERES_TYPE_REF = new TypeReference<ImmutableSet<Sphere>>(){};

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

  @JsonIgnore
  public ImmutableSet<Sphere> getSpheres() { return get(SPHERES, SPHERES_TYPE_REF).or(ImmutableSet.<Sphere>of()); }

  /**
   * Check if this handle has an equivalent use.
   * @return {@code true} if it has a use; otherwise {@code false}.
   */
  public abstract boolean hasUse();

  /**
   * Convert a handle to an equivalent Use
   * @param context the context in which the use will apply
   * @param familiarity
   *@param formality @return the equivalent use; will be {@code null} if this handle does not have an equivalent use
   */
  @Nullable public abstract Use toUse(Context context, final int familiarity, final int formality);

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

  /**
   * The sphere in which a handle operates (professional, personal)
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
    ,PERSONAL(2);

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

    public final int val;

    private Sphere(final int val)
    {
      this.val = val;
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

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }
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
}
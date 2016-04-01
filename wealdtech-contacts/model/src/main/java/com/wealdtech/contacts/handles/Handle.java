package com.wealdtech.contacts.handles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.wealdtech.RangedWObject;
import com.wealdtech.contacts.ContextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.wealdtech.Preconditions.checkState;

/**
 * Handle contains the details of a contact's presence.
 * This is the abstract superclass; details of specific handles are available in the subclasses.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(com.wealdtech.contacts.handles.EmailHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.IrcHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.NameHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.NickNameHandle.class),
               @JsonSubTypes.Type(com.wealdtech.contacts.handles.WebsiteHandle.class)})
public abstract class Handle<T extends Handle<T>> extends RangedWObject<T> implements Comparable<T>
{
  private static final Logger LOG = LoggerFactory.getLogger(Handle.class);

  // The type and realm jointly define the scope of the handle
  protected static final String TYPE = "type";
  protected static final String KEY = "_key";

  protected static final String CONTEXT_TYPES = "contexts";

  @JsonCreator
  public Handle(final Map<String, Object> data){ super(data); }

  /**
   * @return the type of the handle.  This allows for correct serialization/deserialization
   */
  @JsonIgnore
  public String getType(){ return get(TYPE, String.class).get(); }

  /**
   * @return the key for the handle.  The key uniquely identifies the handle for the given type
   */
  @JsonIgnore
  public String getKey(){ return get(KEY, String.class).get(); }

  private static final TypeReference<ImmutableSet<ContextType>> CONTEXT_TYPE_REF = new TypeReference<ImmutableSet<ContextType>>(){};
  @JsonIgnore
  public ImmutableSet<ContextType> getContextTypes() { return get(CONTEXT_TYPES, CONTEXT_TYPE_REF).or(ImmutableSet.<ContextType>of()); }

  @Override
  protected void validate()
  {
    super.validate();
    checkState(exists(TYPE), "Handle failed validation: must contain type");
    checkState(exists(KEY), "Handle failed validation: must contain key");
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

    public P contextTypes(final ImmutableSet<ContextType> contextTypes)
    {
      data(CONTEXT_TYPES, contextTypes);
      return self();
    }
  }
}
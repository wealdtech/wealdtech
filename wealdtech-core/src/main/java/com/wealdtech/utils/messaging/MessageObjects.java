package com.wealdtech.utils.messaging;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wealdtech.DataError;

public class MessageObjects<T extends Object> implements Serializable
{
  private static final long serialVersionUID = 6306799063373268531L;

  private transient final T prior;
  private transient final T current;

  @JsonCreator
  public MessageObjects(final @JsonProperty("prior") T prior,
                        final @JsonProperty("current") T current) throws DataError
  {
    if ((prior == null) && (current == null))
    {
      throw new DataError("At least one object must be non-NULL");
    }
    this.prior = prior;
    this.current = current;
  }

  public T getPrior()
  {
    return this.prior;
  }
  
  public T getCurrent()
  {
    return this.current;
  }

  public Class<? extends Object> getType()
  {
    Class<? extends Object> retclass;
    if (prior == null)
    {
      retclass = this.current.getClass();
    }
    else
    {
      retclass = this.prior.getClass();
    }
    return retclass;
  }
}
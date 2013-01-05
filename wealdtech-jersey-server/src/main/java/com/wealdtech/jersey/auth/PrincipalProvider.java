package com.wealdtech.jersey.auth;

import com.google.common.base.Optional;
import com.wealdtech.DataError;
import com.wealdtech.ServerError;

/**
 * Interface to provide a principal from a unique key.
 *
 * @param <T> the type of the principal
 * @param <C> the type of the key passed to locate the principal
 */
public interface PrincipalProvider<T, C>
{
  public Optional<T> get(C key) throws DataError, ServerError;
}

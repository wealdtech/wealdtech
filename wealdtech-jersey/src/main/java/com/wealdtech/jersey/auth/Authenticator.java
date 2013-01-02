package com.wealdtech.jersey.auth;

import com.google.common.base.Optional;

import com.wealdtech.DataError;
import com.wealdtech.ServerError;

/**
 * Generic interface for authentication of a principal against credentials.
 */
public interface Authenticator<C, P>
{
  /**
   * Authenticate a principal given a set of credentials.
   * @param credentials A set of credentials
   * @return The authenticated principal, or Optional.absent() if not authenticated
   * @throws DataError If the data passed in does not allow authentication
   * @throws ServerError If there is a problem authenticating the credentials
   */
  Optional<P> authenticate(C credentials) throws DataError, ServerError;
}

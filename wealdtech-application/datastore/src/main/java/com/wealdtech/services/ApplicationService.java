package com.wealdtech.services;

import com.wealdtech.Application;
import com.wealdtech.WID;

/**
 */
public interface ApplicationService
{
  /**
   * Create an application
   */
  void create(Application application);

  /**
   * Obtain an application givens its ID
   */
  Application obtain(WID<Application> appId);

  /**
   * Remove an application
   */
  void remove(Application application);
}

package com.wealdtech.utils;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class GuavaUtils
{
  public static <T extends Collection<?>> T emptyToNull(final T cl)
  {
    if (cl == null || cl.isEmpty())
    {
      return null;
    }
    return cl;
  }

  public static <T, U> Map<T, U> emptyToNull(final Map<T, U> input)
  {
    if (input == null || input.isEmpty())
    {
      return null;
    }
    return input;
  }

  public static <T, U> Multimap<T, U> emptyToNull(final Multimap<T, U> input)
  {
    if (input == null || input.isEmpty())
    {
      return null;
    }
    return input;
  }

}

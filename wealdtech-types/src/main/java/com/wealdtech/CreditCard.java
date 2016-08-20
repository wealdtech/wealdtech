/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Range;
import com.wealdtech.collect.IntervalMap;
import com.wealdtech.utils.StringUtils;
import org.joda.time.YearMonth;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Representation of the major items for a credit card
 */
public class CreditCard extends WObject<CreditCard> implements Comparable<CreditCard>
{
  private static final String NUMBER = "number";
  private static final String EXPIRY = "expiry";
  private static final String CSC = "csc";

  @JsonCreator
  public CreditCard(final Map<String, Object> data){ super(data); }

  @Override
  protected Map<String, Object> preCreate(final Map<String, Object> data)
  {
    // Ensure that the number is clean
    if (data.containsKey(NUMBER))
    {
      data.put(NUMBER, cleanNumber((String)data.get(NUMBER)));
    }

    return super.preCreate(data);
  }

  @Override
  protected void validate()
  {
    super.validate();
  }

  /**
   * @return the main number for the credit card
   */
  @JsonIgnore
  public String getNumber(){ return get(NUMBER, String.class).get(); }

  @JsonIgnore
  public YearMonth getExpiry(){ return get(EXPIRY, YearMonth.class).get(); }

  @JsonIgnore
  public String getCsc(){ return get(CSC, String.class).get(); }

  private static String cleanNumber(final String input)
  {
    if (input == null)
    {
      return null;
    }
    return input.replaceAll("[^0-9]", "");
  }

  public static class Builder<P extends Builder<P>> extends WObject.Builder<CreditCard, P>
  {
    public Builder()
    {
      super();
    }

    public Builder(final CreditCard prior)
    {
      super(prior);
    }

    public P number(final String number)
    {
      data(NUMBER, number);
      return self();
    }

    public P expiry(final YearMonth expiry)
    {
      data(EXPIRY, expiry);
      return self();
    }

    public P csc(final String csc)
    {
      data(CSC, csc);
      return self();
    }

    public CreditCard build()
    {
      return new CreditCard(data);
    }
  }

  public static Builder<?> builder(){ return new Builder(); }

  public static Builder<?> builder(final CreditCard prior){ return new Builder(prior); }

  /**
   * Information on the brand of a credit card.
   * The ranges define the shortest numbers that can uniquely identify the card.  For example, although it appears at first
   * that China Unionpay could provide a smaller range [62,63) this is not allowed because there are Discover cards in the
   * range [622126, 622926) so the longer number of digits is required to ensure that the brands can be defined.
   * Having multiple ranges with the same number of digits is fine if one range sits entirely within another (as is the case
   * with the Discover cards, one of whose ranges sits entirely within that for China Unionpay).
   */
  public static enum Brand
  {
    AMERICAN_EXPRESS("^3[47][0-9]{13}$", Arrays.asList(Range.closedOpen(34L, 35L), Range.closedOpen(37L, 38L)), 16, 4, "CID"),

    CHINA_UNIONPAY("^62[0-9]{14,17}$", Arrays.asList(Range.closedOpen(620000L, 630000L)), 19, 3, "CVN2"),

    DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}$",
                Arrays.asList(Range.closedOpen(54L, 56L), Range.closedOpen(300L, 306L), Range.closedOpen(309L, 310L),
                              Range.closedOpen(36L, 37L), Range.closedOpen(38L, 40L)), 16, 3, "CID"),

    DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$",
             Arrays.asList(Range.closedOpen(6011L, 6012L), Range.closedOpen(622126L, 622926L), Range.closedOpen(644L, 650L),
                           Range.closedOpen(65L, 66L)), 16, 3, "CID"),

    JCB("^(?:2131|1800|35\\d{3})\\d{11}$", Arrays.asList(Range.closedOpen(3528L, 3590L)), 16, 3, "CAV2"),

    MASTERCARD("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$",
               Arrays.asList(Range.closedOpen(50L, 56L), Range.closedOpen(2221L, 2721L)), 16, 3, "CVC2"),

    VISA("^4[0-9]{12}(?:[0-9]{3})?$", Arrays.asList(Range.closedOpen(4L, 5L)), 16, 3, "CVV2");

    public final String validationRegex;
    public final List<Range<Long>> prefixes;
    public final int numberMaxLength;
    public final int cscLength;
    public final String cscName;

    private Brand(final String validationRegex,
                  List<Range<Long>> prefixes,
                  final int numberMaxLength,
                  final int cscLength,
                  final String cscName)
    {
      this.validationRegex = validationRegex;
      this.prefixes = prefixes;
      this.numberMaxLength = numberMaxLength;
      this.cscLength = cscLength;
      this.cscName = cscName;
    }

    private static final IntervalMap<Long, Brand> _BRANDSMAP;

    static
    {
      _BRANDSMAP = new IntervalMap<>();
      for (final Brand brand : Brand.values())
      {
        for (final Range<Long> prefix : brand.prefixes)
        {
          _BRANDSMAP.put(prefix, brand);
        }
      }
    }


    @JsonCreator
    public static Brand fromString(final String val)
    {
      try
      {
        return valueOf(val.toUpperCase(Locale.ENGLISH).replaceAll(" ", "_"));
      }
      catch (final IllegalArgumentException iae)
      {
        // N.B. we don't pass the iae as the cause of this exception because this happens during invocation, and in that case the
        // enum handler will report the root cause exception rather than the one we throw.
        throw new DataError.Bad("A brand \"" + val + "\" supplied is invalid");
      }
    }

    /**
     * Obtain a brand for a credit card given a number
     *
     * @param number the credit card number
     *
     * @return the brand, or {@code null} if the brand cannot be determined
     */
    @Nullable
    public static Brand fromCardNumber(final String number)
    {
      // First off clean the input
      final String cleanNumber = cleanNumber(number);
      if (cleanNumber == null)
      {
        // Nothing to work with
        return null;
      }
      // Issuer Identification Number is 6 digits long but we might have less
      final String iin = cleanNumber.substring(0, Math.min(cleanNumber.length(), 6));

      // Work backwards from the full IIN to a single character to see if we can find a match
      for (int i = iin.length(); i > 0; i--)
      {
        final String prefix = iin.substring(0, i);
        final long prefixNum = Long.valueOf(prefix);
        final Brand brand = _BRANDSMAP.get(prefixNum);
        if (brand != null)
        {
          return brand;
        }
      }
      // No luck
      return null;
    }

    @Override
    @JsonValue
    public String toString()
    {
      return StringUtils.capitalize(super.toString().toLowerCase(Locale.ENGLISH)).replaceAll("_", " ");
    }
  }

  public static boolean luhn(final String number)
  {
    int sum = 0;
    boolean alternate = false;
    for (int i = number.length() - 1; i >= 0; i--)
    {
      int n = Integer.parseInt(number.substring(i, i + 1));
      if (alternate)
      {
        n *= 2;
        if (n > 9)
        {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }
    return (sum % 10 == 0);
  }
}

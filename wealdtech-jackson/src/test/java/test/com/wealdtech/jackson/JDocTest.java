/*
 * Copyright 2012 - 2014 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package test.com.wealdtech.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wealdtech.jackson.JDoc;
import com.wealdtech.jackson.WealdMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests for JDoc
 */
public class JDocTest
{
  @BeforeSuite
  public void setupLogging()
  {

  }

  @Test
  public void testSer1()
  {
    try
    {
      final ImmutableMap<String, Object> subMap =
        ImmutableMap.<String, Object>of("sub1", Boolean.TRUE, "sub2", 5, "sub3", new InetSocketAddress("1.2.3.4", 80));
      final JDoc subDoc = new JDoc(subMap);

      final ImmutableMap<String, Object> map =
        ImmutableMap.<String, Object>of("val1", new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC), "val2", subDoc);
      final JDoc doc = new JDoc(map);
      final String docStr = WealdMapper.getServerMapper().writeValueAsString(doc);

      assertEquals(docStr,
                   "{\"val1\":\"2014-09-16T23:00:00.000+00:00 UTC\",\"val2\":{\"sub1\":true,\"sub2\":5,\"sub3\":\"1.2.3.4:80\"}}");
    }
    catch (JsonProcessingException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeser1()
  {
    try
    {
      final String deser = "{\"val1\":true}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertNotNull(doc);
      assertEquals(doc.get("val1", Boolean.class).or(false), (Boolean)true);
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeser2()
  {
    try
    {
      final String deser = "{\"val2\":\"2014-09-16T23:00:00Z\"}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertNotNull(doc);
      assertEquals(doc.get("val2", DateTime.class).orNull(), new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC));
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  public static class Deser3Test
  {
    public DateTime dt;
    public String str;
  }

  @Test
  public void testDeser3()
  {
    try
    {
      final String deser = "{\"val2\":\"2014-09-16T23:00:00Z\",\"val3\":{\"dt\":\"2014-09-16T23:00:00Z\",\"str\":\"objstr\"}}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertNotNull(doc);

      final Optional<DateTime> val2 = doc.get("val2", DateTime.class);
      assertTrue(val2.isPresent());
      assertEquals(val2.get(), new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC));

      final Optional<Deser3Test> sub = doc.get("val3", Deser3Test.class);
      assertTrue(sub.isPresent());
      assertEquals(sub.get().dt, new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC));
      assertEquals(sub.get().str, "objstr");
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeserx()
  {
    try
    {
      final String deser =
        "{\"val1\":\"2014-09-16T23:00:00Z\",\"val2\":{\"sub1\":\"true\",\"sub2\":\"5\",\"sub3\":\"/1.2.3.4:80\"}}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertEquals(doc.get("val1", new TypeReference<DateTime>() {}).orNull(),
                   new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC));
      final JDoc subDoc = doc.get("val2", new TypeReference<JDoc>() {}).orNull();
      assertNotNull(subDoc);
      assertEquals(subDoc.get("sub1", Boolean.class).orNull(), (Boolean)true);
      assertEquals(subDoc.get("sub2", new TypeReference<Integer>() {}).orNull(), (Integer)5);
      assertEquals(subDoc.get("sub3", InetSocketAddress.class).orNull(), new InetSocketAddress("/1.2.3.4", 80));
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeserEmpty()
  {
    try
    {
      final String deser = "{}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertTrue(doc.isEmpty(), "JDoc not empty when it should be");
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  public static class ObjClass
  {
    public int first;
    public String second;
  }

  @Test
  public void testSerObject()
  {
    final ObjClass obj = new ObjClass();
    obj.first = 1;
    obj.second = "Two";

    try
    {
      final ImmutableMap<String, Object> map = ImmutableMap.of("sub1", Boolean.TRUE, "sub2", 5, "sub3", obj);
      final JDoc doc = new JDoc(map);
      final String docStr = WealdMapper.getServerMapper().writeValueAsString(doc);
      assertEquals(docStr, "{\"sub1\":true,\"sub2\":5,\"sub3\":{\"first\":1,\"second\":\"Two\"}}");
    }
    catch (JsonProcessingException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testSerArray()
  {
    try
    {
      final ImmutableMap<String, Object> subMap =
        ImmutableMap.<String, Object>of("sub1", Boolean.TRUE, "sub2", 5, "sub3", Lists.newArrayList("one", "two", "three"));
      final JDoc subDoc = new JDoc(subMap);

      final ImmutableMap<String, Object> map =
        ImmutableMap.<String, Object>of("val1", new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC), "val2", subDoc);
      final JDoc doc = new JDoc(map);
      final String docStr = WealdMapper.getServerMapper().writeValueAsString(doc);
      assertEquals(docStr,
                   "{\"val1\":\"2014-09-16T23:00:00.000+00:00 UTC\",\"val2\":{\"sub1\":true,\"sub2\":5,\"sub3\":[\"one\",\"two\",\"three\"]}}");
    }
    catch (JsonProcessingException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeserArray()
  {
    try
    {
      final String deser = "{\"array\":[\"val1\", \"val2\"]}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
      assertEquals(doc.get("array", ArrayList.class).orNull(), Lists.newArrayList("val1", "val2"));
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  // Ensure we can deserialize a complex document (in this case something from Expedia)
  @Test
  public void testDeserExpedia()
  {
    try
    {
      final String deser =
        "{\"HotelListResponse\":{\"customerSessionId\":\"0ABAAACB-7631-2914-8882-E276A590211B\",\"numberOfRoomsRequested\":1,\"moreResultsAvailable\":true,\"cacheKey\":\"70976312:14888e276a5:-2079\",\"cacheLocation\":\"10.186.170.203:7300\",\"HotelList\":{\"@size\":\"25\",\"@activePropertyCount\":\"1312\",\"HotelSummary\":[{\"@order\":\"0\",\"hotelId\":335698,\"name\":\"Park Plaza Westminster Bridge London\",\"address1\":\"200 Westminster Bridge Road\",\"city\":\"London\",\"postalCode\":\"SE1 7UT\",\"countryCode\":\"GB\",\"airportCode\":\"LCY\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":45,\"amenityMask\":1540098,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near London Eye\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Park Plaza Westminster Bridge London, you&apos;ll be centrally located in London, steps from London Aquarium and Florence Nightingale Museum. This 4-star\",\"highRate\":219,\"lowRate\":160.65,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.50111,\"longitude\":-0.11733,\"proximityDistance\":0.7890795,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/4000000\\/3120000\\/3113100\\/3113039\\/3113039_116_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/335698\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200019300,\"rateCode\":201257002,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Wheelchair Accessible Room - Advance Saver. 21 days\",\"promoId\":201870147,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":3113039,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902078\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"204.0\",\"@averageRate\":\"173.4\",\"@commissionableUsdTotal\":\"563.63\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"36.41\",\"@eanCompensationOnline\":\"60.69\",\"@maxNightlyRate\":\"186.15\",\"@nightlyRateTotal\":\"346.8\",\"@grossProfitOffline\":\"32.84\",\"@grossProfitOnline\":\"56.25\",\"@total\":\"346.8\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"219.0\",\"@rate\":\"186.15\",\"@promo\":\"true\"},{\"@baseRate\":\"189.0\",\"@rate\":\"160.65\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"1024\",\"description\":\"Free High-Speed Internet\"}}}}},{\"@order\":\"1\",\"hotelId\":150368,\"name\":\"Copthorne Tara Hotel London Kensington\",\"address1\":\"Scarsdale Place\",\"address2\":\"Kensington\",\"city\":\"London\",\"postalCode\":\"W8 5SR\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":89,\"amenityMask\":3637251,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Royal Albert Hall\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at Copthorne Tara Hotel London Kensington places you in the heart of London, minutes from Kensington Roof Gardens and close to Royal Albert Hall. This 4-star\",\"highRate\":177.6,\"lowRate\":100.8,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.4991,\"longitude\":-0.19164,\"proximityDistance\":3.404006,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/30000\\/26200\\/26145\\/26145_156_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/150368\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":4997,\"rateCode\":1885460,\"maxRoomOccupancy\":3,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Double Room\",\"currentAllotment\":95,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":26145,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902077\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"139.20001\",\"@averageRate\":\"139.20001\",\"@commissionableUsdTotal\":\"452.46\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"43.15\",\"@eanCompensationOnline\":\"62.63\",\"@maxNightlyRate\":\"177.6\",\"@nightlyRateTotal\":\"278.40002\",\"@grossProfitOffline\":\"40.28\",\"@grossProfitOnline\":\"59.07\",\"@total\":\"278.4\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"177.6\",\"@rate\":\"177.6\",\"@promo\":\"false\"},{\"@baseRate\":\"100.8\",\"@rate\":\"100.8\",\"@promo\":\"false\"}]}}}}}},{\"@order\":\"2\",\"hotelId\":120639,\"name\":\"The Marylebone Hotel\",\"address1\":\"47 Welbeck Street\",\"city\":\"London\",\"postalCode\":\"W1G 8DN\",\"countryCode\":\"GB\",\"airportCode\":\"LGW\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":80,\"amenityMask\":1286147,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near Selfridges\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at The Marylebone Hotel, you&apos;ll be centrally located in London, steps from Wigmore Hall and minutes from Selfridges. This 4-star hotel is close to\",\"highRate\":195,\"lowRate\":165.75,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51805,\"longitude\":-0.1496,\"proximityDistance\":2.4783013,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/470000\\/470000\\/469914\\/469914_26_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/120639\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":350211,\"rateCode\":200928369,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Double Room\",\"promoId\":201555099,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":1,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":469914,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902076\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"195.0\",\"@averageRate\":\"165.75\",\"@commissionableUsdTotal\":\"538.76\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"57.67\",\"@eanCompensationOnline\":\"85.51\",\"@maxNightlyRate\":\"165.75\",\"@nightlyRateTotal\":\"331.5\",\"@grossProfitOffline\":\"53.57\",\"@grossProfitOnline\":\"80.42\",\"@surchargeTotal\":\"66.3\",\"@total\":\"397.8\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"195.0\",\"@rate\":\"165.75\",\"@promo\":\"true\"},{\"@baseRate\":\"195.0\",\"@rate\":\"165.75\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"66.3\"}}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"3\",\"hotelId\":228199,\"name\":\"Park Plaza Riverbank London\",\"address1\":\"18 Albert Embankment\",\"city\":\"London\",\"postalCode\":\"SE1 7TJ\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":61,\"amenityMask\":1081355,\"tripAdvisorRating\":4,\"locationDescription\":\"Near Tate Britain\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Park Plaza Riverbank London in London (South Bank - Waterloo), you&apos;ll be minutes from Tate Britain and Lambeth Bridge. This 4-star hotel is close to\",\"highRate\":179,\"lowRate\":126.65,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49163,\"longitude\":-0.12168,\"proximityDistance\":0.3553135,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/2000000\\/1190000\\/1184000\\/1183908\\/1183908_107_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/228199\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":92416,\"rateCode\":201256965,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior\",\"promoId\":201870185,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":1183908,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902075\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"164.0\",\"@averageRate\":\"139.4\",\"@commissionableUsdTotal\":\"453.11\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"29.27\",\"@eanCompensationOnline\":\"48.79\",\"@maxNightlyRate\":\"152.15\",\"@nightlyRateTotal\":\"278.8\",\"@grossProfitOffline\":\"26.40\",\"@grossProfitOnline\":\"45.22\",\"@total\":\"278.8\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"179.0\",\"@rate\":\"152.15\",\"@promo\":\"true\"},{\"@baseRate\":\"149.0\",\"@rate\":\"126.65\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"1024\",\"description\":\"Free High-Speed Internet\"}}}}},{\"@order\":\"4\",\"hotelId\":193608,\"name\":\"Hotel Russell\",\"address1\":\"1-8 Russell Square\",\"address2\":\"Bloomsbury\",\"city\":\"London\",\"postalCode\":\"WC1B 5BE\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":31,\"amenityMask\":1540097,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Tottenham Court Road\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Hotel Russell, you&apos;ll be centrally located in London, steps from Russell Square and minutes from London Senate House Library. This 4-star hotel is\",\"highRate\":126.67,\"lowRate\":81.66,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.52254,\"longitude\":-0.12544,\"proximityDistance\":2.3099527,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/30000\\/21600\\/21514\\/21514_77_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/193608\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":2148,\"rateCode\":200938012,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Standard Double Room, 1 Double Bed - Advance Purchase\",\"promoId\":202136388,\"promoDescription\":\"14 day advance purchase\",\"currentAllotment\":76,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":21514,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902074\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"104.17\",\"@averageRate\":\"104.16\",\"@commissionableUsdTotal\":\"338.57\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"31.23\",\"@eanCompensationOnline\":\"48.73\",\"@maxNightlyRate\":\"126.66\",\"@nightlyRateTotal\":\"208.32\",\"@grossProfitOffline\":\"28.66\",\"@grossProfitOnline\":\"45.53\",\"@surchargeTotal\":\"41.66\",\"@total\":\"249.98\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"126.67\",\"@rate\":\"126.66\",\"@promo\":\"true\"},{\"@baseRate\":\"81.67\",\"@rate\":\"81.66\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"41.66\"}}}}}}},{\"@order\":\"5\",\"hotelId\":258400,\"name\":\"Park Plaza County Hall London\",\"address1\":\"1 Addington Street\",\"city\":\"London\",\"postalCode\":\"SE1 7RY\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":66,\"amenityMask\":3637259,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near London Aquarium\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Park Plaza County Hall London in London (South Bank - Waterloo), you&apos;ll be minutes from London Aquarium and London Eye. This 4-star hotel is close to\",\"highRate\":179,\"lowRate\":143.65,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.50123,\"longitude\":-0.11621,\"proximityDistance\":0.78963476,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/2000000\\/1750000\\/1742200\\/1742184\\/1742184_75_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/258400\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200709448,\"rateCode\":203520160,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Twin Room, 2 Single Beds\",\"promoId\":201870078,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":1742184,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902073\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"174.0\",\"@averageRate\":\"147.9\",\"@commissionableUsdTotal\":\"480.74\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"31.06\",\"@eanCompensationOnline\":\"51.76\",\"@maxNightlyRate\":\"152.15\",\"@nightlyRateTotal\":\"295.8\",\"@grossProfitOffline\":\"28.01\",\"@grossProfitOnline\":\"47.98\",\"@total\":\"295.8\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"179.0\",\"@rate\":\"152.15\",\"@promo\":\"true\"},{\"@baseRate\":\"169.0\",\"@rate\":\"143.65\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"6\",\"hotelId\":275564,\"name\":\"Grange St. Paul&apos;s\",\"address1\":\"10 Godliman Street\",\"city\":\"London\",\"postalCode\":\"EC4V 5AJ\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":5,\"confidenceRating\":56,\"amenityMask\":3309570,\"tripAdvisorRating\":4,\"locationDescription\":\"Near St. Paul&apos;s Cathedral\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Grange St. Paul&apos;s, you&apos;ll be centrally located in London, steps from St. Paul&apos;s Cathedral and minutes from London Stock Exchange. This 5-star hotel is\",\"highRate\":159,\"lowRate\":159,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51267,\"longitude\":-0.09916,\"proximityDistance\":1.6969413,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/2000000\\/1460000\\/1458000\\/1457972\\/1457972_99_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/275564\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":451542,\"rateCode\":1634820,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Room, 1 Double Bed\",\"currentAllotment\":15,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":1457972,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902072\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"159.0\",\"@averageRate\":\"159.0\",\"@commissionableUsdTotal\":\"516.82\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"62.96\",\"@eanCompensationOnline\":\"89.68\",\"@maxNightlyRate\":\"159.0\",\"@nightlyRateTotal\":\"318.0\",\"@grossProfitOffline\":\"59.03\",\"@grossProfitOnline\":\"84.79\",\"@surchargeTotal\":\"63.6\",\"@total\":\"381.6\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"159.0\",\"@rate\":\"159.0\",\"@promo\":\"false\"},{\"@baseRate\":\"159.0\",\"@rate\":\"159.0\",\"@promo\":\"false\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"63.6\"}}}}}}},{\"@order\":\"7\",\"hotelId\":368307,\"name\":\"DoubleTree by Hilton Hotel London -Tower of London\",\"address1\":\"7 Pepys Street\",\"city\":\"London\",\"postalCode\":\"EC3N 4AF\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":95,\"amenityMask\":1212418,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near Tower of London\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at DoubleTree by Hilton Hotel London -Tower of London places you in the heart of London, walking distance from Tower of London and Tower Hill Pageant. This\",\"highRate\":149,\"lowRate\":149,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51071,\"longitude\":-0.07829,\"proximityDistance\":2.1016016,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/4000000\\/3980000\\/3978000\\/3977996\\/3977996_77_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/368307\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200148483,\"rateCode\":200827910,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Queen guest  room\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":3977996,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902071\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"149.0\",\"@averageRate\":\"149.0\",\"@commissionableUsdTotal\":\"484.32\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"20.85\",\"@eanCompensationOnline\":\"41.71\",\"@maxNightlyRate\":\"149.0\",\"@nightlyRateTotal\":\"298.0\",\"@grossProfitOffline\":\"17.78\",\"@grossProfitOnline\":\"37.90\",\"@total\":\"298.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"149.0\",\"@rate\":\"149.0\",\"@promo\":\"false\"},{\"@baseRate\":\"149.0\",\"@rate\":\"149.0\",\"@promo\":\"false\"}]}}}}}},{\"@order\":\"8\",\"hotelId\":105995,\"name\":\"Lancaster London\",\"address1\":\"Lancaster Terrace\",\"city\":\"London\",\"postalCode\":\"W2 2TY\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":43,\"amenityMask\":2195457,\"tripAdvisorRating\":4,\"locationDescription\":\"Near Kensington Gardens\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Lancaster London, you&apos;ll be centrally located in London, convenient to Kensington Gardens and Royal Albert Hall. This 4-star hotel is close to\",\"highRate\":182.5,\"lowRate\":119.25,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51246,\"longitude\":-0.17493,\"proximityDistance\":3.052724,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/520000\\/518100\\/518026\\/518026_76_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/105995\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200050411,\"rateCode\":203094924,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Double Room - Advance Purchase\",\"promoId\":205185459,\"promoDescription\":\"Seasonal deal: save 10%\",\"currentAllotment\":15,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":518026,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902070\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"157.5\",\"@averageRate\":\"141.75\",\"@commissionableUsdTotal\":\"460.75\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"49.33\",\"@eanCompensationOnline\":\"73.15\",\"@maxNightlyRate\":\"164.25\",\"@nightlyRateTotal\":\"283.5\",\"@grossProfitOffline\":\"45.83\",\"@grossProfitOnline\":\"68.79\",\"@surchargeTotal\":\"56.7\",\"@total\":\"340.2\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"182.5\",\"@rate\":\"164.25\",\"@promo\":\"true\"},{\"@baseRate\":\"132.5\",\"@rate\":\"119.25\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"56.7\"}}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"9\",\"hotelId\":112709,\"name\":\"St. James&apos; Court, A Taj Hotel, London\",\"address1\":\"45-51 Buckingham Gate\",\"city\":\"London\",\"postalCode\":\"SW1E 6AF\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":56,\"amenityMask\":3637259,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near Buckingham Palace\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at St. James&apos; Court, A Taj Hotel, London, you&apos;ll be centrally located in London, minutes from St. James Park and Buckingham Palace. This 4-star hotel is\",\"highRate\":254,\"lowRate\":206.1,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49876,\"longitude\":-0.1374,\"proximityDistance\":1.1819049,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/590000\\/582600\\/582514\\/582514_353_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/112709\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200535103,\"rateCode\":202812576,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Executive Room\",\"promoId\":205030505,\"promoDescription\":\"Save 10%\",\"currentAllotment\":62,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":582514,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206F\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"241.5\",\"@averageRate\":\"217.35\",\"@commissionableUsdTotal\":\"706.48\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"71.73\",\"@eanCompensationOnline\":\"102.16\",\"@maxNightlyRate\":\"228.6\",\"@nightlyRateTotal\":\"434.7\",\"@grossProfitOffline\":\"67.25\",\"@grossProfitOnline\":\"96.60\",\"@total\":\"434.7\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"254.0\",\"@rate\":\"228.6\",\"@promo\":\"true\"},{\"@baseRate\":\"229.0\",\"@rate\":\"206.1\",\"@promo\":\"true\"}]}}}}}},{\"@order\":\"10\",\"hotelId\":400461,\"name\":\"Apex Temple Court Hotel\",\"address1\":\"1-2 Serjeants Inn\",\"address2\":\"Fleet Street\",\"city\":\"London\",\"postalCode\":\"EC4Y 1LL\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":51,\"amenityMask\":1212418,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near St. Paul&apos;s Cathedral\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Apex Temple Court Hotel, you&apos;ll be centrally located in London, steps from London Temple Church and Dr. Johnsons&apos; House. This 4-star hotel is close to\",\"highRate\":300,\"lowRate\":240,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51362,\"longitude\":-0.10843,\"proximityDistance\":1.6564101,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/5000000\\/4840000\\/4837000\\/4836998\\/4836998_35_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/400461\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200354464,\"rateCode\":202164036,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":18,\"roomDescription\":\"Superior King\",\"promoId\":203219031,\"promoDescription\":\"Seasonal deal: save 20%\",\"currentAllotment\":32,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":4836998,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206E\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"300.0\",\"@averageRate\":\"240.0\",\"@commissionableUsdTotal\":\"780.11\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"55.20\",\"@eanCompensationOnline\":\"88.80\",\"@maxNightlyRate\":\"240.0\",\"@nightlyRateTotal\":\"480.0\",\"@grossProfitOffline\":\"50.26\",\"@grossProfitOnline\":\"82.66\",\"@total\":\"480.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"300.0\",\"@rate\":\"240.0\",\"@promo\":\"true\"},{\"@baseRate\":\"300.0\",\"@rate\":\"240.0\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"11\",\"hotelId\":105992,\"name\":\"Melia White House Hotel\",\"address1\":\"Regents Park\",\"address2\":\"Albany Street\",\"city\":\"London\",\"postalCode\":\"NW1 3UP\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":3,\"amenityMask\":7831555,\"tripAdvisorRating\":4,\"locationDescription\":\"Near BT Tower\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;Melia White House Hotel is in the heart of London, walking distance from Crescent Gardens and Park Square Garden. This 4-star hotel is close to Selfridges and London\",\"highRate\":157.5,\"lowRate\":119.25,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.52475,\"longitude\":-0.14389,\"proximityDistance\":2.731976,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/530000\\/525900\\/525818\\/525818_228_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/105992\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":5060,\"rateCode\":201251325,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Classic Room, 1 King Bed\",\"promoId\":2657228,\"promoDescription\":\"Book early and save 10%\",\"currentAllotment\":37,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":525818,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206D\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"145.0\",\"@averageRate\":\"130.5\",\"@commissionableUsdTotal\":\"424.18\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"42.30\",\"@eanCompensationOnline\":\"64.22\",\"@maxNightlyRate\":\"141.75\",\"@nightlyRateTotal\":\"261.0\",\"@grossProfitOffline\":\"39.07\",\"@grossProfitOnline\":\"60.21\",\"@surchargeTotal\":\"52.2\",\"@total\":\"313.2\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"157.5\",\"@rate\":\"141.75\",\"@promo\":\"true\"},{\"@baseRate\":\"132.5\",\"@rate\":\"119.25\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"52.2\"}}}}}}},{\"@order\":\"12\",\"hotelId\":427926,\"name\":\"London City Suites by Montcalm\",\"address1\":\"42-46 Chiswell Street\",\"city\":\"London\",\"postalCode\":\"EC1Y 4SB\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":4,\"hotelRating\":5,\"confidenceRating\":52,\"amenityMask\":32771,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near St. Paul&apos;s Cathedral\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at London City Suites by Montcalm, you&apos;ll be centrally located in London, steps from Barbican Arts Centre and minutes from St. Giles Cripplegate. This\",\"highRate\":175,\"lowRate\":138.6,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.52087,\"longitude\":-0.09091,\"proximityDistance\":2.3580592,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/5000000\\/4930000\\/4924000\\/4923931\\/4923931_4_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/427926\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200262665,\"rateCode\":201543286,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Deluxe Double Room\",\"promoId\":204826672,\"promoDescription\":\"Sale! Save 12% on this Stay.\",\"currentAllotment\":17,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":4923931,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206C\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"166.25\",\"@averageRate\":\"146.3\",\"@commissionableUsdTotal\":\"475.54\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"40.37\",\"@eanCompensationOnline\":\"64.95\",\"@maxNightlyRate\":\"154.0\",\"@nightlyRateTotal\":\"292.6\",\"@grossProfitOffline\":\"36.76\",\"@grossProfitOnline\":\"60.46\",\"@surchargeTotal\":\"58.52\",\"@total\":\"351.12\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"175.0\",\"@rate\":\"154.0\",\"@promo\":\"true\"},{\"@baseRate\":\"157.5\",\"@rate\":\"138.6\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"58.52\"}}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"13\",\"hotelId\":114516,\"name\":\"The Kensington Close Hotel &amp; Spa\",\"address1\":\"Wrights Lane\",\"address2\":\"Kensington\",\"city\":\"London\",\"postalCode\":\"W8 5SP\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":3,\"amenityMask\":491522,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Royal Albert Hall\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at The Kensington Close Hotel &amp; Spa places you in the heart of London, minutes from Kensington Roof Gardens and close to Royal Albert Hall. This 4-star hotel\",\"highRate\":109.17,\"lowRate\":89.17,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49894,\"longitude\":-0.19211,\"proximityDistance\":3.4218426,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/10000\\/9800\\/9789\\/9789_76_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/114516\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":15891,\"rateCode\":15891,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Standard Double Room\",\"currentAllotment\":26,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":9789,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206B\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"99.17\",\"@averageRate\":\"99.17\",\"@commissionableUsdTotal\":\"322.35\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"39.27\",\"@eanCompensationOnline\":\"55.93\",\"@maxNightlyRate\":\"109.17\",\"@nightlyRateTotal\":\"198.34\",\"@grossProfitOffline\":\"36.82\",\"@grossProfitOnline\":\"52.88\",\"@surchargeTotal\":\"39.68\",\"@total\":\"238.02\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"109.17\",\"@rate\":\"109.17\",\"@promo\":\"false\"},{\"@baseRate\":\"89.17\",\"@rate\":\"89.17\",\"@promo\":\"false\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"39.68\"}}}}}}},{\"@order\":\"14\",\"hotelId\":150369,\"name\":\"Millennium Bailey&apos;s Hotel London Kensington\",\"address1\":\"140 Gloucester Road\",\"city\":\"London\",\"postalCode\":\"SW7 4QH\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":72,\"amenityMask\":32768,\"tripAdvisorRating\":4,\"locationDescription\":\"Near Kensington Roof Gardens\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at Millennium Bailey&apos;s Hotel London Kensington places you in the heart of London, minutes from London Natural History Museum and close to Imperial College\",\"highRate\":187.2,\"lowRate\":158.4,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49413,\"longitude\":-0.18255,\"proximityDistance\":2.9677556,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/530000\\/523300\\/523223\\/523223_103_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/150369\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":24257,\"rateCode\":1885416,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Standard Double Room - Non Refundable\",\"currentAllotment\":44,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":523223,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A590206A\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"172.79999\",\"@averageRate\":\"172.79999\",\"@commissionableUsdTotal\":\"561.68\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"53.57\",\"@eanCompensationOnline\":\"77.77\",\"@maxNightlyRate\":\"187.2\",\"@nightlyRateTotal\":\"345.59998\",\"@grossProfitOffline\":\"50.01\",\"@grossProfitOnline\":\"73.34\",\"@total\":\"345.6\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"187.2\",\"@rate\":\"187.2\",\"@promo\":\"false\"},{\"@baseRate\":\"158.4\",\"@rate\":\"158.4\",\"@promo\":\"false\"}]}}}}}},{\"@order\":\"15\",\"hotelId\":105996,\"name\":\"Millennium Gloucester Hotel London Kensington\",\"address1\":\"4-18 Harrington Gardens\",\"city\":\"London\",\"postalCode\":\"SW7 4LH\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":92,\"amenityMask\":32779,\"tripAdvisorRating\":4,\"locationDescription\":\"Near Imperial College London\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Millennium Gloucester Hotel London Kensington in London (Kensington - Earl&apos;s Court), you&apos;ll be minutes from London Natural History Museum and Imperial\",\"highRate\":235.2,\"lowRate\":148.8,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49323,\"longitude\":-0.18292,\"proximityDistance\":2.9781933,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/530000\\/523300\\/523228\\/523228_94_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/105996\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200632737,\"rateCode\":203042571,\"maxRoomOccupancy\":3,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Club Double Room - Advance Purchase\",\"currentAllotment\":211,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":523228,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902069\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"192.0\",\"@averageRate\":\"192.0\",\"@commissionableUsdTotal\":\"624.09\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"59.52\",\"@eanCompensationOnline\":\"86.40\",\"@maxNightlyRate\":\"235.2\",\"@nightlyRateTotal\":\"384.0\",\"@grossProfitOffline\":\"55.56\",\"@grossProfitOnline\":\"81.48\",\"@total\":\"384.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"235.2\",\"@rate\":\"235.2\",\"@promo\":\"false\"},{\"@baseRate\":\"148.8\",\"@rate\":\"148.8\",\"@promo\":\"false\"}]}}},\"ValueAdds\":{\"@size\":\"2\",\"ValueAdd\":[{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"},{\"@id\":\"2\",\"description\":\"Continental Breakfast\"}]}}}},{\"@order\":\"16\",\"hotelId\":223825,\"name\":\"The Soho Hotel\",\"address1\":\"4 Richmond Mews\",\"address2\":\"Dean Street\",\"city\":\"London\",\"postalCode\":\"W1D 3DH\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":5,\"confidenceRating\":90,\"amenityMask\":3375106,\"tripAdvisorRating\":5,\"locationDescription\":\"Near Trafalgar Square\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;The Soho Hotel is in the heart of London, walking distance from Prince Edward Theatre and Queens Theatre. This 5-star hotel is close to Trafalgar Square and King&apos;s\",\"highRate\":432,\"lowRate\":432,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51413,\"longitude\":-0.13364,\"proximityDistance\":1.8775741,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/2000000\\/1120000\\/1112700\\/1112611\\/1112611_66_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/223825\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200077386,\"rateCode\":200461324,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Luxury Room, 1 King Bed\",\"currentAllotment\":1,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":1112611,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902068\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"432.0\",\"@averageRate\":\"432.0\",\"@commissionableUsdTotal\":\"1404.19\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"142.56\",\"@eanCompensationOnline\":\"203.04\",\"@maxNightlyRate\":\"432.0\",\"@nightlyRateTotal\":\"864.0\",\"@grossProfitOffline\":\"133.66\",\"@grossProfitOnline\":\"191.98\",\"@total\":\"864.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"432.0\",\"@rate\":\"432.0\",\"@promo\":\"false\"},{\"@baseRate\":\"432.0\",\"@rate\":\"432.0\",\"@promo\":\"false\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}},{\"@order\":\"17\",\"hotelId\":172847,\"name\":\"The Savoy, A Fairmont Managed Hotel\",\"address1\":\"Strand\",\"city\":\"London\",\"postalCode\":\"WC2R 0EU\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":5,\"confidenceRating\":52,\"amenityMask\":1540099,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near King&apos;s College London\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at The Savoy, A Fairmont Managed Hotel, you&apos;ll be centrally located in London, steps from Lyceum Theatre and Victoria Embankment Gardens. This 5-star\",\"highRate\":395.03,\"lowRate\":301.76,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51038,\"longitude\":-0.12091,\"proximityDistance\":1.4474381,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/30000\\/27200\\/27158\\/27158_88_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/172847\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200003723,\"rateCode\":200375799,\"maxRoomOccupancy\":3,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Deluxe Room, 1 King Bed, City View\",\"promoId\":203871193,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":4,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":27158,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902067\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"375.02002\",\"@averageRate\":\"318.77002\",\"@commissionableUsdTotal\":\"1036.15\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"87.98\",\"@eanCompensationOnline\":\"141.53\",\"@maxNightlyRate\":\"335.78\",\"@nightlyRateTotal\":\"637.54004\",\"@grossProfitOffline\":\"80.10\",\"@grossProfitOnline\":\"131.74\",\"@surchargeTotal\":\"127.5\",\"@total\":\"765.04\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"395.03\",\"@rate\":\"335.78\",\"@promo\":\"true\"},{\"@baseRate\":\"355.01\",\"@rate\":\"301.76\",\"@promo\":\"true\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"127.5\"}}}}}}},{\"@order\":\"18\",\"hotelId\":175392,\"name\":\"St. Ermins Hotel, Autograph Collection\",\"address1\":\"Caxton Street 2\",\"city\":\"London\",\"postalCode\":\"SW1H 0QW\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":74,\"amenityMask\":32769,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near St. James Park\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at St. Ermins Hotel, Autograph Collection places you in the heart of London, walking distance from Westminster Cathedral and Queen Elizabeth II Conference\",\"highRate\":188,\"lowRate\":188,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49864,\"longitude\":-0.13436,\"proximityDistance\":1.0674285,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/440000\\/436300\\/436250\\/436250_153_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/175392\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200300246,\"rateCode\":201797816,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":3,\"roomDescription\":\"Standard Room, 1 Queen Bed\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":436250,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902066\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"188.0\",\"@averageRate\":\"188.0\",\"@commissionableUsdTotal\":\"611.08\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"35.72\",\"@eanCompensationOnline\":\"62.04\",\"@maxNightlyRate\":\"188.0\",\"@nightlyRateTotal\":\"376.0\",\"@grossProfitOffline\":\"31.85\",\"@grossProfitOnline\":\"57.23\",\"@total\":\"376.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"188.0\",\"@rate\":\"188.0\",\"@promo\":\"false\"},{\"@baseRate\":\"188.0\",\"@rate\":\"188.0\",\"@promo\":\"false\"}]}}}}}},{\"@order\":\"19\",\"hotelId\":105509,\"name\":\"The Regency Hotel\",\"address1\":\"100 Queens Gate\",\"address2\":\"South Kensington\",\"city\":\"London\",\"postalCode\":\"SW7 5AG\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":85,\"amenityMask\":32778,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Imperial College London\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at The Regency Hotel in London (Kensington - Earl&apos;s Court), you&apos;ll be minutes from London Natural History Museum and close to Imperial College London.\",\"highRate\":130,\"lowRate\":110,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49247,\"longitude\":-0.17846,\"proximityDistance\":2.7831106,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/30000\\/22500\\/22478\\/22478_77_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/105509\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":482428,\"rateCode\":1873714,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Club Double Room\",\"currentAllotment\":3,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":22478,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902065\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"120.0\",\"@averageRate\":\"120.0\",\"@commissionableUsdTotal\":\"390.05\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"41.76\",\"@eanCompensationOnline\":\"61.92\",\"@maxNightlyRate\":\"130.0\",\"@nightlyRateTotal\":\"240.0\",\"@grossProfitOffline\":\"38.79\",\"@grossProfitOnline\":\"58.23\",\"@surchargeTotal\":\"48.0\",\"@total\":\"288.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"130.0\",\"@rate\":\"130.0\",\"@promo\":\"false\"},{\"@baseRate\":\"110.0\",\"@rate\":\"110.0\",\"@promo\":\"false\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"48.0\"}}}}}}},{\"@order\":\"20\",\"hotelId\":211452,\"name\":\"Saint Georges Hotel\",\"address1\":\"Langham Place, Regent Street\",\"city\":\"London\",\"postalCode\":\"W1B 2QS\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":3,\"amenityMask\":32768,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Selfridges\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;Saint Georges Hotel is in the heart of London, walking distance from Oxford Circus and Wigmore Hall. This 4-star hotel is close to Selfridges and Trafalgar\",\"highRate\":143.33,\"lowRate\":85,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51781,\"longitude\":-0.14312,\"proximityDistance\":2.3021588,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/10000\\/100\\/1\\/1_6_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/211452\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":387110,\"rateCode\":1307058,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Standard Double Room\",\"currentAllotment\":3,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":1,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902064\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"114.165\",\"@averageRate\":\"114.165\",\"@commissionableUsdTotal\":\"371.09\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"45.21\",\"@eanCompensationOnline\":\"64.39\",\"@maxNightlyRate\":\"143.33\",\"@nightlyRateTotal\":\"228.33\",\"@grossProfitOffline\":\"42.39\",\"@grossProfitOnline\":\"60.88\",\"@surchargeTotal\":\"45.67\",\"@total\":\"274.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"143.33\",\"@rate\":\"143.33\",\"@promo\":\"false\"},{\"@baseRate\":\"85.0\",\"@rate\":\"85.0\",\"@promo\":\"false\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"45.67\"}}}}}}},{\"@order\":\"21\",\"hotelId\":211924,\"name\":\"DoubleTree by Hilton Hotel London - Westminster\",\"address1\":\"30 John Islip Street\",\"address2\":\"Westminster\",\"city\":\"London\",\"postalCode\":\"SW1P 4QP\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":95,\"amenityMask\":1343490,\"tripAdvisorRating\":4.5,\"locationDescription\":\"Near Tate Britain\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;DoubleTree by Hilton Hotel London - Westminster is in the heart of London, walking distance from Tate Britain and Lambeth Bridge. This 4-star hotel is close to Big\",\"highRate\":199,\"lowRate\":199,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49324,\"longitude\":-0.12735,\"proximityDistance\":0.62289983,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/920000\\/914200\\/914179\\/914179_46_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/211924\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200136378,\"rateCode\":200768840,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Room\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":914179,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902063\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"199.0\",\"@averageRate\":\"199.0\",\"@commissionableUsdTotal\":\"646.84\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"27.85\",\"@eanCompensationOnline\":\"55.71\",\"@maxNightlyRate\":\"199.0\",\"@nightlyRateTotal\":\"398.0\",\"@grossProfitOffline\":\"23.75\",\"@grossProfitOnline\":\"50.62\",\"@total\":\"398.0\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"199.0\",\"@rate\":\"199.0\",\"@promo\":\"false\"},{\"@baseRate\":\"199.0\",\"@rate\":\"199.0\",\"@promo\":\"false\"}]}}}}}},{\"@order\":\"22\",\"hotelId\":406287,\"name\":\"Tune Hotel - Westminster, London\",\"address1\":\"118 - 120 Westminster Bridge Road\",\"city\":\"London\",\"postalCode\":\"SE1 7RW\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":2.5,\"confidenceRating\":52,\"amenityMask\":1179648,\"tripAdvisorRating\":4,\"locationDescription\":\"Near London Eye\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Tune Hotel - Westminster, London in London (South Bank - Waterloo), you&apos;ll be minutes from Florence Nightingale Museum and London Aquarium. This hotel\",\"highRate\":104.17,\"lowRate\":62.5,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49901,\"longitude\":-0.11271,\"proximityDistance\":0.63210154,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/6000000\\/5050000\\/5040300\\/5040269\\/5040269_17_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/406287\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200162732,\"rateCode\":200920282,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":1,\"roomDescription\":\"Double Room\",\"currentAllotment\":6,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":5040269,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902062\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"false\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"83.335\",\"@averageRate\":\"83.335\",\"@commissionableUsdTotal\":\"270.88\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"23.01\",\"@eanCompensationOnline\":\"37.01\",\"@maxNightlyRate\":\"104.17\",\"@nightlyRateTotal\":\"166.67\",\"@grossProfitOffline\":\"20.95\",\"@grossProfitOnline\":\"34.45\",\"@surchargeTotal\":\"33.34\",\"@total\":\"200.01\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"104.17\",\"@rate\":\"104.17\",\"@promo\":\"false\"},{\"@baseRate\":\"62.5\",\"@rate\":\"62.5\",\"@promo\":\"false\"}]},\"Surcharges\":{\"@size\":\"1\",\"Surcharge\":{\"@type\":\"TaxAndServiceFee\",\"@amount\":\"33.34\"}}}}}}},{\"@order\":\"23\",\"hotelId\":198280,\"name\":\"Park Plaza Victoria London\",\"address1\":\"239 Vauxhall Bridge Road\",\"city\":\"London\",\"postalCode\":\"SW1V 1EQ\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":66,\"amenityMask\":1277963,\"tripAdvisorRating\":4,\"locationDescription\":\"Near St. James Park\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;A stay at Park Plaza Victoria London places you in the heart of London, walking distance from Apollo Victoria Theatre and Westminster Cathedral. This 4-star hotel is\",\"highRate\":199,\"lowRate\":169.15,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.49427,\"longitude\":-0.14101,\"proximityDistance\":1.2046409,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/810000\\/801800\\/801796\\/801796_135_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/198280\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":93012,\"rateCode\":201252882,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":0,\"roomDescription\":\"Superior Room\",\"promoId\":201869974,\"promoDescription\":\"Book early and save 15%\",\"currentAllotment\":0,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":801796,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902061\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"false\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"199.0\",\"@averageRate\":\"169.15\",\"@commissionableUsdTotal\":\"549.81\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"35.52\",\"@eanCompensationOnline\":\"59.21\",\"@maxNightlyRate\":\"169.15\",\"@nightlyRateTotal\":\"338.3\",\"@grossProfitOffline\":\"32.04\",\"@grossProfitOnline\":\"54.88\",\"@total\":\"338.3\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"199.0\",\"@rate\":\"169.15\",\"@promo\":\"true\"},{\"@baseRate\":\"199.0\",\"@rate\":\"169.15\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"1024\",\"description\":\"Free High-Speed Internet\"}}}}},{\"@order\":\"24\",\"hotelId\":144800,\"name\":\"Corus Hotel Hyde Park\",\"address1\":\"Lancaster Gate\",\"city\":\"London\",\"postalCode\":\"W2 3LG\",\"countryCode\":\"GB\",\"airportCode\":\"LHR\",\"supplierType\":\"E\",\"propertyCategory\":1,\"hotelRating\":4,\"confidenceRating\":43,\"amenityMask\":1146883,\"tripAdvisorRating\":3.5,\"locationDescription\":\"Near Kensington Gardens\",\"shortDescription\":\"&lt;p&gt;&lt;b&gt;Property Location&lt;\\/b&gt; &lt;br \\/&gt;With a stay at Corus Hotel Hyde Park, you&apos;ll be centrally located in London, convenient to Kensington Gardens and Royal Albert Hall. This 4-star hotel is close to\",\"highRate\":136.8,\"lowRate\":91.44,\"rateCurrencyCode\":\"GBP\",\"latitude\":51.51175,\"longitude\":-0.17775,\"proximityDistance\":3.1337087,\"proximityUnit\":\"MI\",\"hotelInDestination\":true,\"thumbNailUrl\":\"\\/hotels\\/1000000\\/430000\\/428500\\/428403\\/428403_76_t.jpg\",\"deepLink\":\"http:\\/\\/www.travelnow.com\\/templates\\/55505\\/hotels\\/144800\\/overview?lang=en&amp;currency=GBP&amp;standardCheckin=10\\/18\\/2014&amp;standardCheckout=10\\/20\\/2014&amp;roomsCount=1&amp;rooms[0].adultsCount=2\",\"RoomRateDetailsList\":{\"RoomRateDetails\":{\"roomTypeCode\":200320818,\"rateCode\":201970782,\"maxRoomOccupancy\":2,\"quotedRoomOccupancy\":2,\"minGuestAge\":18,\"roomDescription\":\"Standard Double\",\"promoId\":206575178,\"promoDescription\":\"Book now and save!\",\"currentAllotment\":8,\"propertyAvailable\":true,\"propertyRestricted\":false,\"expediaPropertyId\":428403,\"rateKey\":\"0ABAAACB-7631-2914-8882-E276A5902060\",\"RateInfo\":{\"@priceBreakdown\":\"true\",\"@promo\":\"true\",\"@rateChange\":\"true\",\"ChargeableRateInfo\":{\"@averageBaseRate\":\"125.55\",\"@averageRate\":\"100.44\",\"@commissionableUsdTotal\":\"326.47\",\"@currencyCode\":\"GBP\",\"@eanCompensationOffline\":\"19.09\",\"@eanCompensationOnline\":\"33.15\",\"@maxNightlyRate\":\"109.44\",\"@nightlyRateTotal\":\"200.88\",\"@grossProfitOffline\":\"17.02\",\"@grossProfitOnline\":\"30.58\",\"@total\":\"200.88\",\"NightlyRatesPerRoom\":{\"@size\":\"2\",\"NightlyRate\":[{\"@baseRate\":\"136.8\",\"@rate\":\"109.44\",\"@promo\":\"true\"},{\"@baseRate\":\"114.3\",\"@rate\":\"91.44\",\"@promo\":\"true\"}]}}},\"ValueAdds\":{\"@size\":\"1\",\"ValueAdd\":{\"@id\":\"2048\",\"description\":\"Free Wireless Internet\"}}}}}]}}}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testDeserAskGeo() {
    try {
      final String deser = "{\"code\":0,\"message\":\"ok\",\"data\":[{\"TimeZone\":{\"IsInside\":\"false\",\"AskGeoId\":20451,\"MinDistanceKm\":0.44946358,\"TimeZoneId\":\"Europe/London\",\"ShortName\":\"GMT\",\"CurrentOffsetMs\":0,\"WindowsStandardName\":\"GMT Standard Time\",\"InDstNow\":\"false\"}}]}";
      final JDoc doc = WealdMapper.getServerMapper().readValue(deser, JDoc.class);
    } catch (final IOException e) {
      fail("Failed JSON processing: ", e);
    }
  }

  public static class JDocHolder
  {
    public JDoc doc;

    @JsonCreator
    public JDocHolder(@JsonProperty("doc") final JDoc doc)
    {
      this.doc = doc;
    }
  }

  @Test
  public void testDeserEmptySub()
  {
    try
    {
      final String deser = "{\"doc\":{}}";
      final JDocHolder holder = WealdMapper.getServerMapper().readValue(deser, JDocHolder.class);
      assertNotNull(holder.doc);
      assertTrue(holder.doc.isEmpty());
    }
    catch (final IOException e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  public static class UpcastClass
  {
    public boolean sub1;
    public int sub2;
    public InetSocketAddress sub3;

    @JsonCreator
    public UpcastClass(@JsonProperty("sub1") boolean sub1,
                       @JsonProperty("sub2") int sub2,
                       @JsonProperty("sub3") InetSocketAddress sub3)
    {
      this.sub1 = sub1;
      this.sub2 = sub2;
      this.sub3 = sub3;
    }
  }

  @Test
  public void testUpcast()
  {
    final ImmutableMap<String, Object> subMap =
      ImmutableMap.<String, Object>of("sub1", Boolean.TRUE, "sub2", 5, "sub3", new InetSocketAddress("1.2.3.4", 80));
    final JDoc subDoc = new JDoc(subMap);
    final ImmutableMap<String, Object> map =
      ImmutableMap.<String, Object>of("val1", new DateTime(2014, 9, 16, 23, 0, 0, DateTimeZone.UTC), "val2", subDoc);
    final JDoc doc = new JDoc(map);

    final UpcastClass upcast = doc.get("val2", UpcastClass.class).orNull();
    assertNotNull(upcast);
  }

  @Test
  public void testDeserMap()
  {
    final ImmutableMap<String, InetSocketAddress> addrs =
      ImmutableMap.of("one", new InetSocketAddress("1.1.1.1", 80), "two", new InetSocketAddress("2.2.2.2", 80));
    final JDoc doc = new JDoc(ImmutableMap.<String, Object>of("addrs", addrs));

    final Optional<ImmutableMap<String, InetSocketAddress>> obtainAddrs =
      doc.get("addrs", new TypeReference<ImmutableMap<String, InetSocketAddress>>() {});
    assert (obtainAddrs.isPresent());
    assert (obtainAddrs.get().equals(addrs));
  }


  public static class DeserObjectCollectionClass
  {
    public String str;
    public InetSocketAddress addr;

    @JsonCreator
    public DeserObjectCollectionClass(@JsonProperty("str") final String str,
                                      @JsonProperty("addr") final InetSocketAddress addr)
    {
      this.str = str;
      this.addr = addr;
    }
  }

  @Test
  public void testDeserObjectCollection()
  {
    try
    {
      final ImmutableList<DeserObjectCollectionClass> objects =
        ImmutableList.of(new DeserObjectCollectionClass("one", new InetSocketAddress("1.1.1.1", 80)),
                         new DeserObjectCollectionClass("two", new InetSocketAddress("2.2.2.2", 80)));

      final JDoc doc = new JDoc(ImmutableMap.<String, Object>of("objs", objects));

      final String docStr = WealdMapper.getServerMapper().writeValueAsString(doc);

      final JDoc reDoc = WealdMapper.getServerMapper().readValue(docStr, JDoc.class);

      final Collection<DeserObjectCollectionClass> deserObjects =
        reDoc.get("objs", new TypeReference<ImmutableList<DeserObjectCollectionClass>>() {}).orNull();
      assertNotNull(deserObjects);
      final Iterator<DeserObjectCollectionClass> it = deserObjects.iterator();
      final DeserObjectCollectionClass obj0 = it.next();
      final DeserObjectCollectionClass obj1 = it.next();
    }
    catch (final Exception e)
    {
      fail("Failed JSON processing: ", e);
    }
  }

  @Test
  public void testNull()
  {
    final JDoc doc = new JDoc(ImmutableMap.<String, Object>of("testkey", "testval"));
    final String keyStr = doc.get("badkey", String.class).orNull();
    assertNull(keyStr);
    final Set<String> keyStrSet = doc.get("badkey", new TypeReference<Set<String>>(){}).orNull();
    assertNull(keyStrSet);
  }

  public class JDocExtClass extends JDoc
  {
    public JDocExtClass(final ImmutableMap<String, Object> data)
    {
      super(data);
    }
  }

  @Test
  public void testExtClass() throws IOException
  {
      final JDocExtClass doc = new JDocExtClass(ImmutableMap.<String, Object>of("testkey", "testval"));
      final String docSer = WealdMapper.getServerMapper().writeValueAsString(doc);
  }
}

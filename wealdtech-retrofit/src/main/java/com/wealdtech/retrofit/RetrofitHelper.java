/*
 * Copyright 2012 - 2016 Weald Technology Trading Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.wealdtech.retrofit;

import com.wealdtech.ClientError;
import com.wealdtech.DataError;
import com.wealdtech.jackson.WealdMapper;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RetrofitHelper
{
  private static final Logger LOG = LoggerFactory.getLogger(RetrofitHelper.class);

  /**
   * Create a Retrofit service with appropriate settings
   * @param baseUri the base URI of the retrofit service
   * @param service the service to create
   * @param <T> the class of the service to create
   * @return the retrofit service
   */
  public static <T> T createRetrofit(final String baseUri, final Class<T> service)
  {
    return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                 .addConverterFactory(JacksonConverterFactory.create(WealdMapper.getMapper()))
                                 .baseUrl(baseUri)
                                 .build()
                                 .create(service);
  }

  /**
   * Create a Retrofit service with appropriate settings
   * @param baseUri the base URI of the retrofit service
   * @param service the service to create
   * @param client the custom OkHttp client
   * @param <T> the class of the service to create
   * @return the retrofit service
   */
  public static <T> T createRetrofit(final String baseUri, final Class<T> service, OkHttpClient client)
  {
    return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                 .addConverterFactory(JacksonConverterFactory.create(WealdMapper.getMapper()))
                                 .baseUrl(baseUri)
                                 .client(client)
                                 .build()
                                 .create(service);
  }

  public static <T> T call(final Call<T> call)
  {
    final Response<T> response;
    try
    {
      response = call.execute();
    }
    catch (final IOException ioe)
    {
      LOG.warn("IO error: ", ioe);
      throw new ClientError("IO error: " + ioe.getMessage(), ioe);
    }
    if (response.isSuccessful())
    {
      return response.body();
    }
    else
    {
      String text = null;
      if (response.errorBody() != null)
      {
        try
        {
          text = response.errorBody().string();
        }
        catch (final IOException ignored) {}
      }
      switch (response.code())
      {
        case HttpURLConnection.HTTP_BAD_REQUEST:
          LOG.error("Bad request: {}", text);
          throw new DataError.Bad("Bad request");
        case HttpURLConnection.HTTP_UNAUTHORIZED:
          LOG.error("Unauthorized: {}", text);
          throw new DataError.Authentication("Unauthorized");
        case HttpURLConnection.HTTP_FORBIDDEN:
          LOG.error("Forbidden: {}", text);
          throw new DataError.Permission("Not allowed");
        case HttpURLConnection.HTTP_NOT_FOUND:
          LOG.error("Not found: {}", text);
          throw new DataError.Missing("Not found");
        default:
          LOG.warn("Error {}: {}", response.code(), text);
          throw new ClientError("API error: " + text);
      }
    }
  }
}

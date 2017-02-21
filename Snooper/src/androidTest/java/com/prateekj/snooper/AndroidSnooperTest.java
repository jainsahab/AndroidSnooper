package com.prateekj.snooper;

import com.prateekj.snooper.rules.RealmCleanRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.net.URI;

import io.realm.Realm;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.prateekj.snooper.utility.TestUtilities.toBytes;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public class AndroidSnooperTest {

  private AndroidSnooper androidSnooper;
  private Realm realm;

  @Rule
  public RealmCleanRule rule = new RealmCleanRule();

  @Before
  public void setUp() throws Exception {
    androidSnooper = AndroidSnooper.init(getTargetContext());
    Realm.init(getTargetContext());
    realm = Realm.getDefaultInstance();
  }

  @Test
  public void shouldSaveHttpCallViaSpringHttpRequestInterceptor() throws Exception {
    String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0";
    URI uri = create(url);
    String responseBody = "responseBody";
    String requestBody = "requestBody";
    ClientHttpResponse httpResponse = mock(ClientHttpResponse.class);
    when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream(toBytes(responseBody)));
    when(httpResponse.getStatusCode()).thenReturn(OK);
    when(httpResponse.getRawStatusCode()).thenReturn(200);
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.getMethod()).thenReturn(POST);
    when(httpRequest.getURI()).thenReturn(uri);
    ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
    when(execution.execute(httpRequest, toBytes(requestBody))).thenReturn(httpResponse);

    ClientHttpRequestInterceptor interceptor = androidSnooper;

    ClientHttpResponse clientHttpResponse = interceptor.intercept(httpRequest, toBytes(requestBody), execution);
    HttpCall httpCall = realm.where(HttpCall.class).findAll().first();

    assertThat(clientHttpResponse, sameInstance(httpResponse));
    assertThat(httpCall.getUrl(), is(url));
    assertThat(httpCall.getPayload(), is(requestBody));
    assertThat(httpCall.getMethod(), is("POST"));
    assertThat(httpCall.getResponseBody(), is(responseBody));
    assertThat(httpCall.getStatusCode(), is(200));
    assertThat(httpCall.getStatusText(), is("OK"));
  }
}
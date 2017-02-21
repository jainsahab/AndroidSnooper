package com.prateekj.snooper.transformer;

import com.prateekj.snooper.HttpCall;

import org.junit.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static com.prateekj.snooper.utility.TestUtilities.toBytes;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public class SpringHttpRequestTransformerTest {

  @Test
  public void shouldTransformHttpCallFromSpringHttpRequest() throws Exception {
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

    SpringHttpRequestTransformer transformer = new SpringHttpRequestTransformer();

    HttpCall httpCall = transformer.transform(httpRequest, toBytes(requestBody), httpResponse);

    assertThat(httpCall.getMethod(), is("POST"));
    assertThat(httpCall.getPayload(), is(requestBody));
    assertThat(httpCall.getUrl(), is(url));
    assertThat(httpCall.getResponseBody(), is(responseBody));
    assertThat(httpCall.getStatusText(), is("OK"));
    assertThat(httpCall.getStatusCode(), is(200));
  }
}
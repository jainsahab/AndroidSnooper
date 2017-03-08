package org.springframework.http.client;

import com.prateekj.snooper.model.HttpCall;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import static com.prateekj.snooper.model.HttpHeader.CONTENT_TYPE;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
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
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.put("Content-Type", Arrays.asList("application/json"));

    ClientHttpResponse httpResponse = mock(ClientHttpResponse.class);
    when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream(toBytes(responseBody)));
    when(httpResponse.getStatusCode()).thenReturn(OK);
    when(httpResponse.getRawStatusCode()).thenReturn(200);
    when(httpResponse.getHeaders()).thenReturn(httpHeaders);
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.getMethod()).thenReturn(POST);
    when(httpRequest.getURI()).thenReturn(uri);
    when(httpRequest.getHeaders()).thenReturn(httpHeaders);

    SpringHttpRequestTransformer transformer = new SpringHttpRequestTransformer();

    HttpCall httpCall = transformer.transform(httpRequest, toBytes(requestBody), httpResponse);

    assertThat(httpCall.getMethod(), is("POST"));
    assertThat(httpCall.getPayload(), is(requestBody));
    assertThat(httpCall.getUrl(), is(url));
    assertThat(httpCall.getResponseBody(), is(responseBody));
    assertThat(httpCall.getStatusText(), is("OK"));
    assertThat(httpCall.getStatusCode(), is(200));
    assertThat(httpCall.getRequestHeaders().size(), is(1));
    assertThat(httpCall.getResponseHeaders().size(), is(1));
    assertNotNull(httpCall.getResponseHeader(CONTENT_TYPE));
    assertNotNull(httpCall.getRequestHeader(CONTENT_TYPE));
  }

  public byte[] toBytes(String string) {
    try {
      return string.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new byte[0];
  }
}
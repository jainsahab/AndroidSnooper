package com.prateekj.snooper;

import android.content.Context;

import com.prateekj.snooper.repo.SnooperRepo;
import com.prateekj.snooper.transformer.SpringHttpRequestTransformer;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import io.realm.Realm;

public class AndroidSnooper implements ClientHttpRequestInterceptor{

  private SnooperRepo snooperRepo;

  private AndroidSnooper(){}

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    SpringHttpRequestTransformer transformer = new SpringHttpRequestTransformer();
    ClientHttpResponse clientHttpResponse = execution.execute(request, body);
    HttpCall httpCall = transformer.transform(request, body, clientHttpResponse);
    this.snooperRepo.save(httpCall);
    return clientHttpResponse;
  }

  public static AndroidSnooper init(Context context) {
    Realm.init(context);
    SnooperRepo repo = new SnooperRepo(Realm.getDefaultInstance());
    AndroidSnooper androidSnooper = new AndroidSnooper();
    androidSnooper.snooperRepo = repo;
    return androidSnooper;
  }
}

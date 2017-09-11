package com.prateekj.snooper.networksnooper.helper;

import com.google.common.base.Function;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.model.HttpHeaderValue;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static com.prateekj.snooper.networksnooper.model.HttpHeader.CONTENT_TYPE;

public class DataCopyHelper {
  private ResponseFormatterFactory responseFormatterFactory;
  private final HttpCallRecord httpCallRecord;

  public DataCopyHelper(HttpCallRecord httpCallRecord, ResponseFormatterFactory responseFormatterFactory) {
    this.responseFormatterFactory = responseFormatterFactory;
    this.httpCallRecord = httpCallRecord;
  }

  public String getResponseDataForCopy() {
    return getFormattedData(httpCallRecord.getResponseHeader(CONTENT_TYPE), httpCallRecord.getResponseBody());
  }

  public String getRequestDataForCopy() {
    return getFormattedData(httpCallRecord.getRequestHeader(CONTENT_TYPE), httpCallRecord.getPayload());
  }

  public String getErrorsForCopy() {
    return httpCallRecord.getError();
  }

  public StringBuilder getHttpCallData() {
    StringBuilder dataToCopy = new StringBuilder();

    appendRequestData(dataToCopy);
    appendResponseData(dataToCopy);
    return dataToCopy;
  }

  private void appendRequestData(StringBuilder dataToCopy) {
    String formattedRequestData = getRequestDataForCopy();
    if (!StringUtils.isEmpty(formattedRequestData)) {
      dataToCopy.append("Request Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedRequestData);
    }
    appendHeaders(httpCallRecord.getRequestHeaders(), dataToCopy, "Request Headers");
  }

  private void appendHeaders(List<HttpHeader> headers, StringBuilder dataToCopy, String heading) {
    if (headers != null && !headers.isEmpty()) {
      dataToCopy.append("\n");
      dataToCopy.append(heading);
      dataToCopy.append("\n");
      for (HttpHeader header : headers) {
        dataToCopy.append(header.getName() + ": ");
        dataToCopy.append(StringUtils.join(toHeaderValues(header), ";"));
        dataToCopy.append("\n");
      }
    }
  }

  private Iterator<String> toHeaderValues(HttpHeader httpHeader) {
    return transform(httpHeader.getValues(), new Function<HttpHeaderValue, String>() {
      @Override
      public String apply(HttpHeaderValue headerValue) {
        return headerValue.getValue();
      }
    }).iterator();
  }

  private void appendResponseData(StringBuilder dataToCopy) {
    String formattedResponseData = getResponseDataForCopy();

    if (!StringUtils.isEmpty(formattedResponseData)) {
      dataToCopy.append("Response Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedResponseData);
    }
    appendHeaders(httpCallRecord.getRequestHeaders(), dataToCopy, "Response Headers");
  }

  private String getFormattedData(HttpHeader contentTypeHeader, String dataToCopy) {
    if (dataToCopy == null) {
      return "";
    }
    if (contentHeadersPresent(contentTypeHeader)) {
      ResponseFormatter formatter = this.responseFormatterFactory.getFor(contentTypeHeader.getValues().get(0).getValue());
      return formatter.format(dataToCopy);
    }
    return dataToCopy;
  }


  private boolean contentHeadersPresent(HttpHeader contentTypeHeader) {
    return contentTypeHeader != null && contentTypeHeader.getValues().size() > 0;
  }
}

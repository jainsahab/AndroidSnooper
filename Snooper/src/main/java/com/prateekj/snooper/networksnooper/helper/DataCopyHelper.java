package com.prateekj.snooper.networksnooper.helper;

import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.networksnooper.model.HttpCallRecord;
import com.prateekj.snooper.networksnooper.model.HttpHeader;

import org.apache.commons.lang3.StringUtils;

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

    String formattedRequestData = getRequestDataForCopy();
    if (!StringUtils.isEmpty(formattedRequestData)) {
      dataToCopy.append("Request Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedRequestData);
      dataToCopy.append("\n");
    }

    String formattedResponseData = getResponseDataForCopy();

    if (!StringUtils.isEmpty(formattedResponseData)) {
      dataToCopy.append("Response Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedResponseData);
    }
    return dataToCopy;
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

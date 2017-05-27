package com.prateekj.snooper.formatter;


import org.junit.Test;

import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonResponseFormatterTest {

  @Test
  public void shouldReturnFormattedJsonObject() throws Exception {
    ResponseFormatter formatter = new JsonResponseFormatter();
    String formattedResponse = formatter.format(readFrom("person_details_raw_response.json"));
    String expectedResponse = readFrom("person_details_formatted_response.json");

    assertThat(formattedResponse, is(expectedResponse));
  }

  @Test
  public void shouldReturnFormattedJsonArray() throws Exception {
    ResponseFormatter formatter = new JsonResponseFormatter();
    String formattedResponse = formatter.format(readFrom("person_details_raw_array_response.json"));
    String expectedResponse = readFrom("person_details_formatted_array_response.json");

    assertThat(formattedResponse, is(expectedResponse));
  }

  @Test
  public void shouldReturnSameResponseWhenExceptionOccurs() throws Exception {
    ResponseFormatter formatter = new JsonResponseFormatter();
    String invalidJSON = "{\"value\":\"Invalid JSON\", \"value\"}";
    String formattedResponse = formatter.format(invalidJSON);

    assertThat(formattedResponse, is(invalidJSON));
  }

}
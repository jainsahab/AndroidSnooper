package com.prateekj.snooper.formatter;

import org.junit.Test;

import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlainTextFormatterTest {

  @Test
  public void shouldReturnFormattedPlainText() throws Exception {
    ResponseFormatter formatter = new PlainTextFormatter();
    String rawString = readFrom("person_details_formatted_response.txt").replaceAll("\n", "\r");
    String formattedResponse = formatter.format(rawString);
    String expectedResponse = readFrom("person_details_formatted_response.txt");

    assertThat(formattedResponse, is(expectedResponse));
  }
}
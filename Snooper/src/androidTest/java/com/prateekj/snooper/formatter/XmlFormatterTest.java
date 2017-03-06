package com.prateekj.snooper.formatter;

import org.junit.Test;

import static com.prateekj.snooper.utils.TestUtilities.readFrom;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XmlFormatterTest {

  @Test
  public void shouldReturnFormattedXmlObject() throws Exception {
    ResponseFormatter formatter = new XmlFormatter();
    String formattedResponse = formatter.format(readFrom("person_details_raw_response.xml"));
    String expectedResponse = readFrom("person_details_formatted_response.xml");

    assertThat(formattedResponse, is(expectedResponse));
  }

  @Test
  public void shouldReturnSameXmlWhenExceptionOccurs() throws Exception {
    ResponseFormatter formatter = new XmlFormatter();
    String xml = "<invalid>1</tags>";
    String formattedResponse = formatter.format(xml);

    assertThat(formattedResponse, is(xml));
  }
}

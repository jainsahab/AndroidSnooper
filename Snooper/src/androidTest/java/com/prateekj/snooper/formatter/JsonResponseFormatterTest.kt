package com.prateekj.snooper.formatter


import com.prateekj.snooper.utils.TestUtilities.readFrom
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class JsonResponseFormatterTest {

  @Test
  @Throws(Exception::class)
  fun shouldReturnFormattedJsonObject() {
    val formatter = JsonResponseFormatter()
    val formattedResponse = formatter.format(readFrom("person_details_raw_response.json"))
    val expectedResponse = readFrom("person_details_formatted_response.json")

    assertThat(formattedResponse, `is`<String>(expectedResponse))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnFormattedJsonArray() {
    val formatter = JsonResponseFormatter()
    val formattedResponse = formatter.format(readFrom("person_details_raw_array_response.json"))
    val expectedResponse = readFrom("person_details_formatted_array_response.json")

    assertThat(formattedResponse, `is`<String>(expectedResponse))
  }

  @Test
  @Throws(Exception::class)
  fun shouldReturnSameResponseWhenExceptionOccurs() {
    val formatter = JsonResponseFormatter()
    val invalidJSON = "{\"value\":\"Invalid JSON\", \"value\"}"
    val formattedResponse = formatter.format(invalidJSON)

    assertThat(formattedResponse, `is`(invalidJSON))
  }

}
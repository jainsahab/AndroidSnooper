package com.prateekj.snooper.formatter

import com.prateekj.snooper.utils.Logger

import org.xml.sax.InputSource

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import javax.xml.transform.OutputKeys
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.stream.StreamResult

class XmlFormatter : ResponseFormatter {

  override fun format(response: String): String {
    return try {
      val serializer = SAXTransformerFactory.newInstance().newTransformer().apply {
        setOutputProperty(OutputKeys.INDENT, "yes")
        setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
      }
      val xmlSource = SAXSource(InputSource(ByteArrayInputStream(response.toByteArray())))
      val res = StreamResult(ByteArrayOutputStream())
      serializer.transform(xmlSource, res)
      String((res.outputStream as ByteArrayOutputStream).toByteArray()).trim { it <= ' ' }
    } catch (e: Exception) {
      e.printStackTrace()
      Logger.e(TAG, e.message, e)
      response
    }

  }

  companion object {
    val TAG: String = XmlFormatter::class.java.simpleName
  }
}

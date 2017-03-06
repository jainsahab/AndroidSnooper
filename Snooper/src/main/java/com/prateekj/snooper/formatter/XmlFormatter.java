package com.prateekj.snooper.formatter;

import android.util.Log;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class XmlFormatter implements ResponseFormatter{

  public static final String TAG = XmlFormatter.class.getSimpleName();

  @Override
  public String format(String response) {
    try {
      Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(response.getBytes())));
      StreamResult res = new StreamResult(new ByteArrayOutputStream());
      serializer.transform(xmlSource, res);
      return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray()).trim();
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(TAG, e.getMessage(), e);
      return response;
    }
  }
}

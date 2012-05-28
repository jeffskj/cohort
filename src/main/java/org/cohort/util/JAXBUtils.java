package org.cohort.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.bind.JAXB;

public class JAXBUtils {
    public static InputStream marshalGzipped(Object o) throws IOException {
        StringWriter body = new StringWriter();
        JAXB.marshal(o, body);
        return new DeflaterInputStream(new ByteArrayInputStream(body.toString().getBytes()));
    }
    
    public static <T> T unmarshalGzipped(InputStream in, Class<T> type) throws IOException {
        return JAXB.unmarshal(new InflaterInputStream(in), type); 
    }
}

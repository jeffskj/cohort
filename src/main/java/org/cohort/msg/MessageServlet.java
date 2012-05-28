package org.cohort.msg;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.cohort.util.JAXBUtils;

import com.google.common.collect.Maps;

public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = -4325254561019892690L;

    private Map<String, RequestHandler<?, ?>> handlers = Maps.newHashMap(); 
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String messageType = StringUtils.substringAfterLast(req.getRequestURI(), "/");
        Object messageValue;
        try {
            messageValue = JAXBUtils.unmarshalGzipped(req.getInputStream(), ClassUtils.getClass(messageType));
        } catch (ClassNotFoundException e) {
            resp.setStatus(404);
            return;
        }
        
        @SuppressWarnings("unchecked")
        RequestHandler<Object, Object> requestHandler = (RequestHandler<Object, Object>) handlers.get(messageType);
        Object response = requestHandler.handle(messageValue);
        
        IOUtils.copy(JAXBUtils.marshalGzipped(response), resp.getOutputStream());
    }
    
    public void addHandler(String type, RequestHandler<?, ?> handler) {
        handlers.put(type, handler);
    }
}

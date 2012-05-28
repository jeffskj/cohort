package org.cohort.domain;

import org.cohort.msg.MessageSenderService;
import org.cohort.msg.MessageServlet;
import org.cohort.msg.RequestHandler;
import org.cohort.msg.ResponseHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MessageSendReceiveTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    
    @Test
    public void addServletAndMakeRequest() throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/cohort");
        server.setHandler(context);
 
        MessageServlet messageServlet = new MessageServlet();
        messageServlet.addHandler(FakeRequest.class.getName(), new RequestHandler<FakeRequest, FakeResponse>() {
            @Override
            public FakeResponse handle(FakeRequest req) {
                System.out.println("Received request: " + req.getMessage());
                
                FakeResponse fakeResponse = new FakeResponse();
                fakeResponse.setMessage("This is a response");
                return fakeResponse;
            }
        });
        
        context.addServlet(new ServletHolder(messageServlet),"/*");
        
        server.setHandler(context);
//        server.start();
        
        
        Node n = new Node();
        n.setIpAddress("localhost");
        n.setPort(8080);
        
        FakeRequest req = new FakeRequest();
        req.setMessage("This is a request");
        
        MessageSenderService sender = new MessageSenderService(tmp.getRoot());
        sender.sendMessage(n, req, new ResponseHandler<FakeResponse>() { 
            @Override        
            public void handle(FakeResponse response) {
                System.out.println("received response: " + response.getMessage());
            }
        });
        Thread.sleep(3000);
        
        server.start();
        
        Thread.sleep(3000);
    }
    
    public static class FakeRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class FakeResponse {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

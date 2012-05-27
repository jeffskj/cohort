package org.cohort.domain;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class JettyTest {

    @Test
    public void addServletAndMakeRequest() throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addServlet(new ServletHolder(new FakeServlet()),"/*");
        
        server.setHandler(context);
        server.start();
        
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.prepareGet("http://localhost:8080/cohort").execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response r) throws Exception {
                System.out.println(r.getResponseBody());
                return r;
            }
        }).get();
        
        server.stop();
    }
    
    public class FakeServlet extends HttpServlet {
        private static final long serialVersionUID = -6980449272312046855L;
        
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().println("Test Response");
        }
    }
}

package org.cohort.msg;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.cohort.group.Member;
import org.cohort.util.JAXBUtils;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class MessageSenderService {
    private ExecutorService responseReaderExecutor = Executors.newFixedThreadPool(5);
    private ScheduledExecutorService failedMessageRetryExecutor = Executors.newScheduledThreadPool(1);
    
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    
    private File workingDir;
    
    public MessageSenderService(File workingDir) {
        this.workingDir = workingDir;
    }
    
    public <REQ, RESP> void sendMessage(Member member, REQ request, final ResponseHandler<RESP> responseHandler) throws IOException {
        String url = getUrl(member, request.getClass());
        File f = createFile(member, request.getClass());
        FileUtils.copyInputStreamToFile(JAXBUtils.marshalGzipped(request), f);
        sendRequest(responseHandler, url, f);
    }
    
    private <RESP> void sendRequest(final ResponseHandler<RESP> responseHandler, final String url, final File body) throws IOException {
        System.out.println("sending request to: " + url + " with file: " + body);
        final Future<?> response = asyncHttpClient.preparePost(url).setBody(body).execute(new CompletionHandler<RESP>(responseHandler));
        responseReaderExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    response.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Message message = new Message();
                    message.attempts = 1;
                    message.lastAttempt = new Date();
                    message.requestPayload = body;
                    message.responseHandler = responseHandler;
                    message.url = url;
                    failedMessageRetryExecutor.schedule(new Resend(message), message.attempts * 2, TimeUnit.SECONDS);
                } 
            }
        });
    }
    
    private File createFile(Member node, Class<?> type) {
        return new File(workingDir, System.currentTimeMillis() + "-" + node.getId() + "-" + type.getName());
    }

    
    private String getUrl(Member node, Class<?> requestType) {
        return String.format("http://%s:%s/cohort/%s", node.getIpAddress(), node.getPort(), requestType.getName());
    }

    public class Resend implements Runnable {
        private final Message message;

        public Resend(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                sendRequest(message.responseHandler, message.url, message.requestPayload);
            } catch (Exception e) {
                message.attempts += 1;
                message.lastAttempt = new Date();
                failedMessageRetryExecutor.schedule(this, message.attempts * 2, TimeUnit.SECONDS);
            }
        }

    }
    
    private static class Message {
        private File requestPayload;
        private String url;
        private ResponseHandler<?> responseHandler;
        private int attempts = 0;
        private Date lastAttempt;
    }
    
    private static class CompletionHandler<Resp> extends AsyncCompletionHandler<Resp> {
        private final ResponseHandler<Resp> responseHandler;

        public CompletionHandler(ResponseHandler<Resp> responseHandler) {
            this.responseHandler = responseHandler;
        }
    
        @Override
        public Resp onCompleted(Response r) throws Exception {
            if (r.hasResponseStatus() && r.getStatusCode() > 399) {
                throw new RuntimeException("response failed");
            }
            Resp response = JAXBUtils.unmarshalGzipped(r.getResponseBodyAsStream(), getMessageType(responseHandler));
            responseHandler.handle(response);
            return response;
        }
        
        @SuppressWarnings("unchecked")
        private <T> Class<T> getMessageType(ResponseHandler<T> handler) {
            ParameterizedType superclass = (ParameterizedType) handler.getClass().getGenericInterfaces()[0];
            return (Class<T>) superclass.getActualTypeArguments()[0];
        }
    }
    
    public class MessageSendFailure extends RuntimeException {}
}

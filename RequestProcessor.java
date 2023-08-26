import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestProcessor {

    private static Queue<Request> requestQueue = new LinkedList<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    static {
        RequestProcessor requestProcessor = new RequestProcessor();
        requestProcessor.startProcessing();
    }

    public static void storeRequest(Request request) {
        System.out.println("starting storeRequest");
        synchronized (requestQueue) {
            requestQueue.add(request);
            requestQueue.notify();
        }
        System.out.println("Queue size after adding: " + requestQueue.size());
    }

    public void startProcessing() {
        System.out.println("Starting Executors");
        for (int i = 0; i < 4; i++) {
            executor.submit(this::processRequests);
            //executor.submit(processRequests());
        }
    }

    private void processRequests() {
        while (true) {
            Request request;
            synchronized (requestQueue) {
                while (requestQueue.isEmpty()) {
                    try {
                        System.out.println("Waiting for messages to be added");
                        requestQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                request = requestQueue.poll();
            }

            if (request != null) {
                Response response = processRequest(request);
                System.out.println("Processed: " + response);
            }
        }
    }

    private Response processRequest(Request request) {
        try {
            System.out.println("Processing Thread: " + Thread.currentThread().getName());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Process the request and generate a response
        return new Response(request.getData());
    }

}





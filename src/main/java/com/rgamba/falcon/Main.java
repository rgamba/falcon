package com.rgamba.falcon;

public class Main {
    public static void main(String[] args) {
        System.out.println("Started");
        HttpServer server = new HttpServer();
        server.setHandler((req) -> {
            String resHtml = "<h1>Requested: "+ req.getUri() +"</h1>"
                    + "<p>Body:<br>" + req.readAllBody() + "</p>";
            return new Response.Builder()
                    .setBody(resHtml)
                    .setHeader("Content-Type", "text/html")
                    .build();
        });
        server.listen(8080);
        System.out.print("Finished");
    }
}

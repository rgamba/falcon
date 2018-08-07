# falcon

Falcon is a fast, lightweight and embedded HTTP server
built with with the creation of microservices in mind and inspired
by the embedded built-in http server provided by the Go standard library.

## How does it work?

Simplicity is key

```java
public static void main(String[] args) {
  HttpServer server = new HttpServer();
  server.setHandler(req -> {
    return ok("Hello world!");
  });
  server.listen();
}
```
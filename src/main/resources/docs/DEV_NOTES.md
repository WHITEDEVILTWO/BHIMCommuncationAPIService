# Developer Notes

## Reactive guidelines
- Keep everything reactive end-to-end where possible (WebFlux + WebClient + Reactive Redis + R2DBC).
- Offload blocking operations (crypto, heavy compute) to `Schedulers.boundedElastic()`.

## Logging & Tomcat
- To enable access logs:
  ```properties
  server.tomcat.accesslog.enabled=true
  server.tomcat.accesslog.basedir=logs
  server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D
  ```
- For Tomcat internals set:
  ```properties
  logging.level.org.apache.catalina=DEBUG
  logging.level.org.apache.coyote=DEBUG
  logging.level.org.apache.tomcat=DEBUG
  ```

## Unit tests
- Add tests for encryption (roundtrip + tamper detection).
- Use Mockito + WebTestClient for controller tests.
- For regenerate token, mock WebClient response and assert Redis gets saved.

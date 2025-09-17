# Troubleshooting & Common Errors

## 1. `EOFException` / Tomcat parse error
Cause: malformed HTTP request or HTTP/HTTPS mismatch. Fix: ensure request URL uses correct protocol (http vs https) and correct port. Use `curl -v` to inspect request.

## 2. `RedisConnectionFailureException: Unable to connect to Redis`
- Confirm `spring.data.redis.host` is `redis` when running via Docker Compose.
- Ensure Redis container is ready. Use healthcheck in compose or retry logic.
- From host: `redis-cli -h localhost -p 6379 ping` should return `PONG`.
- Inside app container: install `redis-tools` and run `redis-cli -h redis -p 6379 ping`.

## 3. Token regeneration issues / null token
- Ensure `RegenerateTokenService` returns the actual access token and chains the Redis save operation (i.e. `return webClient...flatMap(... -> redisService.save(...).thenReturn(accessToken))`).
- Do **not** use `Mono.just("Token refreshed")` that ignores pipeline.

## 4. `403 FORBIDDEN` from ConvAPI
- Likely invalid / expired token.
- The service does: (a) try refresh token; (b) regenerate token (non-blocking) and retry once.
- Ensure token stored in Redis with correct key names and TTL.

## 5. DB inserts not appearing
- If using blocking JPA inside reactive flows nothing will subscribe -> no effect. Use R2DBC for reactive DB access or ensure explicit subscription for blocking calls (`.block()` only for dev debug).
- Check transactions and `@Transactional` if using JPA.

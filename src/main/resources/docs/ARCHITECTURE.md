# Architecture & Components

## High-level components
- **API Layer (Spring Boot WebFlux)**  
  - Receives incoming HTTP requests: `/api/v1/whatsapp/sendmessage`, `/api/v1/conv/RCS/...`, etc.
  - Uses reactive `WebClient` for outbound HTTP calls.

- **TokenManager**  
  - Checks Redis for valid access tokens.
  - If missing, checks Redis for refresh tokens (uses refresh token temporarily).
  - If refresh is missing or refresh fails, calls `RegenerateTokenService` to perform token exchange with auth server.
  - Designed to be non-blocking and returns `Mono<String>` (reactive).

- **RegenerateTokenService**  
  - Calls the remote auth endpoint (`/token`) using `WebClient`.
  - Parses response and stores `access_token` and `refresh_token` in Redis (with TTL).
  - Returns the actual access token in the reactive pipeline.

- **MessageService (RCS / WhatsApp)**  
  - Gets valid token via `TokenManager.getValidToken(...)`.
  - Sends message via `WebClient` to convapi/aclwhatsapp endpoints.
  - On 403 (forbidden) errors it triggers regeneration and retries once non-blocking.
  - Saves the outbound request + response metadata (message id) to Redis and to YugabyteDB (persistent store).

- **Redis (Reactive)**  
  - Holds `RCS_access_token`, `RCS_refresh_token`, `WA_access_token`, `WA_refresh_token`.
  - Holds ephemeral mapping: `message_id -> requestBody` for traceability.

- **YugabyteDB (Postgres-like)**  
  - Holds persisted response records (e.g., `whatsapp_responses`, `rcs_responses`, `media_responses`).

## Data flows (short)
1. Client calls `POST /api/v1/whatsapp/sendmessage`.
2. Service checks Redis for token.
   - If present → use token to send message.
   - If missing, check refresh token → use refresh token (temporary) and regenerate access token in background.
   - If both missing, regenerate token and **use regenerated token for the same request** (reactive retry).
3. Send outbound message to external provider.
   - On 2xx: store message id & request body (Redis + Yugabyte).
   - On 403: regenerate token and retry once.
4. Return response to client (non-blocking reactive Mono).

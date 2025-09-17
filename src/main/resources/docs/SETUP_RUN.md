# Setup & Run (local / Docker)

## Prerequisites
- Java 17+ (if running jar locally)
- Docker & Docker Compose (for container setup)
- Ports used:
  - App: 9098
  - Redis: 6379
  - Yugabyte YSQL: 5433

## Environment variables (example)
- `npci.wa.uname` — WA username (npcibhimpd)
- `npci.wa.key` — WA key (secret)
- `npci.rcs.uname` — RCS username (bhimapp_promo)
- `npci.rcs.key` — RCS key
- `SPRING_DATASOURCE_URL` — if overriding (use `jdbc:postgresql://yugabytedb:5433/yugabyte`)
- `APP_BASE64_KEY` (for encryption, optional in dev)

## Run with Docker Compose (recommended for dev)
1. `docker compose up -d` (from project root)
2. Check containers: `docker ps`
3. Check logs: `docker logs -f springboot-app`
4. Call test endpoint:
   ```bash
   curl -v -X POST "http://localhost:9098/api/v1/whatsapp/sendmessage"      -H "Content-Type: application/json"      -d '{ "recipient_type":"individual", "to":"917893411160", "type":"template", "template": { "name":"bbps_pre_06082025", "language": {"code":"en", "policy":"deterministic" } }, "metadata": { "callbackDlrUrl":"https://bhimmartech.npci.org.in/communication/callback/wa" } }'
   ```

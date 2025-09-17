# BHIMSMSserviceAPI — Overview

**Purpose**  
Bhim Communication API is a microservice that sends messages over messaging channels (WhatsApp/RCS), manages authentication tokens for the upstream messaging provider, and caches tokens and responses in Redis for performance. It uses a reactive (WebFlux) approach for non-blocking IO and Redis for token caching.

**Key capabilities (in simple terms)**
- Receives requests (template/text messages) from clients and sends them to a messaging provider (ConvAPI / WhatsApp / RCS).
- Manages access tokens and refresh tokens automatically (caches tokens in Redis).
- Regenerates tokens when missing/expired without blocking message sending.
- Saves message responses (message id + request body) for traceability.
- Exposes REST endpoints for sending messages.

**Who should read this**  
- Business stakeholders wanting a high-level overview.  
- Developers and DevOps who will run, test, or extend the service.  
- Security engineers who will audit encryption/key management.

**Quick facts**  
- Language: Java (Spring Boot, WebFlux)  
- Data store: YugabyteDB (Postgres compatible) for persistent responses; Redis for caching tokens & ephemeral data.  
- Network: Docker Compose used for local environments.  
- Security: AES-GCM recommended for payload encryption (DEK stored via KMS or env in dev).

**Where to start**  
- Read `docs/ARCHITECTURE.md` — architecture & flows.  
- Read `docs/SETUP_RUN.md` — how to boot locally with Docker Compose.  
- Use `curl` or Postman to hit `http://localhost:9098/api/v1/...` after startup.

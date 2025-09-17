# API Reference (selected)

## POST /api/v1/whatsapp/sendmessage
Sends a WhatsApp template message.

**Request (JSON)**:
```json
{
  "recipient_type": "individual",
  "to": "917893411160",
  "type": "template",
  "template": {
    "name": "bbps_pre_06082025",
    "language": {
      "policy": "deterministic",
      "code": "en"
    }
  },
  "metadata": {
    "callbackDlrUrl": "https://your-callback.url/whatsapp"
  }
}
```

**Success response (2xx)**:
```json
{
  "message_id": "01K4VV049PR5S2WJ6S1Q8DVH0X",
  "accepted_time": "2025-09-11T07:31:49.686Z"
}
```

**Failure / retry logic**:
- If the upstream returns `403 FORBIDDEN`, the app triggers token regeneration (non-blocking), retries once and then returns appropriate error if still failing.

# Security & Encryption

## AES-GCM 128-bit (recommended)
- Use `AES/GCM/NoPadding` with 12-byte random IV per encryption and 128-bit tag.
- Store ciphertext as `keyId:BASE64(iv || ciphertext)`.
- Use Additional Authenticated Data (AAD) for binding metadata where required.

## Key management
- **Dev**: store Base64 DEK as environment variable or secret mount.
- **Prod**: use a managed KMS (AWS KMS, GCP KMS, Azure Key Vault) or HashiCorp Vault transit for wrapping/unwrapping data keys.
- Use `GenerateDataKey` pattern:
  1. KMS returns plaintext DEK + encrypted DEK blob.
  2. Use plaintext DEK briefly for encryption, then discard.
  3. Store encrypted DEK with ciphertext.

## Other best practices
- Never commit keys to source control.
- Rotate keys periodically; embed `keyId` in stored data to make rotation safe.
- Catch and handle `AEADBadTagException` as a tampering indicator.
- Run crypto on `Schedulers.boundedElastic()` to avoid blocking reactor threads.

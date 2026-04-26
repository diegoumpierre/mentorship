# OAuth 2.0 Server (RFC 6749)

| Activities |
|------------|
| - [ ] Read RFC 6749 once end-to-end before writing a line; keep it open while you implement |
| - [ ] Model the persistent entities: `Client`, `User`, `AuthorizationCode`, `AccessToken`, `RefreshToken`, `Scope`, `Consent` |
| - [ ] Implement the four core endpoints: `/authorize`, `/token`, `/introspect` (RFC 7662), `/revoke` (RFC 7009) |
| - [ ] Authorization Code grant first — including the `state` parameter and the registered-redirect-URI check |
| - [ ] Add PKCE (RFC 7636) on top — make it required for public clients, recommended for confidential ones |
| - [ ] Client credentials grant for machine-to-machine — no user, no consent screen |
| - [ ] Refresh token grant with rotation: every refresh issues a new refresh token and invalidates the previous one |
| - [ ] Skip resource-owner password credentials (RFC 6819 §4.4.3) and document why |
| - [ ] Issue tokens as JWT (RFC 7519) signed with RS256 / EdDSA; expose the keys at `/.well-known/jwks.json` |
| - [ ] Validate redirect URIs strictly: exact match for the registered set, no wildcards, no fragments |
| - [ ] Authenticate confidential clients on `/token` via Basic auth or `client_secret_post` — no plain `client_id` for confidential |
| - [ ] Build the consent screen: per `(user, client, scope)` consent stored once and reused on the next authorize |
| - [ ] Token storage: short-lived access tokens (5–15 min), long-lived refresh tokens, all hashed at rest |
| - [ ] Audit every issuance, refresh and revocation with `client_id`, `user_id`, scopes, timestamp |
| - [ ] Tests for: invalid `redirect_uri`, mismatched PKCE verifier, expired authorization code, refresh token reuse must revoke the family, scope downgrade on refresh |
| - [ ] Verify against the Spring Security OAuth2 client and the `AppAuth` flow on a phone — your server has to be talked-to by real clients |

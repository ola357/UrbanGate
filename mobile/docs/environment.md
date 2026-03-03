# Mobile environment strategy (UrbanGate)

We align mobile environments with backend environments:

- **dev**: local/backend dev, internal builds
- **staging**: staging backend, internal distribution
- **prod**: production backend, store distribution

## Source of truth for config
We use Expo's `EXPO_PUBLIC_*` environment variables:

- `EXPO_PUBLIC_APP_ENV` (dev|staging|prod)
- `EXPO_PUBLIC_API_BASE_URL` (base URL used by the HTTP client)

### Local development
Create `mobile/.env` (from `.env.example`) and set:

- iOS simulator: `http://localhost:8080`
- Android emulator: `http://10.0.2.2:8080`
- real device: `http://<LAN_IP>:8080`

Expo exposes `EXPO_PUBLIC_*` variables to the app.

### CI / Builds (EAS)
Variables are set in `eas.json` per build profile:

- `build.dev.env`
- `build.staging.env`
- `build.prod.env`

Replace the placeholder URLs in `eas.json` with real endpoints.

Non-interactive EAS builds require credentials to already exist in Expo.
If builds fail with "Generating a new Keystore is not supported in --non-interactive mode",
run `eas credentials -p android` locally once to generate or upload a keystore.
Do the same for iOS if you plan to build iOS in CI.

## OTA updates (later)
When enabling EAS Update:
- dev channel → internal testers
- staging channel → QA
- prod channel → production

Channels are already declared in `eas.json`.

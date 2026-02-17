# UrbanGate Mobile (Expo)

## Run
```bash
cd mobile
npm install
npm run start
```

## Configure API base URL

Expo supports `EXPO_PUBLIC_*` environment variables.

Create `.env` (or export in shell) using the example:

```bash
cp .env.example .env
```

Examples:
- iOS Simulator (backend on your Mac): `http://localhost:8080`
- Android Emulator (backend on your Mac): `http://10.0.2.2:8080`
- Physical device: use your machine IP, e.g. `http://192.168.0.10:8080`

Then run:
```bash
npm run start
```

## What this scaffold includes
- Expo Router (file-based navigation)
- TanStack Query for server state
- Clean-ish feature separation (`src/features/version`)
- A simple `/api/v1/version` fetch displayed on the home screen

## Health Screen
Open `/health` to check backend connectivity with a Retry UX.

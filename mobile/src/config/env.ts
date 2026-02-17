import { Platform } from "react-native";

type AppEnv = "dev" | "staging" | "prod";

function resolveDefaultApiBaseUrl() {
  // iOS simulator: localhost, Android emulator: 10.0.2.2
  if (Platform.OS === "android") return "https://10.0.2.2:8080";
  return "https://localhost:8080";
}

export const env = {
  appEnv: (process.env.EXPO_PUBLIC_APP_ENV as AppEnv | undefined) ?? "dev",
  apiBaseUrl: process.env.EXPO_PUBLIC_API_BASE_URL ?? resolveDefaultApiBaseUrl(),
} as const;

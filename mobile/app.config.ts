import type { ExpoConfig } from "expo/config";

const config: ExpoConfig = {
  name: "UrbanGate",
  slug: "urbangate",
  owner: "urbangate",
  scheme: "urbangate",
  version: "0.1.0",
  orientation: "portrait",
  userInterfaceStyle: "automatic",
  android: {
    package: "com.urbangate.app",
  },
  ios: {
    bundleIdentifier: "com.urbangate.app",
  },
  plugins: ["expo-router", "@react-native-community/datetimepicker"],
  experiments: { typedRoutes: true },
  extra: {
    // Prefer setting this via EXPO_PUBLIC_API_BASE_URL in your shell or .env
    apiBaseUrl: process.env.EXPO_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
    eas: {
      projectId: "a7ae05ee-e088-46a4-8474-04818a5302f4",
    },
  },
};

export default config;

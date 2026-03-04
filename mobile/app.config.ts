import type { ExpoConfig } from "expo/config";

const config: ExpoConfig = {
  name: "UrbanGate",
  slug: "urbangate",
  owner: "urbangate",
  scheme: "urbangate",
  version: "0.1.0",
  orientation: "portrait",
  icon: "./assets/images/icon.png",
  userInterfaceStyle: "automatic",
  newArchEnabled: true,
  splash: {
    image: "./assets/images/splash-icon.png",
    resizeMode: "contain",
    backgroundColor: "#ffffff",
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
  ios: {
    bundleIdentifier: "com.urbangate.app",
    supportsTablet: true,
    infoPlist: {
      ITSAppUsesNonExemptEncryption: false,
    },
  },
  android: {
    package: "com.urbangate.app",
    adaptiveIcon: {
      foregroundImage: "./assets/images/adaptive-icon.png",
      backgroundColor: "#ffffff",
    },
    edgeToEdgeEnabled: true,
  },
  web: {
    bundler: "metro",
    output: "static",
    favicon: "./assets/images/favicon.png",
  },
};

export default config;

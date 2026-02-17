const originalEnv = { ...process.env };

type EnvOverrides = Partial<Record<"EXPO_PUBLIC_APP_ENV" | "EXPO_PUBLIC_API_BASE_URL", string | undefined>>;

function setEnv(key: keyof EnvOverrides, value: string | undefined) {
  if (value === undefined) {
    delete process.env[key];
  } else {
    process.env[key] = value;
  }
}

afterEach(() => {
  setEnv("EXPO_PUBLIC_APP_ENV", originalEnv.EXPO_PUBLIC_APP_ENV);
  setEnv("EXPO_PUBLIC_API_BASE_URL", originalEnv.EXPO_PUBLIC_API_BASE_URL);
  jest.resetModules();
  jest.clearAllMocks();
});

function loadEnv(os: "android" | "ios", overrides: EnvOverrides) {
  jest.resetModules();
  jest.doMock("react-native", () => ({
    Platform: { OS: os },
  }));

  setEnv("EXPO_PUBLIC_APP_ENV", overrides.EXPO_PUBLIC_APP_ENV);
  setEnv("EXPO_PUBLIC_API_BASE_URL", overrides.EXPO_PUBLIC_API_BASE_URL);

  // eslint-disable-next-line @typescript-eslint/no-var-requires
  const { env } = require("../../config/env");
  return env as { appEnv: string; apiBaseUrl: string };
}

describe("env", () => {
  it("falls back to android localhost when unset", () => {
    const env = loadEnv("android", {
      EXPO_PUBLIC_APP_ENV: undefined,
      EXPO_PUBLIC_API_BASE_URL: undefined,
    });

    expect(env.appEnv).toBe("dev");
    expect(env.apiBaseUrl).toBe("https://10.0.2.2:8080");
  });
});

import { Stack } from "expo-router";
import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 30_000,
    },
  },
});

export default function RootLayout() {
  return (
    <QueryClientProvider client={queryClient}>
      <Stack screenOptions={{ headerTitle: "UrbanGate" }}>
        <Stack.Screen name="index" options={{ title: "Home" }} />
      </Stack>
    </QueryClientProvider>
  );
}

import React from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { useVersion } from "@/features/version/hooks/useVersion";

export function VersionCard() {
  const { data, isLoading, error, refetch, isRefetching } = useVersion();

  return (
    <View style={styles.card}>
      <Text style={styles.title}>Backend Version</Text>

      {isLoading ? (
        <ActivityIndicator />
      ) : error ? (
        <>
          <Text style={styles.error}>Could not fetch /api/v1/version</Text>
          <Text style={styles.muted}>
            Tip: set EXPO_PUBLIC_API_BASE_URL (Android emulator often needs http://10.0.2.2:8080)
          </Text>
          <Text style={styles.link} onPress={() => refetch()}>
            {isRefetching ? "Retrying..." : "Tap to retry"}
          </Text>
        </>
      ) : (
        <>
          <Text style={styles.row}>service: {data?.service}</Text>
          <Text style={styles.row}>version: {data?.version}</Text>
          <Text style={styles.row}>time: {data?.time}</Text>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 16,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    backgroundColor: "white",
  },
  title: { fontSize: 16, fontWeight: "700", marginBottom: 8 },
  row: { fontSize: 14, marginTop: 4 },
  error: { color: "#b91c1c", fontWeight: "600" },
  muted: { color: "#6b7280", marginTop: 8, fontSize: 12 },
  link: { color: "#2563eb", marginTop: 12, fontWeight: "600" },
});

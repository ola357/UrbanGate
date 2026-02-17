import React from "react";
import { View, Text, Pressable, ActivityIndicator, StyleSheet } from "react-native";
import { Link } from "expo-router";
import { useVersion } from "@/features/version/hooks/useVersion";

export default function HomeScreen() {
  const { data, isLoading, isFetching, error, refetch } = useVersion();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>UrbanGate</Text>
      <Text style={styles.subtitle}>Mobile Client</Text>

      {(isLoading || isFetching) && (
        <View style={styles.row}>
          <ActivityIndicator />
          <Text style={styles.muted}> Loading version…</Text>
        </View>
      )}

      {data && (
        <View style={styles.card}>
          <Text style={styles.label}>Service</Text>
          <Text style={styles.value}>{data.service}</Text>

          <Text style={styles.label}>Version</Text>
          <Text style={styles.value}>{data.version}</Text>

          <Text style={styles.label}>Time</Text>
          <Text style={styles.value}>{data.time}</Text>

          <Pressable onPress={() => refetch()} style={styles.secondaryBtn}>
            <Text style={styles.secondaryText}>Refresh</Text>
          </Pressable>
        </View>
      )}

      {error && (
        <View style={[styles.card, styles.errorCard]}>
          <Text style={styles.errorTitle}>Couldn’t reach the API</Text>
          <Text style={styles.errorText}>
            {error instanceof Error ? error.message : String(error)}
          </Text>

          <Pressable onPress={() => refetch()} style={styles.retryBtn}>
            <Text style={styles.retryText}>Retry</Text>
          </Pressable>
        </View>
      )}

      <Link href="/health" asChild>
        <Pressable style={styles.linkBtn}>
          <Text style={styles.linkText}>Open Health Screen</Text>
        </Pressable>
      </Link>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, gap: 12 },
  title: { fontSize: 28, fontWeight: "800" },
  subtitle: { color: "#666" },
  row: { flexDirection: "row", alignItems: "center" },
  muted: { color: "#666" },
  card: {
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 12,
    padding: 14,
    gap: 6,
  },
  errorCard: {
    borderColor: "#f0b5b5",
    backgroundColor: "#fff7f7",
  },
  label: { fontSize: 12, color: "#666", marginTop: 6 },
  value: { fontSize: 16, fontWeight: "600" },
  errorTitle: { fontSize: 16, fontWeight: "700" },
  errorText: { color: "#a33" },
  retryBtn: {
    marginTop: 10,
    alignSelf: "flex-start",
    backgroundColor: "#111",
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  retryText: { color: "white", fontWeight: "700" },
  secondaryBtn: {
    marginTop: 10,
    alignSelf: "flex-start",
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  secondaryText: { fontWeight: "700" },
  linkBtn: {
    alignSelf: "flex-start",
    borderWidth: 1,
    borderColor: "#111",
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 10,
  },
  linkText: { fontWeight: "700" },
});

import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";

const PRIMARY_DARK = "#00483C";

export function BillsHeader() {
  const insets = useSafeAreaInsets();

  return (
    <View style={[styles.container, { paddingTop: insets.top + 16 }]}>
      <Text style={styles.title}>Bills</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: PRIMARY_DARK,
    paddingHorizontal: 16,
    paddingBottom: 20,
  },
  title: {
    color: "#FFFFFF",
    fontSize: 28,
    fontWeight: "700",
  },
});

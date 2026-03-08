import React from "react";
import { View, Text, StyleSheet } from "react-native";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { WalletTransaction } from "../types";
import { TransactionItem } from "./TransactionItem";

interface WalletHistoryProps {
  transactions: WalletTransaction[];
}

export function WalletHistory({ transactions }: WalletHistoryProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <View style={styles.container}>
      <Text style={[styles.heading, { color: colors.text }]}>
        Wallet history
      </Text>
      <View style={[styles.listCard, { backgroundColor: colors.card }]}>
        {transactions.map((tx, index) => (
          <TransactionItem
            key={tx.id}
            transaction={tx}
            showSeparator={index < transactions.length - 1}
          />
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 24,
  },
  heading: {
    fontSize: 18,
    fontWeight: "700",
    marginHorizontal: 16,
    marginBottom: 12,
  },
  listCard: {
    marginHorizontal: 16,
    borderRadius: 12,
    overflow: "hidden",
  },
});

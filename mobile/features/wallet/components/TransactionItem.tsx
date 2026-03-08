import React from "react";
import { View, Text, StyleSheet } from "react-native";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { WalletTransaction, TRANSACTION_CATEGORIES } from "../types";

interface TransactionItemProps {
  transaction: WalletTransaction;
  showSeparator?: boolean;
}

const formatAmount = (amount: number) =>
  `₦${amount.toLocaleString("en-NG", { minimumFractionDigits: 2 })}`;

export function TransactionItem({
  transaction,
  showSeparator = true,
}: TransactionItemProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const category = TRANSACTION_CATEGORIES[transaction.category];
  const Icon = category.icon;
  const isDebit = transaction.type === "debit";

  return (
    <>
      <View style={styles.row}>
        <View style={[styles.iconBox, { backgroundColor: category.color }]}>
          <Icon size={20} color="#FFFFFF" />
        </View>
        <View style={styles.info}>
          <Text style={[styles.title, { color: colors.text }]}>
            {transaction.title}
          </Text>
          <Text style={[styles.date, { color: colors.textTertiary }]}>
            {transaction.date}
          </Text>
        </View>
        <Text
          style={[
            styles.amount,
            { color: isDebit ? colors.negative : colors.positive },
          ]}
        >
          {isDebit ? "−" : "+"}
          {formatAmount(transaction.amount)}
        </Text>
      </View>
      {showSeparator && (
        <View style={[styles.separator, { backgroundColor: colors.border }]} />
      )}
    </>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 12,
    paddingHorizontal: 16,
  },
  iconBox: {
    width: 44,
    height: 44,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  info: {
    flex: 1,
    marginHorizontal: 12,
  },
  title: {
    fontWeight: "600",
    fontSize: 14,
  },
  date: {
    fontSize: 12,
    marginTop: 2,
  },
  amount: {
    fontWeight: "600",
    fontSize: 14,
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    marginLeft: 72,
  },
});

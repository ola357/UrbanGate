import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft } from "lucide-react-native";
import { Bill, BILL_CATEGORIES } from "../types";

const PRIMARY_DARK = "#00483C";

const formatAmount = (amount: number) =>
  `₦${amount.toLocaleString("en-NG", { minimumFractionDigits: 2 })}`;

interface BillPaymentHeaderProps {
  bill: Bill;
  onBack: () => void;
}

export function BillPaymentHeader({ bill, onBack }: BillPaymentHeaderProps) {
  const insets = useSafeAreaInsets();
  const category = BILL_CATEGORIES[bill.category];
  const Icon = category.icon;

  return (
    <View style={[styles.container, { paddingTop: insets.top + 16 }]}>
      <View style={styles.topRow}>
        <TouchableOpacity onPress={onBack} hitSlop={8}>
          <ChevronLeft size={24} color="white" />
        </TouchableOpacity>
        <Text style={styles.screenTitle}>Bill payment</Text>
        <View style={{ width: 24 }} />
      </View>

      <View style={styles.billInfo}>
        <View style={[styles.iconBox, { backgroundColor: category.color }]}>
          <Icon size={28} color="#FFFFFF" />
        </View>
        <Text style={styles.billName}>{bill.name}</Text>
        <Text style={styles.amount}>{formatAmount(bill.amount)}</Text>
        <Text style={styles.dueDate}>Due: {bill.dueDate}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: PRIMARY_DARK,
    paddingHorizontal: 16,
    paddingBottom: 28,
  },
  topRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 24,
  },
  screenTitle: {
    flex: 1,
    textAlign: "center",
    color: "#FFFFFF",
    fontWeight: "700",
    fontSize: 17,
  },
  billInfo: {
    alignItems: "center",
  },
  iconBox: {
    width: 56,
    height: 56,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 12,
  },
  billName: {
    color: "rgba(255,255,255,0.7)",
    fontSize: 14,
    marginBottom: 4,
  },
  amount: {
    color: "#FFFFFF",
    fontSize: 28,
    fontWeight: "700",
    marginBottom: 4,
  },
  dueDate: {
    color: "rgba(255,255,255,0.5)",
    fontSize: 13,
  },
});

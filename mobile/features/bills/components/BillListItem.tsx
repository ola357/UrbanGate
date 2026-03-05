import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { Button } from "@/components/ui/Buttons";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { Bill, BILL_CATEGORIES } from "../types";

interface BillListItemProps {
  bill: Bill;
  onPay: (id: string) => void;
  showSeparator?: boolean;
}

const formatAmount = (amount: number) =>
  `₦${amount.toLocaleString("en-NG", { minimumFractionDigits: 2 })}`;

export function BillListItem({ bill, onPay, showSeparator = true }: BillListItemProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const category = BILL_CATEGORIES[bill.category];
  const Icon = category.icon;

  return (
    <>
      <View style={styles.row}>
        <View style={[styles.iconBox, { backgroundColor: category.color }]}>
          <Icon size={20} color="#FFFFFF" />
        </View>
        <View style={styles.info}>
          <Text style={[styles.name, { color: colors.text }]}>{bill.name}</Text>
          <Text style={[styles.meta, { color: colors.textTertiary }]}>
            {formatAmount(bill.amount)} · {bill.dueDate}
          </Text>
        </View>
        <Button variant="outline" size="sm" onPress={() => onPay(bill.id)}>
          Pay
        </Button>
      </View>
      {showSeparator && <View style={[styles.separator, { backgroundColor: colors.border }]} />}
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
  name: {
    fontWeight: "600",
    fontSize: 14,
  },
  meta: {
    fontSize: 12,
    marginTop: 2,
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    marginLeft: 72,
  },
});

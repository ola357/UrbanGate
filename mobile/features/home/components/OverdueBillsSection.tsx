import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Button } from "@/components/ui/Buttons";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";

export type BillItem = {
  id: string;
  name: string;
  amount: number;
  dueDate: string;
  iconColor: string;
  iconLetter: string;
};

interface OverdueBillsSectionProps {
  bills: BillItem[];
  onSeeAll: () => void;
  onPay: (id: string) => void;
}

export function OverdueBillsSection({
  bills,
  onSeeAll,
  onPay,
}: OverdueBillsSectionProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const formatAmount = (amount: number) =>
    `₦${amount.toLocaleString("en-NG", { minimumFractionDigits: 2 })}`;

  return (
    <View>
      <View style={styles.header}>
        <Text style={[styles.headerTitle, { color: colors.text }]}>
          Overdue bills
        </Text>
        <TouchableOpacity onPress={onSeeAll}>
          <Text style={[styles.seeAll, { color: colors.tint }]}>
            See all bills
          </Text>
        </TouchableOpacity>
      </View>

      {bills.map((bill, index) => (
        <React.Fragment key={bill.id}>
          <View style={styles.row}>
            <View
              style={[styles.billIcon, { backgroundColor: bill.iconColor }]}
            >
              <Text style={styles.billIconText}>{bill.iconLetter}</Text>
            </View>
            <View style={styles.billInfo}>
              <Text style={[styles.billName, { color: colors.text }]}>
                {bill.name}
              </Text>
              <Text style={[styles.billMeta, { color: colors.textTertiary }]}>
                {formatAmount(bill.amount)} · {bill.dueDate}
              </Text>
            </View>
            <Button
              variant="outline"
              size="sm"
              onPress={() => onPay(bill.id)}
            >
              Pay
            </Button>
          </View>
          {index < bills.length - 1 && (
            <View
              style={[styles.separator, { backgroundColor: colors.border }]}
            />
          )}
        </React.Fragment>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  headerTitle: {
    fontWeight: "700",
    fontSize: 16,
  },
  seeAll: {
    fontSize: 14,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 12,
  },
  billIcon: {
    width: 44,
    height: 44,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  billIconText: {
    color: "#FFFFFF",
    fontWeight: "700",
    fontSize: 16,
  },
  billInfo: {
    flex: 1,
    marginHorizontal: 12,
  },
  billName: {
    fontWeight: "600",
    fontSize: 14,
  },
  billMeta: {
    fontSize: 12,
    marginTop: 2,
  },
  separator: {
    height: StyleSheet.hairlineWidth,
  },
});

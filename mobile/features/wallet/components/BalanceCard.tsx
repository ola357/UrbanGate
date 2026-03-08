import React from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Plus } from "lucide-react-native";
import { CitySkyline } from "./CitySkyline";

interface BalanceCardProps {
  balance: number;
  onAddMoney: () => void;
}

const formatBalance = (amount: number) => `₦${amount.toLocaleString("en-NG")}`;

export function BalanceCard({ balance, onAddMoney }: BalanceCardProps) {
  return (
    <View style={styles.wrapper}>
      <View style={styles.gradient}>
        <View style={styles.balanceSection}>
          <Text style={styles.label}>Wallet balance</Text>
          <Text style={styles.amount}>{formatBalance(balance)}</Text>
        </View>

        {/* <View style={styles.skylineContainer}>
          <CitySkyline width={200} height={100} />
        </View> */}
      </View>

      <TouchableOpacity style={styles.addMoneyButton} onPress={onAddMoney} activeOpacity={0.8}>
        <Plus size={18} color="#FFFFFF" />
        <Text style={styles.addMoneyText}>Add money</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    marginHorizontal: 16,
    marginTop: 16,
    borderRadius: 16,
    overflow: "hidden",
  },
  gradient: {
    paddingTop: 24,
    paddingHorizontal: 20,
    paddingBottom: 20,
    minHeight: 150,
    backgroundColor: "#05C756",
  },
  balanceSection: {
    zIndex: 1,
  },
  label: {
    color: "#FFFFFF",
    fontSize: 14,
    fontWeight: "500",
  },
  amount: {
    color: "#FFFFFF",
    fontSize: 32,
    fontWeight: "700",
    marginTop: 4,
  },
  skylineContainer: {
    position: "absolute",
    bottom: 0,
    right: 0,
  },
  addMoneyButton: {
    backgroundColor: "#000000",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 14,
    gap: 8,
  },
  addMoneyText: {
    color: "#FFFFFF",
    fontSize: 15,
    fontWeight: "600",
  },
});

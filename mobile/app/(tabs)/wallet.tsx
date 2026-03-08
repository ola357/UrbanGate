import React, { useState } from "react";
import { View, ScrollView, StyleSheet } from "react-native";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { WalletHeader } from "@/features/wallet/components/WalletHeader";
import { BalanceCard } from "@/features/wallet/components/BalanceCard";
import { WalletHistory } from "@/features/wallet/components/WalletHistory";
import { AddMoneyModal } from "@/features/wallet/components/AddMoneyModal";
import {
  useWalletBalance,
  useWalletHistory,
  useAccountDetails,
} from "@/features/wallet/hooks/useWallet";

export default function WalletScreen() {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const [showAddMoney, setShowAddMoney] = useState(false);

  const { data: balance } = useWalletBalance();
  const { data: transactions } = useWalletHistory();
  const { data: accountDetails } = useAccountDetails();

  return (
    <View style={[styles.container, { backgroundColor: "#00483C" }]}>
      <WalletHeader />

      <View style={[styles.content, { backgroundColor: colors.backgroundSecondary }]}>
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <BalanceCard
            balance={balance?.amount ?? 0}
            onAddMoney={() => setShowAddMoney(true)}
          />

          {transactions && transactions.length > 0 && (
            <WalletHistory transactions={transactions} />
          )}
        </ScrollView>
      </View>

      <AddMoneyModal
        visible={showAddMoney}
        onClose={() => setShowAddMoney(false)}
        accountDetails={accountDetails}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
  },
  scrollContent: {
    paddingBottom: 24,
  },
});

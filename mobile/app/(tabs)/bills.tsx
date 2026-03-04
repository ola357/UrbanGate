import React, { useMemo, useState } from "react";
import { View, ScrollView, StyleSheet } from "react-native";
import { useRouter } from "expo-router";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { BillsHeader } from "@/features/bills/components/BillsHeader";
import { BillsTabs } from "@/features/bills/components/BillsTabs";
import { CombineBillsBanner } from "@/features/bills/components/CombineBillsBanner";
import { BillListItem } from "@/features/bills/components/BillListItem";
import { BillStatus } from "@/features/bills/types";
import { mockBills } from "@/features/bills/data/mockBills";

export default function BillsScreen() {
  const router = useRouter();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const [activeTab, setActiveTab] = useState<BillStatus>("due");

  const filteredBills = useMemo(
    () => mockBills.filter((b) => b.status === activeTab),
    [activeTab],
  );

  const handlePay = (billId: string) => {
    router.push({ pathname: "/bill-payment", params: { billId } } as any);
  };

  return (
    <View style={[styles.container, { backgroundColor: "#00483C" }]}>
      <BillsHeader />
      <BillsTabs activeTab={activeTab} onTabChange={setActiveTab} />

      <View
        style={[
          styles.content,
          { backgroundColor: colors.backgroundSecondary },
        ]}
      >
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          {activeTab === "due" && (
            <CombineBillsBanner onCombine={() => {}} />
          )}

          <View style={[styles.listCard, { backgroundColor: colors.card }]}>
            {filteredBills.map((bill, index) => (
              <BillListItem
                key={bill.id}
                bill={bill}
                onPay={handlePay}
                showSeparator={index < filteredBills.length - 1}
              />
            ))}
          </View>
        </ScrollView>
      </View>
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
  listCard: {
    marginHorizontal: 16,
    marginTop: 16,
    borderRadius: 12,
    overflow: "hidden",
  },
});

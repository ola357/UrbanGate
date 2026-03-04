import React, { useState } from "react";
import { View, ScrollView, Text, StyleSheet } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Wallet, Building2 } from "lucide-react-native";
import { Button } from "@/components/ui/Buttons";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { BillPaymentHeader } from "@/features/bills/components/BillPaymentHeader";
import { PaymentMethodCard } from "@/features/bills/components/PaymentMethodCard";
import { PaymentMethod } from "@/features/bills/types";
import { mockBills } from "@/features/bills/data/mockBills";

const PAYMENT_METHODS = [
  {
    key: "wallet" as PaymentMethod,
    label: "Pay from wallet",
    desc: "Use your UrbanGate wallet balance to pay this bill.",
    Icon: Wallet,
  },
  {
    key: "transfer" as PaymentMethod,
    label: "Bank transfer",
    desc: "Pay via direct bank transfer. You will receive account details.",
    Icon: Building2,
  },
];

export default function BillPaymentScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const { billId } = useLocalSearchParams<{ billId: string }>();

  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod>("wallet");

  const bill = mockBills.find((b) => b.id === billId) ?? mockBills[0];

  return (
    <View style={[styles.container, { backgroundColor: "#00483C" }]}>
      <BillPaymentHeader bill={bill} onBack={() => router.back()} />

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
          <Text style={[styles.sectionTitle, { color: colors.text }]}>
            Payment method
          </Text>

          <View style={styles.methodList}>
            {PAYMENT_METHODS.map((method) => (
              <PaymentMethodCard
                key={method.key}
                selected={selectedMethod === method.key}
                Icon={method.Icon}
                label={method.label}
                desc={method.desc}
                onPress={() => setSelectedMethod(method.key)}
              />
            ))}
          </View>
        </ScrollView>

        <View
          style={[
            styles.footer,
            {
              paddingBottom: insets.bottom + 16,
              backgroundColor: colors.backgroundSecondary,
            },
          ]}
        >
          <Button fullWidth onPress={() => {}}>
            Proceed to pay
          </Button>
        </View>
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
    paddingHorizontal: 16,
    paddingTop: 24,
    paddingBottom: 24,
  },
  sectionTitle: {
    fontWeight: "700",
    fontSize: 16,
    marginBottom: 16,
  },
  methodList: {
    gap: 12,
  },
  footer: {
    paddingHorizontal: 16,
  },
});

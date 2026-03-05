import React, { useState } from "react";
import { ScrollView, View } from "react-native";
import { useRouter } from "expo-router";

import { useColorScheme } from "@/components/useColorScheme";
import Colors from "@/constants/Colors";
import { useHomeGreeting } from "@/features/home/hooks/useHomeGreeting";
import { HomeHeader } from "@/features/home/components/HomeHeader";
import { QuickActionsCard } from "@/features/home/components/QuickActionsCard";
import { NotificationBanner } from "@/features/home/components/NotificationBanner";
import { OverdueBillsSection, type BillItem } from "@/features/home/components/OverdueBillsSection";

const MOCK_BILLS: BillItem[] = [
  {
    id: "1",
    name: "Waste",
    amount: 3000,
    dueDate: "5 Dec 2025",
    iconColor: "#FF9500",
    iconLetter: "W",
  },
  {
    id: "2",
    name: "Security Levy",
    amount: 6700,
    dueDate: "5 Dec 2025",
    iconColor: "#1A237E",
    iconLetter: "S",
  },
];

export default function HomeScreen() {
  const router = useRouter();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const { greeting, firstName } = useHomeGreeting();
  const [bannerVisible, setBannerVisible] = useState(true);

  return (
    <View style={{ flex: 1, backgroundColor: colors.backgroundSecondary }}>
      <HomeHeader firstName={firstName} greeting={greeting} onMailPress={() => {}} />
      <ScrollView
        contentContainerStyle={{ paddingBottom: 32 }}
        showsVerticalScrollIndicator={false}
      >
        <View style={{ paddingHorizontal: 16 }}>
          <QuickActionsCard
            onManageHousehold={() => {}}
            onActiveCodes={() => {}}
            onNewAccessCode={() => router.push("/access-code/select-type")}
          />
        </View>

        {bannerVisible && (
          <View style={{ paddingHorizontal: 16, marginTop: 16 }}>
            <NotificationBanner
              title="New announcement"
              preview="Estate management has posted a new update about the upcoming maintenance work scheduled for next week."
              onDismiss={() => setBannerVisible(false)}
              onTapToOpen={() => {}}
            />
          </View>
        )}

        <View style={{ paddingHorizontal: 16, marginTop: 24 }}>
          <OverdueBillsSection
            bills={MOCK_BILLS}
            onSeeAll={() => router.push("/(tabs)/bills")}
            onPay={(_id) => {}}
          />
        </View>
      </ScrollView>
    </View>
  );
}

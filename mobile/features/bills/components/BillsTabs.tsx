import React, { useEffect } from "react";
import { View, Pressable, StyleSheet, useWindowDimensions } from "react-native";
import Animated, {
  Easing,
  useAnimatedStyle,
  useSharedValue,
  withTiming,
} from "react-native-reanimated";
import { BILL_TABS, BillStatus } from "../types";

const PRIMARY_DARK = "#00483C";
const INDICATOR_HEIGHT = 3;

interface BillsTabsProps {
  activeTab: BillStatus;
  onTabChange: (tab: BillStatus) => void;
}

export function BillsTabs({ activeTab, onTabChange }: BillsTabsProps) {
  const { width: screenWidth } = useWindowDimensions();
  const tabCount = BILL_TABS.length;
  const tabWidth = screenWidth / tabCount;
  const indicatorWidth = tabWidth * 0.5;

  const activeIndex = BILL_TABS.findIndex((t) => t.key === activeTab);

  const translateX = useSharedValue(activeIndex * tabWidth + (tabWidth - indicatorWidth) / 2);

  useEffect(() => {
    translateX.value = withTiming(activeIndex * tabWidth + (tabWidth - indicatorWidth) / 2, {
      duration: 250,
      easing: Easing.out(Easing.cubic),
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeIndex, tabWidth, indicatorWidth]);

  const indicatorStyle = useAnimatedStyle(() => ({
    transform: [{ translateX: translateX.value }],
  }));

  return (
    <View style={styles.container}>
      <View style={styles.tabRow}>
        {BILL_TABS.map((tab) => {
          const isActive = activeTab === tab.key;
          return (
            <Pressable key={tab.key} style={styles.tab} onPress={() => onTabChange(tab.key)}>
              <Animated.Text
                style={[
                  styles.label,
                  { color: isActive ? "#FFFFFF" : "rgba(255,255,255,0.5)" },
                  isActive && styles.labelActive,
                ]}
              >
                {tab.label}
              </Animated.Text>
            </Pressable>
          );
        })}
      </View>
      <Animated.View style={[styles.indicator, { width: indicatorWidth }, indicatorStyle]} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: PRIMARY_DARK,
    position: "relative",
    paddingBottom: INDICATOR_HEIGHT,
  },
  tabRow: {
    flexDirection: "row",
  },
  tab: {
    flex: 1,
    alignItems: "center",
    paddingVertical: 12,
  },
  label: {
    fontSize: 15,
    fontWeight: "500",
  },
  labelActive: {
    fontWeight: "700",
  },
  indicator: {
    position: "absolute",
    bottom: 0,
    left: 0,
    height: INDICATOR_HEIGHT,
    backgroundColor: "#05C756",
    borderTopLeftRadius: INDICATOR_HEIGHT,
    borderTopRightRadius: INDICATOR_HEIGHT,
  },
});

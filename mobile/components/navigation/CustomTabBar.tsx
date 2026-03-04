import type { BottomTabBarProps } from "@react-navigation/bottom-tabs";
import { CalendarDays, ClipboardList, Grip, House, Wallet } from "lucide-react-native";
import React, { useEffect } from "react";
import { Pressable, StyleSheet, useWindowDimensions, View } from "react-native";
import Animated, {
  Easing,
  useAnimatedStyle,
  useSharedValue,
  withTiming,
} from "react-native-reanimated";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import { Text } from "@/components/Themed";
import { useColorScheme } from "@/components/useColorScheme";
import Colors from "@/constants/Colors";

const ICON_MAP: Record<string, typeof House> = {
  index: House,
  bills: ClipboardList,
  wallet: Wallet,
  events: CalendarDays,
  contacts: Grip,
};

const INDICATOR_HEIGHT = 3;

export default function CustomTabBar({ state, descriptors, navigation }: BottomTabBarProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const { width: screenWidth } = useWindowDimensions();

  const tabCount = state.routes.length;
  const tabWidth = screenWidth / tabCount;
  const indicatorWidth = tabWidth * 0.5;

  const translateX = useSharedValue(state.index * tabWidth + (tabWidth - indicatorWidth) / 2);

  useEffect(() => {
    translateX.value = withTiming(state.index * tabWidth + (tabWidth - indicatorWidth) / 2, {
      duration: 250,
      easing: Easing.out(Easing.cubic),
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state.index, tabWidth, indicatorWidth]);

  const indicatorStyle = useAnimatedStyle(() => ({
    transform: [{ translateX: translateX.value }],
  }));

  return (
    <View
      style={[
        styles.container,
        {
          backgroundColor: colors.tabBackground,
          borderTopColor: colors.border,
          paddingBottom: insets.bottom,
        },
      ]}
    >
      <Animated.View
        style={[
          styles.indicator,
          { width: indicatorWidth, backgroundColor: colors.tint },
          indicatorStyle,
        ]}
      />

      <View style={styles.tabRow}>
        {state.routes.map((route, index) => {
          const { options } = descriptors[route.key];
          const label = options.title ?? route.name;
          const isFocused = state.index === index;
          const Icon = ICON_MAP[route.name] ?? House;

          const iconColor = isFocused ? colors.tintDark : colors.textTertiary;

          const onPress = () => {
            const event = navigation.emit({
              type: "tabPress",
              target: route.key,
              canPreventDefault: true,
            });

            if (!isFocused && !event.defaultPrevented) {
              navigation.navigate(route.name, route.params);
            }
          };

          const onLongPress = () => {
            navigation.emit({
              type: "tabLongPress",
              target: route.key,
            });
          };

          return (
            <Pressable
              key={route.key}
              accessibilityRole="button"
              accessibilityState={isFocused ? { selected: true } : {}}
              accessibilityLabel={options.tabBarAccessibilityLabel}
              onPress={onPress}
              onLongPress={onLongPress}
              style={styles.tab}
            >
              <Icon size={24} color={iconColor} strokeWidth={isFocused ? 2.2 : 1.8} />
              <Text
                style={[
                  styles.label,
                  {
                    color: iconColor,
                    fontWeight: isFocused ? "600" : "400",
                  },
                ]}
              >
                {label}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    borderTopWidth: StyleSheet.hairlineWidth,
    position: "relative",
  },
  indicator: {
    position: "absolute",
    top: 0,
    left: 0,
    height: INDICATOR_HEIGHT,
    borderBottomLeftRadius: INDICATOR_HEIGHT,
    borderBottomRightRadius: INDICATOR_HEIGHT,
  },
  tabRow: {
    flexDirection: "row",
    paddingTop: 8,
    paddingBottom: 4,
  },
  tab: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    gap: 4,
  },
  label: {
    fontSize: 11,
  },
});

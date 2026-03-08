import React, { useEffect } from "react";
import { Text, View } from "react-native";
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
  withDelay,
  runOnJS,
} from "react-native-reanimated";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import tw from "twrnc";
import { useToastStore } from "@/store/toastStore";

const TOAST_DURATION = 2500;
const ANIMATION_MS = 300;

export function Toast() {
  const message = useToastStore((s) => s.message);
  const clear = useToastStore((s) => s.clear);
  const insets = useSafeAreaInsets();

  const translateY = useSharedValue(-100);
  const opacity = useSharedValue(0);

  useEffect(() => {
    if (message) {
      translateY.value = withTiming(0, { duration: ANIMATION_MS });
      opacity.value = withTiming(1, { duration: ANIMATION_MS });

      // Slide out after duration
      translateY.value = withDelay(
        TOAST_DURATION,
        withTiming(-100, { duration: ANIMATION_MS }, (finished) => {
          if (finished) runOnJS(clear)();
        }),
      );
      opacity.value = withDelay(TOAST_DURATION, withTiming(0, { duration: ANIMATION_MS }));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [message]);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ translateY: translateY.value }],
    opacity: opacity.value,
  }));

  if (!message) return null;

  return (
    <Animated.View
      style={[tw`absolute left-4 right-4 z-50`, { top: insets.top + 8 }, animatedStyle]}
      pointerEvents="none"
    >
      <View
        style={[tw`rounded-xl px-4 py-3 flex-row items-center`, { backgroundColor: "#00352B" }]}
      >
        <Text style={tw`text-base text-white`}>{message}</Text>
      </View>
    </Animated.View>
  );
}

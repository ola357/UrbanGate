import { useEffect } from "react";
import { StyleSheet, View } from "react-native";
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
  withDelay,
  runOnJS,
  Easing,
} from "react-native-reanimated";
import { Image } from "expo-image";

const ICON_SOURCE = require("@/assets/images/logo-icon.png");
const FULL_LOGO_SOURCE = require("@/assets/images/logo-full.png");

const BG_COLOR = "#00483C";

interface Props {
  onComplete: () => void;
}

export function AnimatedSplash({ onComplete }: Props) {
  const iconOpacity = useSharedValue(0);
  const iconScale = useSharedValue(0.8);
  const fullLogoOpacity = useSharedValue(0);
  const containerOpacity = useSharedValue(1);

  useEffect(() => {
    // Phase 1: Icon fades/scales in (0–600ms)
    iconOpacity.value = withTiming(1, { duration: 600, easing: Easing.out(Easing.ease) });
    iconScale.value = withTiming(1, { duration: 600, easing: Easing.out(Easing.ease) });

    // Phase 2: Full logo fades in (1000–1500ms)
    fullLogoOpacity.value = withDelay(
      1000,
      withTiming(1, { duration: 500, easing: Easing.out(Easing.ease) })
    );

    // Phase 3: Entire splash fades out (2200–2700ms), then unmount
    containerOpacity.value = withDelay(
      2200,
      withTiming(0, { duration: 500, easing: Easing.in(Easing.ease) }, (finished) => {
        if (finished) {
          runOnJS(onComplete)();
        }
      })
    );
  }, []);

  const containerStyle = useAnimatedStyle(() => ({
    opacity: containerOpacity.value,
  }));

  const iconStyle = useAnimatedStyle(() => ({
    opacity: iconOpacity.value,
    transform: [{ scale: iconScale.value }],
  }));

  const fullLogoStyle = useAnimatedStyle(() => ({
    opacity: fullLogoOpacity.value,
  }));

  return (
    <Animated.View style={[styles.container, containerStyle]}>
      <View style={styles.content}>
        <Animated.View style={iconStyle}>
          <Image source={ICON_SOURCE} style={styles.icon} contentFit="contain" />
        </Animated.View>
        <Animated.View style={[styles.fullLogoWrap, fullLogoStyle]}>
          <Image source={FULL_LOGO_SOURCE} style={styles.fullLogo} contentFit="contain" />
        </Animated.View>
      </View>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: BG_COLOR,
    zIndex: 999,
    justifyContent: "center",
    alignItems: "center",
  },
  content: {
    alignItems: "center",
    justifyContent: "center",
  },
  icon: {
    width: 80,
    height: 80,
  },
  fullLogoWrap: {
    marginTop: 16,
  },
  fullLogo: {
    width: 200,
    height: 60,
  },
});

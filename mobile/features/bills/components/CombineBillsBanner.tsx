import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Info } from "lucide-react-native";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";

interface CombineBillsBannerProps {
  onCombine: () => void;
}

export function CombineBillsBanner({ onCombine }: CombineBillsBannerProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <View style={[styles.container, { backgroundColor: colors.card }]}>
      <Info size={20} color={colors.tint} style={styles.icon} />
      <Text style={[styles.text, { color: colors.textSecondary }]}>
        You can combine your due bills and{"\n"}pay them at once
      </Text>
      <TouchableOpacity onPress={onCombine} style={styles.button}>
        <Text style={[styles.buttonText, { color: colors.tint }]}>Combine</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    marginHorizontal: 16,
    marginTop: 16,
    padding: 14,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: "#E0E0E0",
  },
  icon: {
    marginRight: 10,
  },
  text: {
    flex: 1,
    fontSize: 13,
    lineHeight: 18,
  },
  button: {
    marginLeft: 8,
  },
  buttonText: {
    fontWeight: "700",
    fontSize: 14,
  },
});

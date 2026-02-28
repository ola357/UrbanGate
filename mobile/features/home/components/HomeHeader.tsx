import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Mail, Building2 } from "lucide-react-native";

const PRIMARY_DARK = "#00483C";
const ERROR_RED = "#E74C3C";

interface HomeHeaderProps {
  firstName: string;
  greeting: string;
  onMailPress: () => void;
}

export function HomeHeader({ firstName, greeting, onMailPress }: HomeHeaderProps) {
  const insets = useSafeAreaInsets();
  const initials = firstName !== "there" ? firstName.charAt(0).toUpperCase() : "?";

  return (
    <View style={[styles.container, { paddingTop: insets.top + 16 }]}>
      <View style={styles.topRow}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>{initials}</Text>
        </View>
        <TouchableOpacity onPress={onMailPress} style={styles.mailButton}>
          <Mail size={22} color="#FFFFFF" />
          <View style={styles.badge} />
        </TouchableOpacity>
      </View>

      <Text style={styles.greetingText}>{greeting}</Text>
      <Text style={styles.nameText}>{firstName}</Text>

      <View style={styles.buildingContainer} pointerEvents="none">
        <Building2 size={100} color="rgba(255,255,255,0.08)" />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: PRIMARY_DARK,
    paddingHorizontal: 16,
    paddingBottom: 24,
    overflow: "hidden",
  },
  topRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
  },
  avatarText: {
    color: PRIMARY_DARK,
    fontWeight: "700",
    fontSize: 16,
  },
  mailButton: {
    position: "relative",
  },
  badge: {
    position: "absolute",
    top: -2,
    right: -2,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: ERROR_RED,
  },
  greetingText: {
    color: "rgba(255,255,255,0.7)",
    fontSize: 16,
    fontWeight: "400",
  },
  nameText: {
    color: "#FFFFFF",
    fontSize: 28,
    fontWeight: "700",
    marginTop: 2,
  },
  buildingContainer: {
    position: "absolute",
    bottom: 0,
    right: 0,
  },
});

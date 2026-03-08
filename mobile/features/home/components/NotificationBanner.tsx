import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { X } from "lucide-react-native";

interface NotificationBannerProps {
  title: string;
  preview: string;
  onDismiss: () => void;
  onTapToOpen: () => void;
}

export function NotificationBanner({
  title,
  preview,
  onDismiss,
  onTapToOpen,
}: NotificationBannerProps) {
  return (
    <View style={styles.container}>
      <View style={styles.topRow}>
        <View style={styles.textContainer}>
          <Text style={styles.title}>{title}</Text>
          <Text style={styles.preview} numberOfLines={2}>
            {preview}
          </Text>
        </View>
        <TouchableOpacity onPress={onDismiss} hitSlop={{ top: 8, right: 8, bottom: 8, left: 8 }}>
          <X size={18} color="#FFFFFF" />
        </TouchableOpacity>
      </View>
      <TouchableOpacity onPress={onTapToOpen} style={styles.tapToOpen}>
        <Text style={styles.tapToOpenText}>TAP TO OPEN</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    borderRadius: 12,
    padding: 14,
    backgroundColor: "#4CAF50",
  },
  topRow: {
    flexDirection: "row",
    alignItems: "flex-start",
  },
  textContainer: {
    flex: 1,
    marginRight: 8,
  },
  title: {
    color: "#FFFFFF",
    fontWeight: "700",
    fontSize: 14,
    marginBottom: 2,
  },
  preview: {
    color: "rgba(255,255,255,0.85)",
    fontSize: 13,
  },
  tapToOpen: {
    marginTop: 8,
  },
  tapToOpenText: {
    fontSize: 11,
    fontWeight: "600",
    color: "rgba(255,255,255,0.75)",
    letterSpacing: 1.2,
    textTransform: "uppercase",
  },
});

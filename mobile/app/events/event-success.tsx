import React from "react";
import { View, Text, ScrollView, TouchableOpacity, Share } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft, LayoutGrid, Copy, Share as ShareIcon } from "lucide-react-native";
import * as Clipboard from "expo-clipboard";
import Svg, { Rect, Circle, Path } from "react-native-svg";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";

const fmt = (iso: string) =>
  new Date(iso).toLocaleString("en-US", {
    hour: "numeric",
    minute: "2-digit",
    hour12: true,
    month: "short",
    day: "numeric",
    year: "numeric",
  });

function CalendarIllustration() {
  return (
    <Svg width={100} height={80} viewBox="0 0 100 80">
      {/* Calendar body */}
      <Rect x="15" y="20" width="70" height="55" rx="6" fill="#E0E0E0" />
      {/* Calendar header */}
      <Rect x="15" y="20" width="70" height="18" rx="6" fill="#A5D6A7" />
      {/* Rings */}
      <Circle cx="35" cy="18" r="4" fill="#81C784" />
      <Circle cx="50" cy="18" r="4" fill="#81C784" />
      <Circle cx="65" cy="18" r="4" fill="#81C784" />
      {/* Page lines */}
      <Rect x="25" y="48" width="50" height="3" rx="1" fill="#BDBDBD" />
      <Rect x="25" y="56" width="35" height="3" rx="1" fill="#BDBDBD" />
      {/* Folded corner */}
      <Path d="M70 75 L85 60 L85 75 Z" fill="#BDBDBD" />
    </Svg>
  );
}

export default function EventSuccessScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams<{
    code: string;
    name: string;
    duration: string;
    validFrom: string;
    validUntil: string;
  }>();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const handleCopy = async () => {
    await Clipboard.setStringAsync(params.code ?? "");
  };

  const handleShare = async () => {
    await Share.share({
      message: `Event: ${params.name}\nCode: ${params.code}`,
    });
  };

  const durationText = params.duration || "2 hours";

  const tableRows = [
    { label: "Status", value: "status" },
    { label: "Valid from", value: params.validFrom ? fmt(params.validFrom) : "" },
    { label: "Valid until", value: params.validUntil ? fmt(params.validUntil) : "" },
  ];

  return (
    <View style={{ flex: 1, backgroundColor: "#00483C" }}>
      <ScrollView
        contentContainerStyle={{
          flexGrow: 1,
          paddingTop: insets.top + 16,
        }}
        showsVerticalScrollIndicator={false}
      >
        {/* Back button */}
        <TouchableOpacity
          onPress={() => router.replace("/(tabs)/events")}
          style={{ paddingHorizontal: 20, alignSelf: "flex-start" }}
        >
          <ChevronLeft size={24} color="#FFFFFF" />
        </TouchableOpacity>

        {/* Illustration */}
        <View style={{ alignItems: "center", marginTop: 24 }}>
          <CalendarIllustration />
        </View>

        {/* Title */}
        <Text
          style={{
            fontSize: 22,
            fontWeight: "700",
            color: "#FFFFFF",
            textAlign: "center",
            marginTop: 20,
          }}
        >
          Event created successfully
        </Text>
        <Text
          style={{
            fontSize: 14,
            color: "rgba(255,255,255,0.7)",
            textAlign: "center",
            marginTop: 8,
          }}
        >
          Code only valid for {durationText}.
        </Text>

        {/* White content area */}
        <View
          style={{
            marginTop: 24,
            borderTopLeftRadius: 24,
            borderTopRightRadius: 24,
            backgroundColor: colors.background,
            paddingHorizontal: 20,
            paddingTop: 28,
            paddingBottom: insets.bottom + 40,
            flex: 1,
          }}
        >
          {/* Code badge */}
          <View
            style={{
              borderRadius: 12,
              backgroundColor: "#05C756",
              paddingVertical: 18,
              paddingHorizontal: 20,
              flexDirection: "row",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <LayoutGrid size={20} color="white" />
            <Text
              style={{
                color: "white",
                fontSize: 24,
                fontWeight: "700",
                letterSpacing: 4,
                marginLeft: 12,
              }}
            >
              {params.code}
            </Text>
          </View>

          {/* Action buttons */}
          <View style={{ marginTop: 16, flexDirection: "row", gap: 12 }}>
            <TouchableOpacity
              onPress={handleCopy}
              style={{
                flex: 1,
                height: 44,
                borderRadius: 22,
                backgroundColor: colorScheme === "dark" ? "#333" : "#F0F0F0",
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "center",
                gap: 8,
              }}
            >
              <Copy size={16} color={colors.text} />
              <Text style={{ fontSize: 14, fontWeight: "500", color: colors.text }}>Copy</Text>
            </TouchableOpacity>

            <TouchableOpacity
              onPress={handleShare}
              style={{
                flex: 1,
                height: 44,
                borderRadius: 22,
                backgroundColor: colorScheme === "dark" ? "#333" : "#F0F0F0",
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "center",
                gap: 8,
              }}
            >
              <ShareIcon size={16} color={colors.text} />
              <Text style={{ fontSize: 14, fontWeight: "500", color: colors.text }}>Share</Text>
            </TouchableOpacity>
          </View>

          {/* Details table */}
          <View style={{ marginTop: 24 }}>
            {tableRows.map((row, index) => (
              <View key={row.label}>
                {index > 0 && (
                  <View
                    style={{
                      height: 1,
                      backgroundColor: colors.border,
                      marginHorizontal: 0,
                    }}
                  />
                )}
                <View
                  style={{
                    flexDirection: "row",
                    justifyContent: "space-between",
                    alignItems: "center",
                    paddingVertical: 14,
                  }}
                >
                  <Text
                    style={{
                      fontSize: 14,
                      color: colors.textTertiary,
                    }}
                  >
                    {row.label}
                  </Text>
                  {row.value === "status" ? (
                    <View
                      style={{
                        flexDirection: "row",
                        alignItems: "center",
                        backgroundColor: colorScheme === "dark" ? "#1A3A33" : "#E8F5E9",
                        borderRadius: 12,
                        paddingHorizontal: 10,
                        paddingVertical: 4,
                      }}
                    >
                      <View
                        style={{
                          width: 8,
                          height: 8,
                          borderRadius: 4,
                          backgroundColor: "#4CAF50",
                        }}
                      />
                      <Text style={{ color: "#2E7D32", fontSize: 13, marginLeft: 4 }}>Active</Text>
                    </View>
                  ) : (
                    <Text style={{ fontSize: 14, color: colors.text }}>{row.value}</Text>
                  )}
                </View>
              </View>
            ))}
          </View>
        </View>
      </ScrollView>
    </View>
  );
}

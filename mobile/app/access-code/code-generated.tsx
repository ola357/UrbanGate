import React from "react";
import { View, Text, ScrollView, TouchableOpacity, Share } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft, LayoutGrid, Copy, Share as ShareIcon } from "lucide-react-native";
import * as Clipboard from "expo-clipboard";
import QRCode from "react-native-qrcode-svg";
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

export default function CodeGeneratedScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { code, accessType, created, validUntil } = useLocalSearchParams<{
    code: string;
    accessType: string;
    created: string;
    validUntil: string;
  }>();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const handleCopy = async () => {
    await Clipboard.setStringAsync(code ?? "");
  };

  const handleShare = async () => {
    await Share.share({ message: code ?? "" });
  };

  const tableRows = [
    { label: "ACCESS TYPE", value: accessType ?? "" },
    { label: "STATUS", value: "status" },
    { label: "CREATED", value: created ? fmt(created) : "" },
    { label: "VALID UNTIL", value: validUntil ? fmt(validUntil) : "" },
  ];

  return (
    <ScrollView
      style={{ flex: 1, backgroundColor: colors.background }}
      contentContainerStyle={{
        paddingHorizontal: 20,
        paddingTop: insets.top + 16,
        paddingBottom: insets.bottom + 24,
      }}
      showsVerticalScrollIndicator={false}
    >
      {/* Back button */}
      <TouchableOpacity onPress={() => router.back()} style={{ alignSelf: "flex-start" }}>
        <ChevronLeft size={24} color={colors.text} />
      </TouchableOpacity>

      {/* Title block */}
      <View style={{ marginTop: 24, alignItems: "center" }}>
        <Text style={{ fontSize: 24, fontWeight: "700", color: colors.text, textAlign: "center" }}>
          Visitor code generated!
        </Text>
        <Text
          style={{
            fontSize: 14,
            color: colors.textTertiary,
            marginTop: 8,
            textAlign: "center",
          }}
        >
          Your visitor can now use this code at the gate.
        </Text>
      </View>

      {/* QR code */}
      <View style={{ alignItems: "center", marginTop: 32 }}>
        <QRCode value={code ?? "ACCESS"} size={180} color="#05C756" backgroundColor="white" />
      </View>

      {/* Code badge */}
      <View
        style={{
          marginTop: 24,
          borderRadius: 12,
          backgroundColor: "#05C756",
          paddingVertical: 16,
          paddingHorizontal: 20,
          flexDirection: "row",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <View style={{ marginRight: 8 }}>
          <LayoutGrid size={18} color="white" />
        </View>
        <Text
          style={{
            color: "white",
            fontSize: 22,
            fontWeight: "700",
            letterSpacing: 4,
            marginLeft: 8,
          }}
        >
          {code}
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
            backgroundColor: "#F0F0F0",
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
            backgroundColor: "#F0F0F0",
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
      <View
        style={{
          marginTop: 32,
          borderRadius: 12,
          borderWidth: 1,
          borderColor: colors.border,
          overflow: "hidden",
        }}
      >
        {tableRows.map((row, index) => (
          <View key={row.label}>
            {index > 0 && (
              <View
                style={{ height: 1 / 3, backgroundColor: colors.border, marginHorizontal: 16 }}
              />
            )}
            <View
              style={{
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                paddingHorizontal: 16,
                paddingVertical: 12,
              }}
            >
              <Text
                style={{
                  fontSize: 11,
                  fontWeight: "600",
                  letterSpacing: 1,
                  color: colors.textTertiary,
                  textTransform: "uppercase",
                }}
              >
                {row.label}
              </Text>
              {row.value === "status" ? (
                <View
                  style={{
                    flexDirection: "row",
                    alignItems: "center",
                    backgroundColor: "#E8F5E9",
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
    </ScrollView>
  );
}

import { Button } from "@/components/ui/Buttons";
import { RadioCard } from "@/components/ui/RadioCard";
import { useColorScheme } from "@/components/useColorScheme";
import Colors from "@/constants/Colors";
import type { ResidentType } from "@/store/authStore";
import { router } from "expo-router";
import React, { useState } from "react";
import { Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import tw from "twrnc";

export default function WelcomeScreen() {
  const [selectedType, setSelectedType] = useState<ResidentType | null>(null);
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();

  const handleContinue = () => {
    if (selectedType === "existing") {
      router.push("/login");
    } else {
      router.push("/activation-code");
    }
  };

  return (
    <View
      style={[tw`flex-1 px-6`, { backgroundColor: colors.background, paddingTop: insets.top + 40 }]}
    >
      <Text style={[tw`text-3xl font-bold mb-2`, { color: colors.text }]}>Welcome</Text>
      <Text style={[tw`text-base mb-8`, { color: colors.textTertiary }]}>
        How would you like to get started?
      </Text>

      <View style={tw`gap-4 mb-auto`}>
        <RadioCard
          selected={selectedType === "new"}
          title="I'm a new resident"
          description="I have an activation code from my estate"
          onPress={() => setSelectedType("new")}
        />

        <RadioCard
          selected={selectedType === "existing"}
          title="I'm an existing resident"
          description="I want to login to my estate"
          onPress={() => setSelectedType("existing")}
        />
      </View>

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button fullWidth disabled={!selectedType} onPress={handleContinue}>
          Continue
        </Button>
      </View>
    </View>
  );
}

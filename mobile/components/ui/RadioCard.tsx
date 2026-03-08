import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "../useColorScheme";

interface RadioCardProps {
  selected: boolean;
  title: string;
  description: string;
  onPress: () => void;
}

export const RadioCard = ({ selected, title, description, onPress }: RadioCardProps) => {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <TouchableOpacity
      onPress={onPress}
      activeOpacity={0.7}
      style={[
        tw`rounded-2xl p-4 flex-row items-center justify-between`,
        {
          backgroundColor: selected ? colors.backgroundSecondary : colors.card,
          borderWidth: selected ? 0 : 1,
          borderColor: colors.border,
        },
      ]}
    >
      <View style={tw`flex-1 mr-4`}>
        <Text style={[tw`text-base font-semibold mb-1`, { color: colors.text }]}>{title}</Text>
        <Text style={[tw`text-sm`, { color: colors.textTertiary }]}>{description}</Text>
      </View>
      <View
        style={[
          tw`w-6 h-6 rounded-full border-2 items-center justify-center`,
          {
            borderColor: selected ? colors.tint : colors.border,
          },
        ]}
      >
        {selected && (
          <View style={[tw`w-3.5 h-3.5 rounded-full`, { backgroundColor: colors.tint }]} />
        )}
      </View>
    </TouchableOpacity>
  );
};

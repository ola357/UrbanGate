import React, { memo, useCallback } from "react";
import { View, Text, TouchableOpacity, Share } from "react-native";
import { ChevronDown, LayoutGrid, Copy, Share as ShareIcon } from "lucide-react-native";
import * as Clipboard from "expo-clipboard";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { EstateEvent } from "../types";

interface EventCardProps {
  event: EstateEvent;
  style?: any;
}

function formatDateParts(isoDate: string): { day: string; month: string } {
  const date = new Date(isoDate);
  return {
    day: date.getDate().toString().padStart(2, "0"),
    month: date
      .toLocaleString("en-US", { month: "short" })
      .toUpperCase(),
  };
}

const EventCard: React.FC<EventCardProps> = memo(({ event, style }) => {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const { day, month } = formatDateParts(event.date);

  const handleCopy = useCallback(async () => {
    await Clipboard.setStringAsync(event.code);
  }, [event.code]);

  const handleShare = useCallback(async () => {
    await Share.share({
      message: `Event: ${event.name}\nCode: ${event.code}`,
    });
  }, [event.name, event.code]);

  return (
    <View
      style={[
        tw`rounded-xl border overflow-hidden`,
        { backgroundColor: colors.card, borderColor: colors.border },
        style,
      ]}
    >
      {/* Top section: date, name, code, chevron */}
      <View style={tw`flex-row items-center p-4`}>
        {/* Date circle */}
        <View
          style={[
            tw`w-14 h-14 rounded-full items-center justify-center mr-3`,
            { backgroundColor: colorScheme === "dark" ? "#1A3A33" : "#E8F5E9" },
          ]}
        >
          <Text style={[tw`text-lg font-bold`, { color: colors.text }]}>
            {day}
          </Text>
          <Text style={[tw`text-[10px] font-semibold -mt-0.5`, { color: colors.textTertiary }]}>
            {month}
          </Text>
        </View>

        {/* Name + code */}
        <View style={tw`flex-1`}>
          <Text style={[tw`text-[15px] font-semibold`, { color: colors.text }]}>
            {event.name}
          </Text>
          <View
            style={[
              tw`flex-row items-center self-start mt-1.5 px-2.5 py-1 rounded-md`,
              { backgroundColor: "#05C756" },
            ]}
          >
            <LayoutGrid size={12} color="#FFFFFF" />
            <Text style={tw`text-white text-xs font-bold ml-1.5 tracking-wider`}>
              {event.code}
            </Text>
          </View>
        </View>

        {/* Expand chevron */}
        <View
          style={[
            tw`w-8 h-8 rounded-full items-center justify-center`,
            { backgroundColor: colorScheme === "dark" ? "#444" : "#F0F0F0" },
          ]}
        >
          <ChevronDown size={16} color={colors.textTertiary} />
        </View>
      </View>

      {/* Separator */}
      <View style={[tw`mx-4`, { height: 1, backgroundColor: colors.border }]} />

      {/* Bottom section: Copy / Share */}
      <View style={tw`flex-row`}>
        <TouchableOpacity
          onPress={handleCopy}
          style={tw`flex-1 flex-row items-center justify-center py-3`}
          activeOpacity={0.7}
        >
          <Copy size={15} color={colors.text} />
          <Text style={[tw`text-sm font-medium ml-2`, { color: colors.text }]}>
            Copy
          </Text>
        </TouchableOpacity>

        <View style={[tw`my-2`, { width: 1, backgroundColor: colors.border }]} />

        <TouchableOpacity
          onPress={handleShare}
          style={tw`flex-1 flex-row items-center justify-center py-3`}
          activeOpacity={0.7}
        >
          <ShareIcon size={15} color={colors.text} />
          <Text style={[tw`text-sm font-medium ml-2`, { color: colors.text }]}>
            Share
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
});

EventCard.displayName = "EventCard";
export default EventCard;

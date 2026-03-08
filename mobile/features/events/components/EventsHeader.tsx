import React, { memo } from "react";
import { View, Text, TouchableOpacity, StyleProp, ViewStyle } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Plus } from "lucide-react-native";
import tw from "twrnc";

interface EventsHeaderProps {
  onAdd: () => void;
  style?: StyleProp<ViewStyle>;
}

const EventsHeader: React.FC<EventsHeaderProps> = memo(({ onAdd, style }) => {
  const insets = useSafeAreaInsets();

  return (
    <View
      style={[tw`px-4 pb-5`, { backgroundColor: "#00483C", paddingTop: insets.top + 16 }, style]}
    >
      <View style={tw`flex-row items-center justify-between`}>
        <Text style={tw`text-white text-[24px] font-bold`}>Events</Text>
        <TouchableOpacity
          onPress={onAdd}
          style={[
            tw`w-11 h-11 rounded-full items-center justify-center`,
            { backgroundColor: "#05C756" },
          ]}
          activeOpacity={0.8}
        >
          <Plus size={22} color="#FFFFFF" />
        </TouchableOpacity>
      </View>
    </View>
  );
});

EventsHeader.displayName = "EventsHeader";
export default EventsHeader;

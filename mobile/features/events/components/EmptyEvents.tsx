import React, { memo } from "react";
import { View, Text, TouchableOpacity } from "react-native";
import { CalendarPlus } from "lucide-react-native";
import tw from "twrnc";

interface EmptyEventsProps {
  onCreateEvent: () => void;
  style?: any;
}

const EmptyEvents: React.FC<EmptyEventsProps> = memo(
  ({ onCreateEvent, style }) => {
    return (
      <View style={[tw`flex-1 items-center justify-center px-8`, style]}>
        <Text style={tw`text-lg font-bold text-black`}>No events yet</Text>
        <Text style={tw`text-sm text-gray-500 text-center mt-2 leading-5`}>
          Events help you organise gatherings and control guest entry within the
          estate.
        </Text>
        <TouchableOpacity
          onPress={onCreateEvent}
          style={[
            tw`flex-row items-center mt-6 px-5 h-12 rounded-full`,
            { backgroundColor: "#00483C" },
          ]}
          activeOpacity={0.8}
        >
          <CalendarPlus size={18} color="#FFFFFF" />
          <Text style={tw`text-white font-semibold text-[15px] ml-2`}>
            Create a new event
          </Text>
        </TouchableOpacity>
      </View>
    );
  }
);

EmptyEvents.displayName = "EmptyEvents";
export default EmptyEvents;

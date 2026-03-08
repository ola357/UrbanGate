import React, { memo, useState, useMemo, useCallback } from "react";
import { View, Text, TouchableOpacity, Modal, Pressable, StyleProp, ViewStyle } from "react-native";
import { FlashList } from "@shopify/flash-list";
import { ChevronDown, Check } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { EstateEvent, EventFilter } from "../types";
import EventCard from "./EventCard";

interface EventsListProps {
  events: EstateEvent[];
  style?: StyleProp<ViewStyle>;
}

const FILTER_OPTIONS: { key: EventFilter; label: string }[] = [
  { key: "all", label: "All" },
  { key: "active", label: "Active" },
  { key: "expired", label: "Expired" },
];

const EventsList: React.FC<EventsListProps> = memo(({ events, style }) => {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const [filter, setFilter] = useState<EventFilter>("all");
  const [showFilterModal, setShowFilterModal] = useState(false);

  const filteredEvents = useMemo(() => {
    if (filter === "all") return events;
    return events.filter((e) => e.status === filter);
  }, [events, filter]);

  const renderItem = useCallback(
    ({ item }: { item: EstateEvent }) => (
      <EventCard event={item} style={tw`mx-4 mb-3`} />
    ),
    []
  );

  const filterLabel = FILTER_OPTIONS.find((o) => o.key === filter)?.label ?? "All";

  return (
    <View style={[tw`flex-1`, style]}>
      {/* Filter header */}
      <View style={tw`flex-row items-center justify-between px-4 pt-4 pb-2`}>
        <Text style={[tw`text-base font-semibold`, { color: colors.text }]}>
          All events
        </Text>
        <TouchableOpacity
          onPress={() => setShowFilterModal(true)}
          style={tw`flex-row items-center`}
          activeOpacity={0.7}
        >
          <Text style={[tw`text-sm`, { color: colors.textTertiary }]}>
            Showing:{" "}
          </Text>
          <Text style={[tw`text-sm font-semibold`, { color: colors.text }]}>
            {filterLabel}
          </Text>
          <ChevronDown size={16} color={colors.text} style={tw`ml-1`} />
        </TouchableOpacity>
      </View>

      {/* Event list */}
      <FlashList
        data={filteredEvents}
        renderItem={renderItem}
        contentContainerStyle={{ paddingTop: 8, paddingBottom: 24 }}
        showsVerticalScrollIndicator={false}
      />

      {/* Filter modal */}
      <Modal
        visible={showFilterModal}
        transparent
        animationType="fade"
        onRequestClose={() => setShowFilterModal(false)}
      >
        <Pressable
          style={tw`flex-1 bg-black/40 justify-end`}
          onPress={() => setShowFilterModal(false)}
        >
          <Pressable
            style={[
              tw`rounded-t-2xl py-4`,
              { backgroundColor: colors.background },
            ]}
            onPress={(e) => e.stopPropagation()}
          >
            <Text
              style={[tw`text-base font-bold px-6 mb-2`, { color: colors.text }]}
            >
              Filter events
            </Text>
            {FILTER_OPTIONS.map((option) => (
              <TouchableOpacity
                key={option.key}
                onPress={() => {
                  setFilter(option.key);
                  setShowFilterModal(false);
                }}
                style={tw`flex-row items-center justify-between py-4 px-6`}
                activeOpacity={0.7}
              >
                <Text style={[tw`text-[15px]`, { color: colors.text }]}>
                  {option.label}
                </Text>
                {filter === option.key && <Check size={18} color="#05C756" />}
              </TouchableOpacity>
            ))}
          </Pressable>
        </Pressable>
      </Modal>
    </View>
  );
});

EventsList.displayName = "EventsList";
export default EventsList;

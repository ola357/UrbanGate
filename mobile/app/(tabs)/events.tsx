import React from "react";
import { View, StyleSheet } from "react-native";
import { useRouter } from "expo-router";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import EventsHeader from "@/features/events/components/EventsHeader";
import EmptyEvents from "@/features/events/components/EmptyEvents";
import EventsList from "@/features/events/components/EventsList";
import { useEvents } from "@/features/events/hooks/useEvents";

export default function EventsScreen() {
  const router = useRouter();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const { data: events } = useEvents();

  const handleCreateEvent = () => {
    router.push("/events/new-event");
  };

  const hasEvents = events && events.length > 0;

  return (
    <View style={[styles.container, { backgroundColor: "#00483C" }]}>
      <EventsHeader onAdd={handleCreateEvent} />

      <View style={[styles.content, { backgroundColor: colors.backgroundSecondary }]}>
        {hasEvents ? (
          <EventsList events={events} />
        ) : (
          <EmptyEvents onCreateEvent={handleCreateEvent} />
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
  },
});

import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, Modal, Platform } from "react-native";
import { useRouter } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft, CalendarDays } from "lucide-react-native";
import DateTimePicker, { DateTimePickerEvent } from "@react-native-community/datetimepicker";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Buttons";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { useCreateEvent } from "@/features/events/hooks/useEvents";
import { EventFormData } from "@/features/events/types";

function formatDate(date: Date): string {
  return date.toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

interface DateFieldProps {
  date: Date | null;
  onChange: (date: Date) => void;
  error?: string;
  label?: string;
}

function DateField({ date, onChange, error, label = "Date of visit" }: DateFieldProps) {
  const [showPicker, setShowPicker] = useState(false);
  const [tempDate, setTempDate] = useState(new Date());
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const handleOpen = () => {
    setTempDate(date ?? new Date());
    setShowPicker(true);
  };

  const handleChange = (_: DateTimePickerEvent, selectedDate?: Date) => {
    if (Platform.OS === "android") {
      setShowPicker(false);
      if (selectedDate) onChange(selectedDate);
    } else {
      if (selectedDate) setTempDate(selectedDate);
    }
  };

  const handleDone = () => {
    onChange(tempDate);
    setShowPicker(false);
  };

  return (
    <View style={{ width: "100%" }}>
      <Text
        style={{ fontSize: 14, fontWeight: "500", color: colors.textSecondary, marginBottom: 8 }}
      >
        {label}
      </Text>
      <TouchableOpacity
        onPress={handleOpen}
        style={{
          height: 48,
          borderBottomWidth: 2,
          borderBottomColor: error ? colors.error : colors.border,
          flexDirection: "row",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <Text style={{ fontSize: 16, color: date ? colors.text : colors.textTertiary }}>
          {date ? formatDate(date) : "Select date"}
        </Text>
        <CalendarDays size={20} color={colors.textTertiary} />
      </TouchableOpacity>
      {error ? (
        <Text style={{ fontSize: 12, color: colors.error, marginTop: 4 }}>{error}</Text>
      ) : null}

      {Platform.OS === "android" && showPicker && (
        <DateTimePicker value={tempDate} mode="date" display="default" onChange={handleChange} />
      )}

      {Platform.OS === "ios" && (
        <Modal
          visible={showPicker}
          transparent
          animationType="slide"
          onRequestClose={() => setShowPicker(false)}
        >
          <TouchableOpacity
            style={{ flex: 1 }}
            activeOpacity={1}
            onPress={() => setShowPicker(false)}
          />
          <View
            style={{
              backgroundColor: "white",
              borderTopLeftRadius: 16,
              borderTopRightRadius: 16,
              paddingBottom: 24,
            }}
          >
            <View
              style={{
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                paddingHorizontal: 20,
                paddingTop: 16,
                paddingBottom: 8,
              }}
            >
              <TouchableOpacity onPress={() => setShowPicker(false)}>
                <Text style={{ fontSize: 16, color: colors.textTertiary }}>Cancel</Text>
              </TouchableOpacity>
              <Text style={{ fontSize: 16, fontWeight: "700", color: colors.text }}>{label}</Text>
              <TouchableOpacity onPress={handleDone}>
                <Text style={{ fontSize: 16, fontWeight: "600", color: "#05C756" }}>Done</Text>
              </TouchableOpacity>
            </View>
            <DateTimePicker
              value={tempDate}
              mode="date"
              display="spinner"
              onChange={handleChange}
              style={{ height: 200 }}
            />
          </View>
        </Modal>
      )}
    </View>
  );
}

export default function NewEventScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const createEvent = useCreateEvent();

  const [form, setForm] = useState<EventFormData>({
    name: "",
    date: null,
    expectedGuests: "",
    location: "",
    duration: "",
    description: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const updateField = (key: keyof EventFormData, value: string | Date | null) => {
    setForm((prev) => ({ ...prev, [key]: value }));
    if (errors[key]) setErrors((prev) => ({ ...prev, [key]: "" }));
  };

  const validate = () => {
    const newErrors: Record<string, string> = {};

    // Event name
    const name = form.name.trim();
    if (!name) {
      newErrors.name = "Event name is required";
    } else if (name.length < 2) {
      newErrors.name = "Event name must be at least 2 characters";
    } else if (name.length > 100) {
      newErrors.name = "Event name must be under 100 characters";
    }

    // Date
    if (!form.date) {
      newErrors.date = "Please select a date";
    } else {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const selected = new Date(form.date);
      selected.setHours(0, 0, 0, 0);
      if (selected < today) {
        newErrors.date = "Date cannot be in the past";
      }
    }

    // Expected guests
    const guestsStr = form.expectedGuests.trim();
    if (!guestsStr) {
      newErrors.expectedGuests = "Number of guests is required";
    } else if (!/^\d+$/.test(guestsStr)) {
      newErrors.expectedGuests = "Please enter a valid number";
    } else {
      const guests = parseInt(guestsStr, 10);
      if (guests < 1) {
        newErrors.expectedGuests = "Must have at least 1 guest";
      } else if (guests > 10000) {
        newErrors.expectedGuests = "Cannot exceed 10,000 guests";
      }
    }

    // Location
    const location = form.location.trim();
    if (!location) {
      newErrors.location = "Location is required";
    } else if (location.length < 2) {
      newErrors.location = "Location must be at least 2 characters";
    }

    // Duration
    const durationStr = form.duration.trim();
    if (!durationStr) {
      newErrors.duration = "Duration is required";
    } else {
      const durationNum = parseInt(durationStr, 10);
      if (isNaN(durationNum) || durationNum < 1) {
        newErrors.duration = "Please enter a valid duration (e.g. 2 hours)";
      } else if (durationNum > 72) {
        newErrors.duration = "Duration cannot exceed 72 hours";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleCreate = () => {
    if (!validate()) return;

    createEvent.mutate(form, {
      onSuccess: (event) => {
        router.push({
          pathname: "/events/event-success",
          params: {
            code: event.code,
            name: event.name,
            duration: event.duration,
            validFrom: event.validFrom,
            validUntil: event.validUntil,
          },
        });
      },
    });
  };

  return (
    <View style={{ flex: 1, backgroundColor: colors.background }}>
      {/* Header */}
      <View
        style={{
          paddingTop: insets.top + 12,
          paddingHorizontal: 16,
          paddingBottom: 12,
          flexDirection: "row",
          alignItems: "center",
          backgroundColor: colors.background,
        }}
      >
        <TouchableOpacity onPress={() => router.back()}>
          <ChevronLeft size={24} color={colors.text} />
        </TouchableOpacity>
        <Text
          style={{
            flex: 1,
            textAlign: "center",
            color: colors.text,
            fontWeight: "700",
            fontSize: 18,
          }}
        >
          New event
        </Text>
        <View style={{ width: 24 }} />
      </View>

      {/* Form */}
      <ScrollView
        contentContainerStyle={{
          paddingHorizontal: 20,
          paddingTop: 24,
          paddingBottom: 24,
          gap: 24,
        }}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled"
      >
        <Input
          variant="underlined"
          label="Event name"
          placeholder="Enter event name"
          value={form.name}
          onChangeText={(v) => updateField("name", v)}
          error={errors.name}
          maxLength={100}
        />

        <DateField
          date={form.date}
          onChange={(d) => updateField("date", d)}
          error={errors.date}
        />

        <Input
          variant="underlined"
          label="Number of expected guests"
          placeholder="Enter number"
          keyboardType="number-pad"
          value={form.expectedGuests}
          onChangeText={(v) => updateField("expectedGuests", v.replace(/[^0-9]/g, ""))}
          error={errors.expectedGuests}
          maxLength={5}
        />

        <Input
          variant="underlined"
          label="Location"
          placeholder="Enter location"
          value={form.location}
          onChangeText={(v) => updateField("location", v)}
          error={errors.location}
        />

        <Input
          variant="underlined"
          label="Duration"
          placeholder="e.g. 2 hours"
          value={form.duration}
          onChangeText={(v) => updateField("duration", v)}
          error={errors.duration}
        />

        <Input
          variant="underlined"
          label="Description"
          placeholder="Enter description"
          value={form.description}
          onChangeText={(v) => updateField("description", v)}
          error={errors.description}
        />

        <View style={{ height: 80 }} />
      </ScrollView>

      {/* Footer */}
      <View
        style={{
          paddingBottom: insets.bottom + 16,
          paddingHorizontal: 20,
          backgroundColor: colors.background,
        }}
      >
        <Button fullWidth onPress={handleCreate} isLoading={createEvent.isPending}>
          Create event
        </Button>
      </View>
    </View>
  );
}

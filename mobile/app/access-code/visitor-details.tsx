import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, Modal, Platform } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft, CalendarDays, Check } from "lucide-react-native";
import DateTimePicker, { DateTimePickerEvent } from "@react-native-community/datetimepicker";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Buttons";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";

const TITLE: Record<string, string> = {
  single: "Single visitor code",
  group: "Group visit code",
  multiple: "New multi-pass code",
  "all-gate": "All-gate access code",
};

const ACCESS_TYPE_LABEL: Record<string, string> = {
  single: "Single visitor access",
  group: "Group visit access",
  multiple: "Multi-pass access",
  "all-gate": "All-gate access",
};

const VISITOR_TYPES = ["Guest", "Staff", "Delivery", "Contractor", "Other"];

function formatDate(date: Date): string {
  return date.toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

interface VisitorTypeSelectProps {
  value: string;
  onChange: (val: string) => void;
  error?: string;
}

function VisitorTypeSelect({ value, onChange, error }: VisitorTypeSelectProps) {
  const [showModal, setShowModal] = useState(false);
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <>
      <View style={{ width: "100%" }}>
        <Text
          style={{ fontSize: 14, fontWeight: "500", color: colors.textSecondary, marginBottom: 8 }}
        >
          Visitor type
        </Text>
        <TouchableOpacity
          onPress={() => setShowModal(true)}
          style={{
            height: 48,
            paddingHorizontal: 0,
            borderBottomWidth: 2,
            borderBottomColor: error ? colors.error : colors.border,
            justifyContent: "center",
          }}
        >
          <Text style={{ fontSize: 16, color: value ? colors.text : colors.textTertiary }}>
            {value || "Select visitor type"}
          </Text>
        </TouchableOpacity>
        {error ? (
          <Text style={{ fontSize: 12, color: colors.error, marginTop: 4 }}>{error}</Text>
        ) : null}
      </View>

      <Modal
        visible={showModal}
        transparent
        animationType="slide"
        onRequestClose={() => setShowModal(false)}
      >
        <TouchableOpacity
          style={{ flex: 1, backgroundColor: "rgba(0,0,0,0.4)" }}
          activeOpacity={1}
          onPress={() => setShowModal(false)}
        />
        <View
          style={{
            backgroundColor: "white",
            borderTopLeftRadius: 16,
            borderTopRightRadius: 16,
            paddingVertical: 16,
          }}
        >
          <Text style={{ fontSize: 16, fontWeight: "700", paddingHorizontal: 24, marginBottom: 8 }}>
            Select visitor type
          </Text>
          {VISITOR_TYPES.map((type) => (
            <TouchableOpacity
              key={type}
              onPress={() => {
                onChange(type);
                setShowModal(false);
              }}
              style={{
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "space-between",
                paddingVertical: 16,
                paddingHorizontal: 24,
              }}
            >
              <Text style={{ fontSize: 15, color: "#000" }}>{type}</Text>
              {value === type && <Check size={18} color="#05C756" />}
            </TouchableOpacity>
          ))}
        </View>
      </Modal>
    </>
  );
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

      {/* Android: renders inline native dialog */}
      {Platform.OS === "android" && showPicker && (
        <DateTimePicker value={tempDate} mode="date" display="default" onChange={handleChange} />
      )}

      {/* iOS: Modal with spinner + Done button */}
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

export default function VisitorDetailsScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { type } = useLocalSearchParams<{ type: string }>();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const [visitorName, setVisitorName] = useState("");
  const [visitorPhone, setVisitorPhone] = useState("");
  const [visitorType, setVisitorType] = useState("");
  const [visitDate, setVisitDate] = useState<Date | null>(null);
  const [purpose, setPurpose] = useState("");
  const [numberOfGuests, setNumberOfGuests] = useState("");
  const [groupName, setGroupName] = useState("");
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);

  const [errors, setErrors] = useState<Record<string, string>>({});

  const title = TITLE[type ?? ""] ?? "Visitor code";

  const validate = () => {
    const newErrors: Record<string, string> = {};

    if (type === "group") {
      if (!numberOfGuests.trim()) newErrors.numberOfGuests = "Number of guests is required";
      if (!groupName.trim()) newErrors.groupName = "Group name is required";
      if (!visitDate) newErrors.visitDate = "Please select a date";
      if (!purpose.trim()) newErrors.purpose = "Purpose is required";
    } else if (type === "multiple") {
      if (!visitorName.trim()) newErrors.visitorName = "Visitor name is required";
      if (!visitorPhone.trim()) newErrors.visitorPhone = "Phone number is required";
      if (!visitorType) newErrors.visitorType = "Please select a visitor type";
      if (!startDate) newErrors.startDate = "Please select a start date";
      if (!endDate) newErrors.endDate = "Please select an end date";
    } else if (type === "all-gate") {
      if (!visitorName.trim()) newErrors.visitorName = "Visitor name is required";
      if (!visitorPhone.trim()) newErrors.visitorPhone = "Phone number is required";
      if (!visitDate) newErrors.visitDate = "Please select a date";
      if (!visitorType) newErrors.visitorType = "Please select a visitor type";
    } else {
      if (!visitorName.trim()) newErrors.visitorName = "Visitor name is required";
      if (!visitorPhone.trim()) newErrors.visitorPhone = "Phone number is required";
      if (!visitorType) newErrors.visitorType = "Please select a visitor type";
      if (!visitDate) newErrors.visitDate = "Please select a date";
      if (!purpose.trim()) newErrors.purpose = "Purpose is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleGenerate = () => {
    if (!validate()) return;

    const code = [3, 2, 1]
      .map(() => Math.random().toString(36).substring(2, 4).toUpperCase())
      .join(" ");
    const now = new Date().toISOString();
    const validUntil =
      type === "multiple" ? (endDate?.toISOString() ?? now) : (visitDate?.toISOString() ?? now);
    router.push({
      pathname: "/access-code/code-generated",
      params: {
        code,
        type,
        accessType: ACCESS_TYPE_LABEL[type ?? ""] ?? type,
        created: now,
        validUntil,
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
          {title}
        </Text>
        <View style={{ width: 24 }} />
      </View>

      {/* Body */}
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
        {/* GROUP */}
        {type === "group" && (
          <>
            <Input
              variant="underlined"
              label="Number of guests"
              placeholder="Enter number of guests"
              keyboardType="number-pad"
              value={numberOfGuests}
              onChangeText={(v) => {
                setNumberOfGuests(v);
                if (errors.numberOfGuests) setErrors((e) => ({ ...e, numberOfGuests: "" }));
              }}
              error={errors.numberOfGuests}
            />
            <Input
              variant="underlined"
              label="Group name"
              placeholder="Enter group name"
              value={groupName}
              onChangeText={(v) => {
                setGroupName(v);
                if (errors.groupName) setErrors((e) => ({ ...e, groupName: "" }));
              }}
              error={errors.groupName}
            />
            <DateField
              date={visitDate}
              onChange={(d) => {
                setVisitDate(d);
                if (errors.visitDate) setErrors((e) => ({ ...e, visitDate: "" }));
              }}
              error={errors.visitDate}
            />
            <Input
              variant="underlined"
              label="Purpose"
              placeholder="Enter purpose of visit"
              value={purpose}
              onChangeText={(v) => {
                setPurpose(v);
                if (errors.purpose) setErrors((e) => ({ ...e, purpose: "" }));
              }}
              error={errors.purpose}
            />
          </>
        )}

        {/* MULTI-PASS */}
        {type === "multiple" && (
          <>
            <Input
              variant="underlined"
              label="Visitor name"
              placeholder="Enter visitor name"
              value={visitorName}
              onChangeText={(v) => {
                setVisitorName(v);
                if (errors.visitorName) setErrors((e) => ({ ...e, visitorName: "" }));
              }}
              error={errors.visitorName}
            />
            <Input
              variant="underlined"
              label="Visitor phone number"
              placeholder="Enter phone number"
              keyboardType="phone-pad"
              value={visitorPhone}
              onChangeText={(v) => {
                setVisitorPhone(v);
                if (errors.visitorPhone) setErrors((e) => ({ ...e, visitorPhone: "" }));
              }}
              error={errors.visitorPhone}
            />
            <VisitorTypeSelect
              value={visitorType}
              onChange={(v) => {
                setVisitorType(v);
                if (errors.visitorType) setErrors((e) => ({ ...e, visitorType: "" }));
              }}
              error={errors.visitorType}
            />
            <DateField
              label="Start date"
              date={startDate}
              onChange={(d) => {
                setStartDate(d);
                if (errors.startDate) setErrors((e) => ({ ...e, startDate: "" }));
              }}
              error={errors.startDate}
            />
            <DateField
              label="End date"
              date={endDate}
              onChange={(d) => {
                setEndDate(d);
                if (errors.endDate) setErrors((e) => ({ ...e, endDate: "" }));
              }}
              error={errors.endDate}
            />
          </>
        )}

        {/* ALL-GATE */}
        {type === "all-gate" && (
          <>
            <Input
              variant="underlined"
              label="Visitor name"
              placeholder="Enter visitor name"
              value={visitorName}
              onChangeText={(v) => {
                setVisitorName(v);
                if (errors.visitorName) setErrors((e) => ({ ...e, visitorName: "" }));
              }}
              error={errors.visitorName}
            />
            <Input
              variant="underlined"
              label="Visitor phone number"
              placeholder="Enter phone number"
              keyboardType="phone-pad"
              value={visitorPhone}
              onChangeText={(v) => {
                setVisitorPhone(v);
                if (errors.visitorPhone) setErrors((e) => ({ ...e, visitorPhone: "" }));
              }}
              error={errors.visitorPhone}
            />
            <DateField
              date={visitDate}
              onChange={(d) => {
                setVisitDate(d);
                if (errors.visitDate) setErrors((e) => ({ ...e, visitDate: "" }));
              }}
              error={errors.visitDate}
            />
            <VisitorTypeSelect
              value={visitorType}
              onChange={(v) => {
                setVisitorType(v);
                if (errors.visitorType) setErrors((e) => ({ ...e, visitorType: "" }));
              }}
              error={errors.visitorType}
            />
          </>
        )}

        {/* SINGLE (default) */}
        {(type === "single" || !type) && (
          <>
            <Input
              variant="underlined"
              label="Visitor name"
              placeholder="Enter visitor name"
              value={visitorName}
              onChangeText={(v) => {
                setVisitorName(v);
                if (errors.visitorName) setErrors((e) => ({ ...e, visitorName: "" }));
              }}
              error={errors.visitorName}
            />
            <Input
              variant="underlined"
              label="Visitor phone number"
              placeholder="Enter phone number"
              keyboardType="phone-pad"
              value={visitorPhone}
              onChangeText={(v) => {
                setVisitorPhone(v);
                if (errors.visitorPhone) setErrors((e) => ({ ...e, visitorPhone: "" }));
              }}
              error={errors.visitorPhone}
            />
            <VisitorTypeSelect
              value={visitorType}
              onChange={(v) => {
                setVisitorType(v);
                if (errors.visitorType) setErrors((e) => ({ ...e, visitorType: "" }));
              }}
              error={errors.visitorType}
            />
            <DateField
              date={visitDate}
              onChange={(d) => {
                setVisitDate(d);
                if (errors.visitDate) setErrors((e) => ({ ...e, visitDate: "" }));
              }}
              error={errors.visitDate}
            />
            <Input
              variant="underlined"
              label="Purpose"
              placeholder="Enter purpose of visit"
              value={purpose}
              onChangeText={(v) => {
                setPurpose(v);
                if (errors.purpose) setErrors((e) => ({ ...e, purpose: "" }));
              }}
              error={errors.purpose}
            />
          </>
        )}

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
        <Button fullWidth onPress={handleGenerate}>
          Generate access code
        </Button>
      </View>
    </View>
  );
}

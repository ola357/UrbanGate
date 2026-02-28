import React, { useRef } from "react";
import { TextInput, View } from "react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";

interface OtpInputProps {
  length?: number;
  value: string;
  onChangeText: (text: string) => void;
  error?: string;
}

export function OtpInput({
  length = 6,
  value,
  onChangeText,
  error,
}: OtpInputProps) {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const refs = useRef<(TextInput | null)[]>([]);

  const digits = value.split("").concat(Array(length).fill("")).slice(0, length);

  const handleChange = (text: string, index: number) => {
    const digit = text.replace(/[^0-9]/g, "").slice(-1);
    const newValue = digits.map((d, i) => (i === index ? digit : d)).join("");
    onChangeText(newValue.replace(/\s/g, ""));

    if (digit && index < length - 1) {
      refs.current[index + 1]?.focus();
    }
  };

  const handleKeyPress = (
    e: { nativeEvent: { key: string } },
    index: number,
  ) => {
    if (e.nativeEvent.key === "Backspace" && !digits[index] && index > 0) {
      refs.current[index - 1]?.focus();
      const newValue = digits.map((d, i) => (i === index - 1 ? "" : d)).join("");
      onChangeText(newValue.replace(/\s/g, ""));
    }
  };

  return (
    <View style={tw`flex-row justify-between`}>
      {Array.from({ length }).map((_, index) => {
        const isFocusable = index === value.length || (index === length - 1 && value.length >= length);
        return (
          <TextInput
            key={index}
            ref={(ref) => {
              refs.current[index] = ref;
            }}
            style={[
              tw`w-12 h-14 text-center text-2xl font-bold`,
              {
                color: colors.text,
                borderBottomWidth: 2,
                borderBottomColor: error
                  ? colors.error
                  : digits[index]
                    ? colors.tintDark
                    : colors.border,
              },
            ]}
            value={digits[index]?.trim() || ""}
            onChangeText={(text) => handleChange(text, index)}
            onKeyPress={(e) => handleKeyPress(e, index)}
            keyboardType="number-pad"
            maxLength={1}
            selectTextOnFocus
            onFocus={() => {
              if (!isFocusable && value.length < length) {
                refs.current[value.length]?.focus();
              }
            }}
          />
        );
      })}
    </View>
  );
}

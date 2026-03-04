import React, { useState } from "react";
import { StyleProp, Text, TextInput, TextInputProps, TextStyle, View } from "react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "../useColorScheme";

export type InputVariant = "default" | "filled" | "underlined";
export type InputSize = "sm" | "md" | "lg";

interface InputProps extends TextInputProps {
  variant?: InputVariant;
  size?: InputSize;
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Input = ({
  variant = "default",
  size = "md",
  label,
  error,
  helperText,
  leftIcon,
  rightIcon,
  style,
  ...props
}: InputProps) => {
  const [isFocused, setIsFocused] = useState(false);
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  const getVariantStyles = () => {
    switch (variant) {
      case "default":
        return [
          tw`border-2 rounded-lg`,
          {
            borderColor: error ? colors.error : isFocused ? colors.tintDark : colors.border,
            backgroundColor: colors.card,
          },
        ];
      case "filled":
        return [
          tw`border-0 rounded-lg`,
          {
            backgroundColor: colors.backgroundSecondary,
            borderBottomWidth: 2,
            borderBottomColor: error ? colors.error : isFocused ? colors.tintDark : "transparent",
          },
        ];
      case "underlined":
        return [
          tw`border-0 border-b-2 rounded-none bg-transparent`,
          {
            borderBottomColor: error ? colors.error : isFocused ? colors.tintDark : colors.border,
          },
        ];
      default:
        return [
          tw`border-2 rounded-lg`,
          {
            borderColor: error ? colors.error : isFocused ? colors.tintDark : colors.border,
            backgroundColor: colors.card,
          },
        ];
    }
  };

  const getSizeStyles = () => {
    switch (size) {
      case "sm":
        return tw`h-10 px-3 text-sm`;
      case "md":
        return tw`h-12 px-4 text-base`;
      case "lg":
        return tw`h-14 px-4 text-lg`;
      default:
        return tw`h-12 px-4 text-base`;
    }
  };

  const variantStyles = getVariantStyles();
  const sizeStyles = getSizeStyles();

  return (
    <View style={tw`w-full`}>
      {label && (
        <Text style={[tw`text-sm font-medium mb-2`, { color: colors.textSecondary }]}>{label}</Text>
      )}
      <View style={tw`relative`}>
        {leftIcon && (
          <View style={tw`absolute left-3 top-0 h-full justify-center z-10`}>{leftIcon}</View>
        )}
        <TextInput
          style={
            [
              { color: colors.text },
              ...variantStyles,
              sizeStyles,
              leftIcon && tw`pl-10`,
              rightIcon && tw`pr-10`,
              style,
            ] as StyleProp<TextStyle>
          }
          {...props}
          placeholderTextColor={colors.textTertiary}
          onFocus={(e) => {
            setIsFocused(true);
            props.onFocus?.(e);
          }}
          onBlur={(e) => {
            setIsFocused(false);
            props.onBlur?.(e);
          }}
        />
        {rightIcon && (
          <View style={tw`absolute right-3 top-0 h-full justify-center z-10`}>{rightIcon}</View>
        )}
      </View>
      {error && <Text style={[tw`text-xs mt-1`, { color: colors.error }]}>{error}</Text>}
      {helperText && !error && (
        <Text style={[tw`text-xs mt-1`, { color: colors.textTertiary }]}>{helperText}</Text>
      )}
    </View>
  );
};

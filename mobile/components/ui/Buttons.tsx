import React from "react";
import {
  ActivityIndicator,
  Text,
  TouchableOpacity,
  TouchableOpacityProps,
} from "react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "../useColorScheme";

export type ButtonVariant = "primary" | "secondary" | "outline" | "ghost";
export type ButtonSize = "sm" | "md" | "lg";

interface ButtonProps extends TouchableOpacityProps {
  variant?: ButtonVariant;
  size?: ButtonSize;
  children: string | number;
  isLoading?: boolean;
  fullWidth?: boolean;
}

export const Button = ({
  variant = "primary",
  size = "md",
  children,
  isLoading = false,
  fullWidth = false,
  disabled,
  style,
  ...props
}: ButtonProps) => {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const getVariantStyles = () => {
    switch (variant) {
      case "primary":
        return [{ backgroundColor: colors.buttonPrimary }];
      case "secondary":
        return [{ backgroundColor: colors.buttonSecondary }];
      case "outline":
        return [tw`bg-transparent border-2`, { borderColor: colors.tintDark }];
      case "ghost":
        return [tw`bg-transparent`];
      default:
        return [{ backgroundColor: colors.buttonPrimary }];
    }
  };

  const getTextStyles = () => {
    switch (variant) {
      case "primary":
        return [{ color: colors.buttonText }];
      case "secondary":
        return [{ color: colors.buttonTextSecondary }];
      case "outline":
      case "ghost":
        return [{ color: colors.tintDark }];
      default:
        return [{ color: colors.buttonText }];
    }
  };

  const getSizeStyles = () => {
    switch (size) {
      case "sm":
        return {
          button: tw`h-10 px-4 rounded-full`,
          text: tw`text-sm`,
        };
      case "md":
        return {
          button: tw`h-12 px-4 rounded-full`,
          text: tw`text-base`,
        };
      case "lg":
        return {
          button: tw`h-12 px-4 rounded-full`,
          text: tw`text-base`,
        };
      default:
        return {
          button: tw`h-12 px-4 rounded-full`,
          text: tw`text-base`,
        };
    }
  };

  const sizeStyles = getSizeStyles();
  const variantStyles = getVariantStyles();
  const textStyles = getTextStyles();

  return (
    <TouchableOpacity
      style={[
        tw`flex-row items-center justify-center`,
        sizeStyles.button,
        ...variantStyles,
        fullWidth && tw`w-full`,
        disabled && tw`opacity-50`,
        style,
      ]}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <ActivityIndicator
          color={
            variant === "outline" || variant === "ghost"
              ? colors.tintDark
              : variant === "secondary"
              ? colors.buttonTextSecondary
              : colors.buttonText
          }
        />
      ) : (
        <Text style={[tw`font-medium`, sizeStyles.text, ...textStyles]}>
          {children}
        </Text>
      )}
    </TouchableOpacity>
  );
};

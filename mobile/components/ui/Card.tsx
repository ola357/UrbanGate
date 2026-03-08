import React from "react";
import { TouchableOpacity, TouchableOpacityProps, View, ViewProps } from "react-native";
import tw from "twrnc";

export type CardVariant = "elevated" | "outlined" | "filled";
export type CardPadding = "none" | "sm" | "md" | "lg";

interface BaseCardProps {
  variant?: CardVariant;
  padding?: CardPadding;
  children: React.ReactNode;
}

type StaticCardProps = BaseCardProps &
  Omit<ViewProps, "children"> & {
    pressable?: false;
  };

type PressableCardProps = BaseCardProps &
  Omit<TouchableOpacityProps, "children"> & {
    pressable: true;
  };

type CardProps = StaticCardProps | PressableCardProps;

export const Card = ({
  variant = "elevated",
  padding = "md",
  children,
  style,
  pressable,
  ...props
}: CardProps) => {
  const getVariantStyles = () => {
    switch (variant) {
      case "elevated":
        return tw`bg-white shadow-md`;
      case "outlined":
        return tw`bg-white border-2 border-gray-200`;
      case "filled":
        return tw`bg-gray-50`;
      default:
        return tw`bg-white shadow-md`;
    }
  };

  const getPaddingStyles = () => {
    switch (padding) {
      case "none":
        return tw`p-0`;
      case "sm":
        return tw`p-3`;
      case "md":
        return tw`p-4`;
      case "lg":
        return tw`p-6`;
      default:
        return tw`p-4`;
    }
  };

  const variantStyles = getVariantStyles();
  const paddingStyles = getPaddingStyles();
  const baseStyles = [tw`rounded-2xl`, variantStyles, paddingStyles, style];

  if (pressable) {
    const { pressable: _, ...touchableProps } = props as PressableCardProps;
    return (
      <TouchableOpacity style={baseStyles} activeOpacity={0.7} {...touchableProps}>
        {children}
      </TouchableOpacity>
    );
  }

  return (
    <View style={baseStyles} {...(props as ViewProps)}>
      {children}
    </View>
  );
};

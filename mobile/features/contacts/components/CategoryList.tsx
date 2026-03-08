import React, { memo } from "react";
import { View, Text, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";
import { ChevronRight } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { CONTACT_CATEGORIES } from "../types";

const CategoryList: React.FC = memo(() => {
  const router = useRouter();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <View
      style={[
        tw`mx-4 my-4 rounded-2xl overflow-hidden`,
        { backgroundColor: colors.card },
      ]}
    >
      {CONTACT_CATEGORIES.map((category, index) => {
        const Icon = category.icon;
        const isLast = index === CONTACT_CATEGORIES.length - 1;

        return (
          <TouchableOpacity
            key={category.key}
            onPress={() => router.push(`/contacts/${category.key}` as any)}
            activeOpacity={0.7}
            style={tw`flex-row items-center px-4 py-3.5`}
          >
            <View
              style={[
                tw`w-10 h-10 rounded-xl items-center justify-center`,
                { backgroundColor: "#E8F5E9" },
              ]}
            >
              <Icon size={20} color="#00483C" />
            </View>

            <View style={tw`flex-1 ml-3`}>
              <Text style={[tw`text-[15px] font-semibold`, { color: colors.text }]}>
                {category.title}
              </Text>
              <Text style={[tw`text-xs mt-0.5`, { color: colors.textTertiary }]}>
                {category.subtitle}
              </Text>
            </View>

            <ChevronRight size={20} color={colors.textTertiary} />

            {!isLast && (
              <View
                style={[
                  tw`absolute bottom-0 right-4`,
                  {
                    left: 68,
                    height: 1,
                    backgroundColor: colors.border,
                  },
                ]}
              />
            )}
          </TouchableOpacity>
        );
      })}
    </View>
  );
});

CategoryList.displayName = "CategoryList";
export default CategoryList;

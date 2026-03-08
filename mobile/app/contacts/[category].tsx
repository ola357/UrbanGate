import React from "react";
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from "react-native";
import { useRouter, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { ContactCategoryKey, CONTACT_CATEGORIES } from "@/features/contacts/types";
import { useContactsByCategory } from "@/features/contacts/hooks/useContacts";
import ContactCard from "@/features/contacts/components/ContactCard";

export default function CategoryDetailScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const { category } = useLocalSearchParams<{ category: ContactCategoryKey }>();

  const config = CONTACT_CATEGORIES.find((c) => c.key === category);
  const { data: contacts, isLoading } = useContactsByCategory(category!);

  return (
    <View style={tw`flex-1`}>
      {/* Header */}
      <View style={{ backgroundColor: "#00483C" }}>
        <View
          style={[
            tw`px-4 pb-5 flex-row items-center`,
            { paddingTop: insets.top + 12 },
          ]}
        >
          <TouchableOpacity onPress={() => router.back()} activeOpacity={0.7}>
            <ChevronLeft size={24} color="#FFFFFF" />
          </TouchableOpacity>
          <Text style={tw`flex-1 text-white text-lg font-bold text-center mr-6`}>
            {config?.title ?? "Contacts"}
          </Text>
        </View>
      </View>

      {/* Content */}
      <ScrollView
        style={[tw`flex-1`, { backgroundColor: colors.background }]}
        contentContainerStyle={{ paddingTop: 20, paddingBottom: insets.bottom + 24 }}
        showsVerticalScrollIndicator={false}
      >
        {isLoading ? (
          <ActivityIndicator color="#00483C" style={tw`mt-8`} />
        ) : (
          contacts?.map((contact) => (
            <ContactCard key={contact.id} contact={contact} />
          ))
        )}
      </ScrollView>
    </View>
  );
}

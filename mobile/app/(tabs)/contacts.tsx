import React from "react";
import { View, ScrollView, StyleSheet } from "react-native";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import ContactsHeader from "@/features/contacts/components/ContactsHeader";
import CategoryList from "@/features/contacts/components/CategoryList";

export default function ContactsScreen() {
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];

  return (
    <View style={[styles.container, { backgroundColor: "#E8F5E9" }]}>
      <ContactsHeader />

      <View style={[styles.content, { backgroundColor: colors.backgroundSecondary }]}>
        <ScrollView
          contentContainerStyle={{ paddingBottom: 24 }}
          showsVerticalScrollIndicator={false}
        >
          <CategoryList />
        </ScrollView>
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

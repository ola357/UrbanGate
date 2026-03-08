import React, { memo } from "react";
import { View, Text, StyleProp, ViewStyle } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Phone } from "lucide-react-native";
import tw from "twrnc";

interface ContactsHeaderProps {
  style?: StyleProp<ViewStyle>;
}

const ContactsHeader: React.FC<ContactsHeaderProps> = memo(({ style }) => {
  const insets = useSafeAreaInsets();

  return (
    <View style={[{ backgroundColor: "#E8F5E9", paddingTop: insets.top + 30 }, style]}>
      <View style={tw`px-4 pb-4 flex-row items-center justify-between gap-8`}>
        <View style={tw`flex-1`}>
          <Text style={tw`text-black text-[24px] font-bold`}>Contacts</Text>
          <Text style={tw`text-gray-500 text-sm mt-1`}>
            Important numbers for estate support and emergencies
          </Text>
        </View>
        <View
          style={[
            tw`w-[85px] h-[135px] rounded-2xl items-center justify-center`,
            { backgroundColor: "#C8E6C9" },
          ]}
        >
          {/* <Phone size={28} color="#2E7D32" /> */}
        </View>
      </View>
      <View style={{ height: 3, backgroundColor: "#05C756" }} />
    </View>
  );
});

ContactsHeader.displayName = "ContactsHeader";
export default ContactsHeader;

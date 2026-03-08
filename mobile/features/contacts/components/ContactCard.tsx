import React, { memo } from "react";
import { View, Text, TouchableOpacity, Linking } from "react-native";
import { Phone, Mail, Contact as ContactIcon } from "lucide-react-native";
import tw from "twrnc";
import { useColorScheme } from "@/components/useColorScheme";
import { Contact } from "../types";

interface ContactCardProps {
  contact: Contact;
}

const ContactCard: React.FC<ContactCardProps> = memo(({ contact }) => {
  const colorScheme = useColorScheme() ?? "light";
  const cardBg = colorScheme === "dark" ? "#1A3A33" : "#E8F2ED";
  const textColor = colorScheme === "dark" ? "#FFFFFF" : "#1A1A1A";
  const subtextColor = colorScheme === "dark" ? "#A0A0A0" : "#4A4A4A";

  const handleCall = () => {
    Linking.openURL(`tel:${contact.phone.replace(/\s/g, "")}`);
  };

  const handleEmail = () => {
    Linking.openURL(`mailto:${contact.email}`);
  };

  return (
    <View style={[tw`mx-5 mb-4 rounded-2xl p-5`, { backgroundColor: cardBg }]}>
      {/* Contact badge icon top-right */}
      <View
        style={[
          tw`absolute top-4 right-4 w-16 h-8 rounded-lg items-center justify-center`,
          { backgroundColor: "#4CAF50" },
        ]}
      ></View>

      {/* Name */}
      <Text style={[tw`text-[17px] font-bold mb-4 pr-14`, { color: textColor }]}>
        {contact.name}
      </Text>

      {/* Phone */}
      <View style={tw`flex-row items-center mb-2.5`}>
        <Phone size={16} color={subtextColor} />
        <Text style={[tw`text-[15px] ml-3`, { color: subtextColor }]}>{contact.phone}</Text>
      </View>

      {/* Email */}
      <View style={tw`flex-row items-center mb-5`}>
        <Mail size={16} color={subtextColor} />
        <Text style={[tw`text-[15px] ml-3`, { color: subtextColor }]}>{contact.email}</Text>
      </View>

      {/* Action buttons */}
      <View style={tw`flex-row gap-3`}>
        <TouchableOpacity
          onPress={handleEmail}
          activeOpacity={0.8}
          style={[
            tw`flex-1 h-12 rounded-full items-center justify-center`,
            { backgroundColor: "#00483C" },
          ]}
        >
          <Text style={tw`text-white text-[15px] font-semibold`}>Send email</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={handleCall}
          activeOpacity={0.8}
          style={[
            tw`flex-1 h-12 rounded-full items-center justify-center`,
            { backgroundColor: "#00483C" },
          ]}
        >
          <Text style={tw`text-white text-[15px] font-semibold`}>Call</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
});

ContactCard.displayName = "ContactCard";
export default ContactCard;

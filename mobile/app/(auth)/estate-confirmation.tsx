import { Button } from "@/components/ui/Buttons";
import { useAuthStore } from "@/store/authStore";
import { router } from "expo-router";
import { Building2 } from "lucide-react-native";
import React from "react";
import { Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import tw from "twrnc";

const PRIMARY_DARK = "#00483C";

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <View style={tw`flex-row justify-between py-3 border-b border-white/15`}>
      <Text style={tw`text-sm text-white/60`}>{label}</Text>
      <Text style={tw`text-sm font-medium text-white`}>{value}</Text>
    </View>
  );
}

export default function EstateConfirmationScreen() {
  const user = useAuthStore((s) => s.user);
  const insets = useSafeAreaInsets();

  return (
    <View style={[tw`flex-1 px-6`, { backgroundColor: PRIMARY_DARK, paddingTop: insets.top + 40 }]}>
      <View style={tw`items-center mb-8`}>
        <View style={tw`w-20 h-20 rounded-full bg-white/10 items-center justify-center mb-6`}>
          <Building2 size={40} color="white" />
        </View>
        <Text style={tw`text-2xl font-bold text-white text-center mb-2`}>
          Welcome to {user?.estateName}
        </Text>
        <Text style={tw`text-base text-white/60 text-center`}>
          Kindly review the following information to continue
        </Text>
      </View>

      <View style={tw`bg-white/10 rounded-2xl px-4 mb-auto`}>
        <InfoRow label="First name" value={user?.firstName ?? ""} />
        <InfoRow label="Last name" value={user?.lastName ?? ""} />
        <InfoRow label="Phone number" value={user?.phone ?? ""} />
        <InfoRow label="Property Unit" value={user?.propertyUnit ?? ""} />
      </View>

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          onPress={() => router.push("/create-password")}
          style={{ backgroundColor: "#05C756" }}
        >
          Confirm
        </Button>
      </View>
    </View>
  );
}

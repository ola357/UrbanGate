import React, { useState } from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { router } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { Button } from "@/components/ui/Buttons";
import { Input } from "@/components/ui/Input";
import { useForgotPassword } from "@/features/auth/auth.hooks";

export default function ForgotPasswordScreen() {
  const [phone, setPhone] = useState("");
  const [error, setError] = useState("");

  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const mutation = useForgotPassword();

  const handleSubmit = () => {
    setError("");
    if (!phone.trim()) return;

    mutation.mutate(phone.trim(), {
      onSuccess: () => {
        router.push({
          pathname: "/reset-otp",
          params: { phone: phone.trim() },
        });
      },
      onError: (err) => {
        setError(err.message);
      },
    });
  };

  return (
    <View
      style={[tw`flex-1 px-6`, { backgroundColor: colors.background, paddingTop: insets.top + 16 }]}
    >
      <TouchableOpacity onPress={() => router.back()} style={tw`mb-6`}>
        <ChevronLeft size={28} color={colors.text} />
      </TouchableOpacity>

      <Text style={[tw`text-3xl font-bold mb-2`, { color: colors.text }]}>Forgot password</Text>
      <Text style={[tw`text-base mb-8`, { color: colors.textTertiary }]}>
        Enter your phone number and we&apos;ll send you a code
      </Text>

      {error ? <Text style={[tw`text-sm mb-4`, { color: colors.error }]}>{error}</Text> : null}

      <Input
        variant="underlined"
        label="Phone number"
        value={phone}
        onChangeText={(text) => {
          setPhone(text);
          if (error) setError("");
        }}
        placeholder="Enter your phone number"
        keyboardType="phone-pad"
      />

      <View style={tw`flex-1`} />

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          disabled={!phone.trim()}
          isLoading={mutation.isPending}
          onPress={handleSubmit}
        >
          Continue
        </Button>
      </View>
    </View>
  );
}

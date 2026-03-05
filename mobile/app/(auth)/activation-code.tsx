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
import { activationCodeSchema } from "@/features/auth/auth.schema";
import { useValidateActivationCode } from "@/features/auth/auth.hooks";
import { useAuthStore } from "@/store/authStore";

export default function ActivationCodeScreen() {
  const [code, setCode] = useState("");
  const [error, setError] = useState("");
  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const mutation = useValidateActivationCode();
  const setUser = useAuthStore((s) => s.setUser);

  const handleSubmit = () => {
    setError("");
    const result = activationCodeSchema.safeParse({ code });
    if (!result.success) {
      setError(result.error.issues[0].message);
      return;
    }

    mutation.mutate(code, {
      onSuccess: (userData) => {
        setUser(userData);
        router.push("/estate-confirmation");
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

      <Text style={[tw`text-3xl font-bold mb-2`, { color: colors.text }]}>
        Enter activation code
      </Text>
      <Text style={[tw`text-base mb-8`, { color: colors.textTertiary }]}>
        Enter the code provided by your estate management
      </Text>

      <Input
        variant="underlined"
        label="Activation code"
        value={code}
        onChangeText={(text) => {
          setCode(text);
          if (error) setError("");
        }}
        error={error}
        placeholder="e.g. PARKVIEW-00967"
        autoCapitalize="characters"
      />

      <View style={tw`flex-1`} />

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          disabled={!code.trim()}
          isLoading={mutation.isPending}
          onPress={handleSubmit}
        >
          Continue
        </Button>
      </View>
    </View>
  );
}

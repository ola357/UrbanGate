import { Button } from "@/components/ui/Buttons";
import { Input } from "@/components/ui/Input";
import { useColorScheme } from "@/components/useColorScheme";
import Colors from "@/constants/Colors";
import { useLogin } from "@/features/auth/auth.hooks";
import { useAuthStore } from "@/store/authStore";
import { useToastStore } from "@/store/toastStore";
import { router } from "expo-router";
import { ChevronLeft, Eye, EyeOff } from "lucide-react-native";
import React, { useState } from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import tw from "twrnc";

const PRIMARY_DARK = "#00483C";

export default function LoginScreen() {
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const mutation = useLogin();
  const login = useAuthStore((s) => s.login);
  const showToast = useToastStore((s) => s.show);

  const handleSubmit = () => {
    setError("");
    if (!phone.trim() || !password.trim()) return;

    mutation.mutate(
      { phone: phone.trim(), password },
      {
        onSuccess: ({ token, user }) => {
          login(token, user);
          showToast("\u{1F389} Ding! You're in");
        },
        onError: (err) => {
          setError(err.message);
        },
      },
    );
  };

  return (
    <View style={[tw`flex-1`, { backgroundColor: colors.background }]}>
      {/* Dark green header */}
      <View style={[tw`px-6 pb-6`, { backgroundColor: PRIMARY_DARK, paddingTop: insets.top + 16 }]}>
        <TouchableOpacity onPress={() => router.back()} style={tw`mb-4`}>
          <ChevronLeft size={28} color="white" />
        </TouchableOpacity>
        <Text style={tw`text-3xl font-bold text-white mb-1`}>Log in</Text>
        <Text style={tw`text-base text-white/60`}>Enter your credentials to continue</Text>
      </View>

      {/* Form */}
      <View style={tw`px-6 pt-8 flex-1`}>
        {error ? <Text style={[tw`text-sm mb-4`, { color: colors.error }]}>{error}</Text> : null}

        <View style={tw`gap-6`}>
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
          <Input
            variant="underlined"
            label="Password"
            value={password}
            onChangeText={(text) => {
              setPassword(text);
              if (error) setError("");
            }}
            secureTextEntry={!showPassword}
            placeholder="Enter your password"
            rightIcon={
              <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                {showPassword ? (
                  <EyeOff size={20} color={colors.textTertiary} />
                ) : (
                  <Eye size={20} color={colors.textTertiary} />
                )}
              </TouchableOpacity>
            }
          />
        </View>

        <TouchableOpacity style={tw`mt-4`} onPress={() => router.push("/forgot-password")}>
          <Text style={[tw`text-sm font-medium underline`, { color: PRIMARY_DARK }]}>
            Forgot password?
          </Text>
        </TouchableOpacity>
      </View>

      {/* Continue button */}
      <View style={[tw`px-6 pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          disabled={!phone.trim() || !password.trim()}
          isLoading={mutation.isPending}
          onPress={handleSubmit}
        >
          Continue
        </Button>
      </View>
    </View>
  );
}

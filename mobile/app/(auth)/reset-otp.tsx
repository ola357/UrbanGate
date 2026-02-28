import React, { useEffect, useState } from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { Button } from "@/components/ui/Buttons";
import { OtpInput } from "@/components/ui/OtpInput";
import { useVerifyResetOtp } from "@/features/auth/auth.hooks";
import { otpSchema } from "@/features/auth/auth.schema";

const RESEND_SECONDS = 60;

export default function ResetOtpScreen() {
  const { phone } = useLocalSearchParams<{ phone: string }>();
  const [code, setCode] = useState("");
  const [error, setError] = useState("");
  const [countdown, setCountdown] = useState(RESEND_SECONDS);

  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const mutation = useVerifyResetOtp();

  useEffect(() => {
    if (countdown <= 0) return;
    const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
    return () => clearTimeout(timer);
  }, [countdown]);

  const handleSubmit = () => {
    setError("");
    const result = otpSchema.safeParse(code);
    if (!result.success) {
      setError(result.error.issues[0].message);
      return;
    }

    mutation.mutate(
      { phone: phone ?? "", code },
      {
        onSuccess: ({ resetToken }) => {
          router.push({
            pathname: "./reset-password" as any,
            params: { resetToken },
          });
        },
        onError: (err) => {
          setError(err.message);
        },
      },
    );
  };

  const handleResend = () => {
    setCountdown(RESEND_SECONDS);
  };

  return (
    <View
      style={[
        tw`flex-1 px-6`,
        { backgroundColor: colors.background, paddingTop: insets.top + 16 },
      ]}
    >
      <TouchableOpacity onPress={() => router.back()} style={tw`mb-6`}>
        <ChevronLeft size={28} color={colors.text} />
      </TouchableOpacity>

      <Text style={[tw`text-3xl font-bold mb-2`, { color: colors.text }]}>
        Enter verification code
      </Text>
      <Text style={[tw`text-base mb-8`, { color: colors.textTertiary }]}>
        We sent a 6-digit code to {phone}
      </Text>

      {error ? (
        <Text style={[tw`text-sm mb-4`, { color: colors.error }]}>{error}</Text>
      ) : null}

      <OtpInput
        value={code}
        onChangeText={(text) => {
          setCode(text);
          if (error) setError("");
        }}
        error={error || undefined}
      />

      <View style={tw`mt-6 flex-row items-center`}>
        {countdown > 0 ? (
          <Text style={[tw`text-sm`, { color: colors.textTertiary }]}>
            Resend code in {countdown}s
          </Text>
        ) : (
          <TouchableOpacity onPress={handleResend}>
            <Text
              style={[tw`text-sm font-medium underline`, { color: colors.tintDark }]}
            >
              Resend code
            </Text>
          </TouchableOpacity>
        )}
      </View>

      <View style={tw`flex-1`} />

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          disabled={code.length < 6}
          isLoading={mutation.isPending}
          onPress={handleSubmit}
        >
          Continue
        </Button>
      </View>
    </View>
  );
}

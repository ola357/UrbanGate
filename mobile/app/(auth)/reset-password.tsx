import React, { useState } from "react";
import { Alert, Text, TouchableOpacity, View } from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { ChevronLeft, Eye, EyeOff } from "lucide-react-native";
import tw from "twrnc";
import Colors from "@/constants/Colors";
import { useColorScheme } from "@/components/useColorScheme";
import { Button } from "@/components/ui/Buttons";
import { Input } from "@/components/ui/Input";
import { passwordSchema } from "@/features/auth/auth.schema";
import { useResetPassword } from "@/features/auth/auth.hooks";

export default function ResetPasswordScreen() {
  const { resetToken } = useLocalSearchParams<{ resetToken: string }>();
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [errors, setErrors] = useState<{
    password?: string;
    confirmPassword?: string;
  }>({});

  const colorScheme = useColorScheme() ?? "light";
  const colors = Colors[colorScheme];
  const insets = useSafeAreaInsets();
  const mutation = useResetPassword();

  const handleSubmit = () => {
    setErrors({});
    const result = passwordSchema.safeParse({ password, confirmPassword });
    if (!result.success) {
      const fieldErrors: typeof errors = {};
      for (const issue of result.error.issues) {
        const field = issue.path[0] as keyof typeof errors;
        if (!fieldErrors[field]) {
          fieldErrors[field] = issue.message;
        }
      }
      setErrors(fieldErrors);
      return;
    }

    mutation.mutate(
      { resetToken: resetToken ?? "", password },
      {
        onSuccess: () => {
          Alert.alert(
            "Password reset",
            "Your password has been reset successfully. Please log in with your new password.",
            [
              {
                text: "OK",
                onPress: () => router.dismissTo("./login" as any),
              },
            ],
          );
        },
        onError: () => {
          setErrors({ password: "Something went wrong. Please try again." });
        },
      },
    );
  };

  const EyeToggle = (visible: boolean, toggle: () => void) => (
    <TouchableOpacity onPress={toggle}>
      {visible ? (
        <EyeOff size={20} color={colors.textTertiary} />
      ) : (
        <Eye size={20} color={colors.textTertiary} />
      )}
    </TouchableOpacity>
  );

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
        Reset password
      </Text>
      <Text style={[tw`text-base mb-8`, { color: colors.textTertiary }]}>
        Create a new secure password
      </Text>

      <View style={tw`gap-6`}>
        <Input
          variant="underlined"
          label="Password"
          value={password}
          onChangeText={(text) => {
            setPassword(text);
            if (errors.password)
              setErrors((e) => ({ ...e, password: undefined }));
          }}
          secureTextEntry={!showPassword}
          error={errors.password}
          rightIcon={EyeToggle(showPassword, () =>
            setShowPassword(!showPassword),
          )}
        />
        <Input
          variant="underlined"
          label="Confirm password"
          value={confirmPassword}
          onChangeText={(text) => {
            setConfirmPassword(text);
            if (errors.confirmPassword)
              setErrors((e) => ({ ...e, confirmPassword: undefined }));
          }}
          secureTextEntry={!showConfirm}
          error={errors.confirmPassword}
          rightIcon={EyeToggle(showConfirm, () => setShowConfirm(!showConfirm))}
        />
      </View>

      <View style={tw`flex-1`} />

      <View style={[tw`pb-4`, { paddingBottom: insets.bottom + 16 }]}>
        <Button
          fullWidth
          disabled={!password || !confirmPassword}
          isLoading={mutation.isPending}
          onPress={handleSubmit}
        >
          Continue
        </Button>
      </View>
    </View>
  );
}

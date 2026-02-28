import { useMutation } from "@tanstack/react-query";
import { authApi } from "@/services/api";

export const useValidateActivationCode = () => {
  return useMutation({
    mutationFn: (code: string) => authApi.validateActivationCode(code),
  });
};

export const useLogin = () => {
  return useMutation({
    mutationFn: ({ phone, password }: { phone: string; password: string }) =>
      authApi.login(phone, password),
  });
};

export const useCreatePassword = () => {
  return useMutation({
    mutationFn: (password: string) => authApi.createPassword(password),
  });
};

export const useForgotPassword = () => {
  return useMutation({
    mutationFn: (phone: string) => authApi.forgotPassword(phone),
  });
};

export const useVerifyResetOtp = () => {
  return useMutation({
    mutationFn: ({ phone, code }: { phone: string; code: string }) =>
      authApi.verifyResetOtp(phone, code),
  });
};

export const useResetPassword = () => {
  return useMutation({
    mutationFn: ({
      resetToken,
      password,
    }: {
      resetToken: string;
      password: string;
    }) => authApi.resetPassword(resetToken, password),
  });
};

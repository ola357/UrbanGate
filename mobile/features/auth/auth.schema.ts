import { z } from "zod/v4";

export const activationCodeSchema = z.object({
  code: z.string().min(1, "Activation code is required"),
});

export const passwordSchema = z
  .object({
    password: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .regex(/[A-Z]/, "Password must contain an uppercase letter")
      .regex(/[a-z]/, "Password must contain a lowercase letter")
      .regex(/[0-9]/, "Password must contain a number"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const otpSchema = z
  .string()
  .length(6, "Code must be 6 digits")
  .regex(/^\d{6}$/, "Code must contain only digits");

export type ActivationCodeInput = z.infer<typeof activationCodeSchema>;
export type PasswordInput = z.infer<typeof passwordSchema>;

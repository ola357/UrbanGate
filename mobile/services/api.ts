import type { UserData } from "./storage";

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export const authApi = {
  validateActivationCode: async (code: string): Promise<UserData> => {
    await delay(1000);

    if (code !== "PARKVIEW-00967") {
      throw new Error("Invalid activation code");
    }

    return {
      firstName: "Joshua",
      lastName: "Inyang",
      phone: "+234 812 345 6789",
      propertyUnit: "Block A, Unit 12",
      estateName: "Parkview Estate",
    };
  },

  login: async (phone: string, password: string): Promise<{ token: string; user: UserData }> => {
    await delay(1000);

    if (phone === "123456789" && password === "Demo@123.") {
      return {
        token: "dummy-session-token-login-456",
        user: {
          firstName: "Joshua",
          lastName: "Inyang",
          phone: "+234 806 447 8829",
          propertyUnit: "Block A, Unit 12",
          estateName: "Parkview Estate",
        },
      };
    }

    throw new Error("Invalid phone number or password");
  },

  createPassword: async (_password: string): Promise<string> => {
    await delay(1000);
    return "dummy-session-token-abc123";
  },

  forgotPassword: async (_phone: string): Promise<{ success: true }> => {
    await delay(1000);
    return { success: true };
  },

  verifyResetOtp: async (_phone: string, code: string): Promise<{ resetToken: string }> => {
    await delay(1000);
    if (code !== "123456") {
      throw new Error("Invalid verification code");
    }
    return { resetToken: "dummy-reset-token-789" };
  },

  resetPassword: async (_resetToken: string, _password: string): Promise<{ success: true }> => {
    await delay(1000);
    return { success: true };
  },
};

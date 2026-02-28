import { create } from "zustand";
import {
  clearAll,
  getSessionToken,
  getUserData,
  setSessionToken,
  setUserData,
  type UserData,
} from "@/services/storage";

export type ResidentType = "existing" | "new";

interface AuthState {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: UserData | null;
  sessionToken: string | null;
  residentType: ResidentType | null;

  initialize: () => void;
  setResidentType: (type: ResidentType) => void;
  setUser: (user: UserData) => void;
  login: (token: string, user: UserData) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  isLoading: true,
  user: null,
  sessionToken: null,
  residentType: null,

  initialize: () => {
    const token = getSessionToken();
    const user = getUserData();

    if (token && user) {
      set({ isAuthenticated: true, sessionToken: token, user, isLoading: false });
    } else {
      set({ isLoading: false });
    }
  },

  setResidentType: (type) => set({ residentType: type }),

  setUser: (user) => {
    setUserData(user);
    set({ user });
  },

  login: (token, user) => {
    setSessionToken(token);
    setUserData(user);
    set({ isAuthenticated: true, sessionToken: token, user });
  },

  logout: () => {
    clearAll();
    set({
      isAuthenticated: false,
      sessionToken: null,
      user: null,
      residentType: null,
    });
  },
}));

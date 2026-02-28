import { createMMKV } from "react-native-mmkv";

export const storage = createMMKV();

export const StorageKeys = {
  SESSION_TOKEN: "session_token",
  USER_DATA: "user_data",
} as const;

export const getSessionToken = (): string | undefined => {
  return storage.getString(StorageKeys.SESSION_TOKEN);
};

export const setSessionToken = (token: string) => {
  storage.set(StorageKeys.SESSION_TOKEN, token);
};

export const getUserData = (): UserData | undefined => {
  const raw = storage.getString(StorageKeys.USER_DATA);
  if (!raw) return undefined;
  return JSON.parse(raw) as UserData;
};

export const setUserData = (data: UserData) => {
  storage.set(StorageKeys.USER_DATA, JSON.stringify(data));
};

export const clearAll = () => {
  storage.remove(StorageKeys.SESSION_TOKEN);
  storage.remove(StorageKeys.USER_DATA);
};

export interface UserData {
  firstName: string;
  lastName: string;
  phone: string;
  propertyUnit: string;
  estateName: string;
}

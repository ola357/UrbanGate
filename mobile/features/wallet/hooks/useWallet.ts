import { useQuery } from "@tanstack/react-query";
import {
  mockBalance,
  mockTransactions,
  mockAccountDetails,
} from "../data/mockWallet";

export function useWalletBalance() {
  return useQuery({
    queryKey: ["wallet", "balance"],
    queryFn: async () => mockBalance,
  });
}

export function useWalletHistory() {
  return useQuery({
    queryKey: ["wallet", "history"],
    queryFn: async () => mockTransactions,
  });
}

export function useAccountDetails() {
  return useQuery({
    queryKey: ["wallet", "accountDetails"],
    queryFn: async () => mockAccountDetails,
  });
}

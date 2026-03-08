import {
  Recycle,
  Shield,
  Droplet,
  Wallet,
  LucideIcon,
} from "lucide-react-native";

export type TransactionType = "debit" | "credit";
export type TransactionCategory = "waste" | "security" | "water" | "funding";

export interface WalletTransaction {
  id: string;
  title: string;
  category: TransactionCategory;
  amount: number;
  type: TransactionType;
  date: string;
}

export interface WalletBalance {
  amount: number;
  currency: string;
}

export interface AccountDetails {
  accountHolder: string;
  accountNumber: string;
  bankName: string;
}

export interface TransactionCategoryConfig {
  icon: LucideIcon;
  color: string;
}

export const TRANSACTION_CATEGORIES: Record<
  TransactionCategory,
  TransactionCategoryConfig
> = {
  waste: { icon: Recycle, color: "#FF9500" },
  security: { icon: Shield, color: "#1A237E" },
  water: { icon: Droplet, color: "#2196F3" },
  funding: { icon: Wallet, color: "#05C756" },
};

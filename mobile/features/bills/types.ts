import { Recycle, Shield, Droplet, LucideIcon } from "lucide-react-native";

export type BillStatus = "due" | "current" | "upcoming";
export type BillCategoryKey = "waste" | "security" | "water";
export type PaymentMethod = "wallet" | "transfer";

export interface Bill {
  id: string;
  name: string;
  category: BillCategoryKey;
  amount: number;
  dueDate: string;
  status: BillStatus;
}

export interface BillCategoryConfig {
  icon: LucideIcon;
  color: string;
  letter: string;
}

export const BILL_CATEGORIES: Record<BillCategoryKey, BillCategoryConfig> = {
  waste: { icon: Recycle, color: "#FF9500", letter: "W" },
  security: { icon: Shield, color: "#1A237E", letter: "S" },
  water: { icon: Droplet, color: "#2196F3", letter: "D" },
};

export const BILL_TABS: { key: BillStatus; label: string }[] = [
  { key: "due", label: "Due" },
  { key: "current", label: "Current" },
  { key: "upcoming", label: "Upcoming" },
];

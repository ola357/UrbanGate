import type { LucideIcon } from "lucide-react-native";
import { Building2, Store, ShieldAlert, Shield } from "lucide-react-native";

export type ContactCategoryKey =
  | "facility-management"
  | "vendors"
  | "emergency"
  | "security";

export interface Contact {
  id: string;
  name: string;
  phone: string;
  email: string;
  category: ContactCategoryKey;
}

export interface ContactCategoryConfig {
  key: ContactCategoryKey;
  title: string;
  subtitle: string;
  icon: LucideIcon;
}

export const CONTACT_CATEGORIES: ContactCategoryConfig[] = [
  {
    key: "facility-management",
    title: "Facility management",
    subtitle: "Building & maintenance contacts",
    icon: Building2,
  },
  {
    key: "vendors",
    title: "Vendors",
    subtitle: "Service providers & suppliers",
    icon: Store,
  },
  {
    key: "emergency",
    title: "Emergency",
    subtitle: "Emergency response contacts",
    icon: ShieldAlert,
  },
  {
    key: "security",
    title: "Security",
    subtitle: "Estate security personnel",
    icon: Shield,
  },
];

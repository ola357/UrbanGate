import { WalletTransaction, WalletBalance, AccountDetails } from "../types";

export const mockBalance: WalletBalance = {
  amount: 89050,
  currency: "NGN",
};

export const mockTransactions: WalletTransaction[] = [
  {
    id: "1",
    title: "Waste bill",
    category: "waste",
    amount: 5000,
    type: "debit",
    date: "Jan 20, 2025",
  },
  {
    id: "2",
    title: "Account funded",
    category: "funding",
    amount: 50000,
    type: "credit",
    date: "Jan 18, 2025",
  },
  {
    id: "3",
    title: "Security Levy",
    category: "security",
    amount: 10000,
    type: "debit",
    date: "Jan 15, 2025",
  },
  {
    id: "4",
    title: "Water bill",
    category: "water",
    amount: 3500,
    type: "debit",
    date: "Jan 10, 2025",
  },
  {
    id: "5",
    title: "Waste bill",
    category: "waste",
    amount: 5000,
    type: "debit",
    date: "Jan 05, 2025",
  },
];

export const mockAccountDetails: AccountDetails = {
  accountHolder: "Amaka Hazel",
  accountNumber: "1234 5678 90",
  bankName: "Wema Bank",
};

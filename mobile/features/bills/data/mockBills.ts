import { Bill } from "../types";

export const mockBills: Bill[] = [
  {
    id: "1",
    name: "Waste bill",
    category: "waste",
    amount: 5000,
    dueDate: "Jan 20, 2025",
    status: "due",
  },
  {
    id: "2",
    name: "Security levy",
    category: "security",
    amount: 10000,
    dueDate: "Jan 25, 2025",
    status: "due",
  },
  {
    id: "3",
    name: "Water bill",
    category: "water",
    amount: 3500,
    dueDate: "Feb 01, 2025",
    status: "due",
  },
  {
    id: "4",
    name: "Waste bill",
    category: "waste",
    amount: 5000,
    dueDate: "Feb 15, 2025",
    status: "current",
  },
  {
    id: "5",
    name: "Security levy",
    category: "security",
    amount: 10000,
    dueDate: "Mar 01, 2025",
    status: "current",
  },
  {
    id: "6",
    name: "Water bill",
    category: "water",
    amount: 3500,
    dueDate: "Mar 20, 2025",
    status: "upcoming",
  },
];

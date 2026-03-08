import { useQuery } from "@tanstack/react-query";
import { mockContacts } from "../data/mockContacts";
import { Contact, ContactCategoryKey } from "../types";

export const contactKeys = {
  all: ["contacts"] as const,
  lists: () => [...contactKeys.all, "list"] as const,
  byCategory: (category: ContactCategoryKey) =>
    [...contactKeys.all, "category", category] as const,
};

export function useContactsByCategory(category: ContactCategoryKey) {
  return useQuery({
    queryKey: contactKeys.byCategory(category),
    queryFn: async (): Promise<Contact[]> =>
      mockContacts.filter((c) => c.category === category),
    enabled: !!category,
  });
}

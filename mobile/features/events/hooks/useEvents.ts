import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { mockEvents } from "../data/mockEvents";
import { EstateEvent, EventFormData } from "../types";

export const eventKeys = {
  all: ["events"] as const,
  lists: () => [...eventKeys.all, "list"] as const,
  detail: (id: string) => [...eventKeys.all, "detail", id] as const,
};

export function useEvents() {
  return useQuery({
    queryKey: eventKeys.lists(),
    queryFn: async (): Promise<EstateEvent[]> => mockEvents,
  });
}

export function useEventDetail(id: string) {
  return useQuery({
    queryKey: eventKeys.detail(id),
    queryFn: async (): Promise<EstateEvent | undefined> =>
      mockEvents.find((e) => e.id === id),
    enabled: !!id,
  });
}

function generateCode(): string {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let code = "";
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return code;
}

export function useCreateEvent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (form: EventFormData): Promise<EstateEvent> => {
      const date = form.date ?? new Date();
      const validFrom = new Date(date);
      validFrom.setHours(10, 0, 0, 0);

      const durationHours = parseInt(form.duration, 10) || 2;
      const validUntil = new Date(validFrom);
      validUntil.setHours(validFrom.getHours() + durationHours);

      const event: EstateEvent = {
        id: Date.now().toString(),
        name: form.name,
        date: date.toISOString(),
        expectedGuests: parseInt(form.expectedGuests, 10) || 0,
        location: form.location,
        duration: form.duration,
        description: form.description,
        code: generateCode(),
        status: "active",
        validFrom: validFrom.toISOString(),
        validUntil: validUntil.toISOString(),
      };

      return event;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.lists() });
    },
  });
}

export type EventStatus = "active" | "expired" | "cancelled";

export interface EstateEvent {
  id: string;
  name: string;
  date: string; // ISO date
  expectedGuests: number;
  location: string;
  duration: string;
  description: string;
  code: string;
  status: EventStatus;
  validFrom: string; // ISO datetime
  validUntil: string; // ISO datetime
}

export interface EventFormData {
  name: string;
  date: Date | null;
  expectedGuests: string;
  location: string;
  duration: string;
  description: string;
}

export type EventFilter = "all" | "active" | "expired";

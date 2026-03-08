import { EstateEvent } from "../types";

export const mockEvents: EstateEvent[] = [
  {
    id: "1",
    name: "Thanksgiving",
    date: "2025-02-12T00:00:00.000Z",
    expectedGuests: 22,
    location: "Hall",
    duration: "2 hours",
    description: "Guest",
    code: "841AB9",
    status: "active",
    validFrom: "2025-02-12T10:00:00.000Z",
    validUntil: "2025-02-12T12:00:00.000Z",
  },
];

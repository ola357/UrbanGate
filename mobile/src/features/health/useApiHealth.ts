import { useQuery } from "@tanstack/react-query";
import { fetchApiHealth } from "./healthApi";

export function useApiHealth() {
  return useQuery({
    queryKey: ["apiHealth"],
    queryFn: fetchApiHealth,
    retry: 1,
    staleTime: 10_000,
  });
}

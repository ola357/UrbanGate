import { useQuery } from "@tanstack/react-query";
import { getVersion } from "@/features/version/api/getVersion";

export function useVersion() {
  return useQuery({
    queryKey: ["version"],
    queryFn: getVersion,
  });
}

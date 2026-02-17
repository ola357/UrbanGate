import { httpGet } from "@/core/http/httpClient";

export type ApiHealth = {
  ok: boolean;
  version?: {
    service: string;
    version: string;
    time: string;
  };
  error?: string;
};

/**
 * For now, we treat /api/v1/version as a lightweight "health" probe.
 * Later: replace with a real /actuator/health (or /api/v1/health) contract.
 */
export async function fetchApiHealth(): Promise<ApiHealth> {
  const v = await httpGet<{ service: string; version: string; time: string }>("/api/v1/version");
  return { ok: true, version: v };
}

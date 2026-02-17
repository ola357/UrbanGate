import { getJson } from "@/core/http/httpClient";

export type VersionDto = {
  service: string;
  version: string;
  time: string;
};

export function getVersion(): Promise<VersionDto> {
  return getJson<VersionDto>("/api/v1/version");
}

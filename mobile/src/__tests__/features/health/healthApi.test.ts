import { fetchApiHealth } from "@/features/health/healthApi";

const mockGetJson = jest.fn();

jest.mock("@/core/http/httpClient", () => ({
  getJson: (...args: unknown[]) => mockGetJson(...args),
}));

describe("fetchApiHealth", () => {
  beforeEach(() => {
    mockGetJson.mockReset();
  });

  it("wraps version response as ok", async () => {
    mockGetJson.mockResolvedValue({
      service: "urbangate",
      version: "1.0.0",
      time: "now",
    });

    const result = await fetchApiHealth();

    expect(mockGetJson).toHaveBeenCalledWith("/api/v1/version");
    expect(result).toEqual({
      ok: true,
      version: { service: "urbangate", version: "1.0.0", time: "now" },
    });
  });
});

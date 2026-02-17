import { getVersion } from "@/features/version/api/getVersion";

const mockGetJson = jest.fn();

jest.mock("@/core/http/httpClient", () => ({
  getJson: (...args: unknown[]) => mockGetJson(...args),
}));

describe("getVersion", () => {
  beforeEach(() => {
    mockGetJson.mockReset();
  });

  it("requests the version endpoint", async () => {
    mockGetJson.mockResolvedValue({ service: "urbangate", version: "1", time: "now" });

    const result = await getVersion();

    expect(mockGetJson).toHaveBeenCalledWith("/api/v1/version");
    expect(result).toEqual({ service: "urbangate", version: "1", time: "now" });
  });
});

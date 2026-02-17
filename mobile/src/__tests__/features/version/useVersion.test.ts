import { useVersion } from "@/features/version/hooks/useVersion";
import { getVersion } from "@/features/version/api/getVersion";

const mockUseQuery = jest.fn();

jest.mock("@tanstack/react-query", () => ({
  useQuery: (config: unknown) => mockUseQuery(config),
}));

jest.mock("@/features/version/api/getVersion", () => ({
  getVersion: jest.fn(),
}));

describe("useVersion", () => {
  beforeEach(() => {
    mockUseQuery.mockReset();
  });

  it("configures react-query correctly", () => {
    const fakeResult = { data: { service: "urbangate" } };
    mockUseQuery.mockReturnValue(fakeResult);

    const result = useVersion();

    expect(mockUseQuery).toHaveBeenCalledWith({
      queryKey: ["version"],
      queryFn: getVersion,
    });
    expect(result).toBe(fakeResult);
  });
});

import { useApiHealth } from "@/features/health/useApiHealth";
import { fetchApiHealth } from "@/features/health/healthApi";

const mockUseQuery = jest.fn();

jest.mock("@tanstack/react-query", () => ({
  useQuery: (config: unknown) => mockUseQuery(config),
}));

jest.mock("@/features/health/healthApi", () => ({
  fetchApiHealth: jest.fn(),
}));

describe("useApiHealth", () => {
  beforeEach(() => {
    mockUseQuery.mockReset();
  });

  it("configures react-query correctly", () => {
    const fakeResult = { data: { ok: true } };
    mockUseQuery.mockReturnValue(fakeResult);

    const result = useApiHealth();

    expect(mockUseQuery).toHaveBeenCalledWith({
      queryKey: ["apiHealth"],
      queryFn: fetchApiHealth,
      retry: 1,
      staleTime: 10_000,
    });
    expect(result).toBe(fakeResult);
  });
});

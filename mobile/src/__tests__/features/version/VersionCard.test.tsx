import React from "react";
import { fireEvent, render } from "@testing-library/react-native";
import { VersionCard } from "@/features/version/components/VersionCard";

const mockUseVersion = jest.fn();

jest.mock("@/features/version/hooks/useVersion", () => ({
  useVersion: () => mockUseVersion(),
}));

describe("VersionCard", () => {
  beforeEach(() => {
    mockUseVersion.mockReset();
  });

  it("renders loading state", () => {
    mockUseVersion.mockReturnValue({
      data: null,
      isLoading: true,
      isRefetching: false,
      error: null,
      refetch: jest.fn(),
    });

    const { getByText } = render(<VersionCard />);
    expect(getByText("Backend Version")).toBeTruthy();
  });

  it("renders error state and retries", () => {
    const refetch = jest.fn();
    mockUseVersion.mockReturnValue({
      data: null,
      isLoading: false,
      isRefetching: false,
      error: new Error("boom"),
      refetch,
    });

    const { getByText } = render(<VersionCard />);
    expect(getByText(/Could not fetch/)).toBeTruthy();

    fireEvent.press(getByText("Tap to retry"));
    expect(refetch).toHaveBeenCalledTimes(1);
  });

  it("renders retrying label when refetching", () => {
    mockUseVersion.mockReturnValue({
      data: null,
      isLoading: false,
      isRefetching: true,
      error: new Error("boom"),
      refetch: jest.fn(),
    });

    const { getByText } = render(<VersionCard />);
    expect(getByText("Retrying...")).toBeTruthy();
  });

  it("renders version data", () => {
    mockUseVersion.mockReturnValue({
      data: { service: "urbangate", version: "1.2.3", time: "now" },
      isLoading: false,
      isRefetching: false,
      error: null,
      refetch: jest.fn(),
    });

    const { getByText } = render(<VersionCard />);
    expect(getByText("service: urbangate")).toBeTruthy();
    expect(getByText("version: 1.2.3")).toBeTruthy();
    expect(getByText("time: now")).toBeTruthy();
  });
});

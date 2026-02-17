import React from "react";
import { fireEvent, render } from "@testing-library/react-native";
import HomeScreen from "@/screens/HomeScreen";

const mockUseVersion = jest.fn();

jest.mock("expo-router", () => ({
  Link: ({ children }: { children: React.ReactNode }) => children,
}));

jest.mock("@/features/version/hooks/useVersion", () => ({
  useVersion: () => mockUseVersion(),
}));

describe("HomeScreen", () => {
  beforeEach(() => {
    mockUseVersion.mockReset();
  });

  it("renders version info and refresh action", () => {
    const refetch = jest.fn();
    mockUseVersion.mockReturnValue({
      data: { service: "urbangate-backend", version: "0.0.1", time: "now" },
      isLoading: false,
      isFetching: false,
      error: null,
      refetch,
    });

    const { getByText } = render(<HomeScreen />);
    expect(getByText("urbangate-backend")).toBeTruthy();

    fireEvent.press(getByText("Refresh"));
    expect(refetch).toHaveBeenCalledTimes(1);
  });

  it("renders loading state", () => {
    mockUseVersion.mockReturnValue({
      data: null,
      isLoading: true,
      isFetching: false,
      error: null,
      refetch: jest.fn(),
    });

    const { getByText } = render(<HomeScreen />);
    expect(getByText(/Loading version/)).toBeTruthy();
  });

  it("renders error state and retries", () => {
    const refetch = jest.fn();
    mockUseVersion.mockReturnValue({
      data: null,
      isLoading: false,
      isFetching: false,
      error: new Error("Network down"),
      refetch,
    });

    const { getByText } = render(<HomeScreen />);
    expect(getByText(/reach the API/i)).toBeTruthy();

    fireEvent.press(getByText("Retry"));
    expect(refetch).toHaveBeenCalledTimes(1);
  });
});

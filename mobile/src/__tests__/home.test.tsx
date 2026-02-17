import React from "react";
import { render } from "@testing-library/react-native";
import HomeScreen from "@/screens/HomeScreen";

jest.mock("@/features/version/useVersion", () => ({
  useVersion: () => ({
    data: { service: "urbangate-backend", version: "0.0.1", time: "now" },
    isLoading: false,
    isFetching: false,
    error: null,
    refetch: jest.fn(),
  }),
}));

describe("HomeScreen", () => {
  it("renders version info", () => {
    const { getByText } = render(<HomeScreen />);
    expect(getByText("urbangate-backend")).toBeTruthy();
  });
});

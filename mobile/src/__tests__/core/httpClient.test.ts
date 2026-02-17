import { getJson, HttpError } from "@/core/http/httpClient";

jest.mock("@/config/env", () => ({
  env: { apiBaseUrl: "http://api.test" },
}));

describe("httpClient.getJson", () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  it("returns parsed JSON body", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue(JSON.stringify({ ok: true })),
    });

    const result = await getJson<{ ok: boolean }>("/status");

    expect(global.fetch).toHaveBeenCalledWith(
      "http://api.test/status",
      expect.objectContaining({ method: "GET" }),
    );
    expect(result).toEqual({ ok: true });
  });

  it("throws HttpError on non-OK response", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      status: 500,
      text: jest.fn().mockResolvedValue(JSON.stringify({ error: "boom" })),
    });

    await expect(getJson("/status")).rejects.toBeInstanceOf(HttpError);
    await expect(getJson("/status")).rejects.toMatchObject({
      status: 500,
      body: { error: "boom" },
    });
  });

  it("returns raw text when JSON parsing fails", async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200,
      text: jest.fn().mockResolvedValue("plain-text"),
    });

    const result = await getJson<string>("/plain");
    expect(result).toBe("plain-text");
  });
});

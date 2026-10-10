import { afterEach, describe, expect, it, vi } from "vitest";
import { createBorrow, listBooks, listReaders, returnBorrow } from "./api";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("library API", () => {
  it("encodes a book search and returns results", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({
        ok: true,
        status: 200,
        json: async () => [{ id: 1, title: "Java & Web" }],
      });
    vi.stubGlobal("fetch", fetchMock);

    await expect(listBooks("Java & Web")).resolves.toEqual([
      { id: 1, title: "Java & Web" },
    ]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/books?title=Java+%26+Web&page=1&size=100",
      expect.any(Object),
    );
  });

  it("surfaces the backend message for an invalid borrow", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockResolvedValue({
          ok: false,
          status: 409,
          json: async () => ({ message: "Book is out of stock: 2" }),
        }),
    );
    await expect(createBorrow(2, 1)).rejects.toThrow("Book is out of stock: 2");
  });

  it("sends borrow and return requests using the existing routes", async () => {
    const fetchMock = vi
      .fn()
      .mockResolvedValue({
        ok: true,
        status: 200,
        json: async () => ({ id: 7, status: "RETURNED" }),
      });
    vi.stubGlobal("fetch", fetchMock);
    await createBorrow(2, 3);
    await returnBorrow(7);
    expect(fetchMock.mock.calls[0][0]).toBe("/api/borrow-records");
    expect(fetchMock.mock.calls[0][1]).toMatchObject({
      method: "POST",
      body: JSON.stringify({ bookId: 2, readerId: 3 }),
    });
    expect(fetchMock.mock.calls[1][0]).toBe("/api/borrow-records/7/return");
    expect(fetchMock.mock.calls[1][1]).toMatchObject({ method: "PUT" });
  });

  it("loads readers from the collection endpoint", async () => {
    vi.stubGlobal(
      "fetch",
      vi
        .fn()
        .mockResolvedValue({
          ok: true,
          status: 200,
          json: async () => [{ id: 3, name: "张三" }],
        }),
    );
    await expect(listReaders()).resolves.toEqual([{ id: 3, name: "张三" }]);
  });
});

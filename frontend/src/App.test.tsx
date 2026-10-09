import { afterEach, expect, it, vi } from "vitest";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import App from "./App";

afterEach(() => {
  cleanup();
  vi.unstubAllGlobals();
});

it("borrows and returns a book from the circulation view", async () => {
  let returned = false;
  let borrowed = false;
  const calls: string[] = [];
  vi.stubGlobal(
    "fetch",
    vi.fn(async (input: string, init?: RequestInit) => {
      calls.push(`${init?.method || "GET"} ${input}`);
      if (input === "/api/books?title=&page=1&size=100")
        return json([
          {
            id: 1,
            isbn: "B-1",
            title: "海边的卡夫卡",
            author: "村上春树",
            publisher: "上海译文",
            totalStock: 2,
            availableStock: borrowed && !returned ? 1 : 2,
          },
        ]);
      if (input === "/api/readers")
        return json([{ id: 2, readerNo: "R-2", name: "林同学", phone: "" }]);
      if (input === "/api/borrow-records" && init?.method === "POST") {
        borrowed = true;
        return json({
          id: 9,
          bookId: 1,
          readerId: 2,
          borrowedAt: "2026-10-09T10:00:00",
          returnedAt: null,
          status: "BORROWED",
        });
      }
      if (input === "/api/borrow-records/9/return") {
        returned = true;
        return json({
          id: 9,
          bookId: 1,
          readerId: 2,
          borrowedAt: "2026-10-09T10:00:00",
          returnedAt: "2026-10-09T11:00:00",
          status: "RETURNED",
        });
      }
      if (input === "/api/borrow-records")
        return json(
          borrowed
            ? [
                {
                  id: 9,
                  bookId: 1,
                  readerId: 2,
                  borrowedAt: "2026-10-09T10:00:00",
                  returnedAt: returned ? "2026-10-09T11:00:00" : null,
                  status: returned ? "RETURNED" : "BORROWED",
                },
              ]
            : [],
        );
      throw new Error(`Unexpected request: ${input}`);
    }),
  );

  const user = userEvent.setup();
  render(<App />);
  await user.click(screen.getByRole("button", { name: "借阅管理" }));
  await screen.findByRole("option", { name: /海边的卡夫卡/ });
  await user.selectOptions(screen.getByLabelText("图书"), "1");
  await user.selectOptions(screen.getByLabelText("读者"), "2");
  await user.click(screen.getByRole("button", { name: "确认借出" }));
  await screen.findByText("借阅 #9");
  await user.click(screen.getByRole("button", { name: "归还" }));
  await waitFor(() => expect(screen.getByText("已归还")).toBeTruthy());
  expect(calls).toContain("POST /api/borrow-records");
  expect(calls).toContain("PUT /api/borrow-records/9/return");
});

it("keeps the reader form open when the server rejects it", async () => {
  vi.stubGlobal(
    "fetch",
    vi.fn(async (input: string, init?: RequestInit) => {
      if (input.startsWith("/api/books?")) return json([]);
      if (input === "/api/readers" && init?.method === "POST")
        return {
          ok: false,
          status: 409,
          json: async () => ({ message: "Reader number already exists" }),
        };
      if (input === "/api/readers" || input === "/api/borrow-records")
        return json([]);
      throw new Error(`Unexpected request: ${input}`);
    }),
  );
  const user = userEvent.setup();
  render(<App />);
  await user.click(screen.getByRole("button", { name: "读者管理" }));
  await screen.findByRole("button", { name: /登记读者/ });
  await user.click(screen.getByRole("button", { name: /登记读者/ }));
  await user.type(screen.getByLabelText("读者编号"), "R-1");
  await user.type(screen.getByLabelText("姓名"), "林同学");
  await user.click(screen.getByRole("button", { name: "保存读者" }));
  await screen.findByRole("alert");
  expect(screen.getByLabelText("读者编号")).toBeTruthy();
});

function json(value: unknown) {
  return { ok: true, status: 200, json: async () => value };
}

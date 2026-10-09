import type {
  Book,
  BookInput,
  BorrowRecord,
  Reader,
  ReaderInput,
} from "./types";

async function request<T>(
  path: string,
  method = "GET",
  body?: unknown,
): Promise<T> {
  const response = await fetch(path, {
    method,
    headers:
      body === undefined ? undefined : { "Content-Type": "application/json" },
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (!response.ok) {
    const details = (await response.json().catch(() => null)) as {
      message?: string;
    } | null;
    throw new Error(details?.message || `请求失败（${response.status}）`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export function listBooks(title = ""): Promise<Book[]> {
  const query = new URLSearchParams({ title, page: "1", size: "100" });
  return request(`/api/books?${query}`);
}

export function createBook(book: BookInput): Promise<Book> {
  return request("/api/books", "POST", book);
}

export function updateBook(id: number, book: BookInput): Promise<Book> {
  return request(`/api/books/${id}`, "PUT", book);
}

export function deleteBook(id: number): Promise<void> {
  return request(`/api/books/${id}`, "DELETE");
}

export function listReaders(): Promise<Reader[]> {
  return request("/api/readers");
}

export function createReader(reader: ReaderInput): Promise<Reader> {
  return request("/api/readers", "POST", reader);
}

export function listBorrowRecords(): Promise<BorrowRecord[]> {
  return request("/api/borrow-records");
}

export function createBorrow(
  bookId: number,
  readerId: number,
): Promise<BorrowRecord> {
  return request("/api/borrow-records", "POST", { bookId, readerId });
}

export function returnBorrow(id: number): Promise<BorrowRecord> {
  return request(`/api/borrow-records/${id}/return`, "PUT");
}

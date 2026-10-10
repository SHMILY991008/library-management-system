export type Book = {
  id: number;
  isbn: string;
  title: string;
  author: string;
  publisher: string | null;
  totalStock: number;
  availableStock: number;
};

export type Reader = {
  id: number;
  readerNo: string;
  name: string;
  phone: string | null;
};

export type BorrowRecord = {
  id: number;
  bookId: number;
  readerId: number;
  borrowedAt: string;
  returnedAt: string | null;
  status: "BORROWED" | "RETURNED";
};

export type BookInput = Omit<Book, "id">;
export type ReaderInput = Omit<Reader, "id">;

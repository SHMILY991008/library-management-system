import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type FormEvent,
} from "react";
import {
  createBook,
  createBorrow,
  createReader,
  deleteBook,
  listBooks,
  listBorrowRecords,
  listReaders,
  returnBorrow,
  updateBook,
} from "./api";
import type {
  Book,
  BookInput,
  BorrowRecord,
  Reader,
  ReaderInput,
} from "./types";

type Section = "books" | "readers" | "borrows";
const emptyBook: BookInput = {
  isbn: "",
  title: "",
  author: "",
  publisher: "",
  totalStock: 1,
  availableStock: 1,
};
const emptyReader: ReaderInput = { readerNo: "", name: "", phone: "" };

export default function App() {
  const [section, setSection] = useState<Section>("books");
  const [books, setBooks] = useState<Book[]>([]);
  const [readers, setReaders] = useState<Reader[]>([]);
  const [records, setRecords] = useState<BorrowRecord[]>([]);
  const [searchDraft, setSearchDraft] = useState("");
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  const refresh = useCallback(
    async (term = search) => {
      try {
        const [bookList, readerList, recordList] = await Promise.all([
          listBooks(term),
          listReaders(),
          listBorrowRecords(),
        ]);
        setBooks(bookList);
        setReaders(readerList);
        setRecords(recordList);
        setError("");
      } catch (problem) {
        setError(messageOf(problem));
      } finally {
        setLoading(false);
      }
    },
    [search],
  );

  useEffect(() => {
    void refresh();
  }, [refresh]);

  async function run(action: () => Promise<unknown>, success: string) {
    setBusy(true);
    setError("");
    setNotice("");
    try {
      await action();
      await refresh();
      setNotice(success);
      return true;
    } catch (problem) {
      setError(messageOf(problem));
      return false;
    } finally {
      setBusy(false);
    }
  }

  const activeCount = records.filter(
    (record) => record.status === "BORROWED",
  ).length;
  const availableCount = books.reduce(
    (total, book) => total + book.availableStock,
    0,
  );

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">册</span>
          <div>
            <strong>馆藏工作台</strong>
            <small>LIBRARY DESK</small>
          </div>
        </div>
        <div className="sidebar-label">管理目录</div>
        <nav aria-label="主导航" className="navigation">
          <button
            className={section === "books" ? "active" : ""}
            onClick={() => setSection("books")}
          >
            <span aria-hidden="true">▤</span> 图书管理
          </button>
          <button
            className={section === "readers" ? "active" : ""}
            onClick={() => setSection("readers")}
          >
            <span aria-hidden="true">◇</span> 读者管理
          </button>
          <button
            className={section === "borrows" ? "active" : ""}
            onClick={() => setSection("borrows")}
          >
            <span aria-hidden="true">↗</span> 借阅管理
          </button>
        </nav>
        <div className="sidebar-foot">
          <span className="status-dot" />
          馆藏服务 <strong>运行中</strong>
        </div>
      </aside>

      <main className="content">
        <header className="topbar">
          <span>图书馆 · 日常管理</span>
          <span className="date-label">
            {new Intl.DateTimeFormat("zh-CN", {
              year: "numeric",
              month: "long",
              day: "numeric",
            }).format(new Date())}
          </span>
        </header>
        <section className="page-intro">
          <div>
            <p className="eyebrow">LIBRARY MANAGEMENT</p>
            <h1>
              {section === "books"
                ? "图书管理"
                : section === "readers"
                  ? "读者管理"
                  : "借阅管理"}
            </h1>
            <p className="subtitle">
              {section === "books"
                ? "维护馆藏书目，随时掌握可借库存。"
                : section === "readers"
                  ? "登记读者，让每一次借阅都有迹可循。"
                  : "办理借出与归还，查看最新流通记录。"}
            </p>
          </div>
          <div className="intro-stamp" aria-hidden="true">
            馆<br />藏
          </div>
        </section>
        <div className="overview" aria-label="馆藏概览">
          <div>
            <small>馆藏书目</small>
            <strong>{books.length}</strong>
            <span>种图书</span>
          </div>
          <div>
            <small>可借册数</small>
            <strong>{availableCount}</strong>
            <span>册在架</span>
          </div>
          <div>
            <small>未归还</small>
            <strong>{activeCount}</strong>
            <span>条记录</span>
          </div>
        </div>
        {error && (
          <div className="message error" role="alert">
            {error}
          </div>
        )}
        {notice && (
          <div className="message success" role="status">
            {notice}
          </div>
        )}
        {loading ? (
          <div className="loading">正在读取馆藏数据…</div>
        ) : section === "books" ? (
          <BooksView
            books={books}
            searchDraft={searchDraft}
            setSearchDraft={setSearchDraft}
            onSearch={() => {
              setSearch(searchDraft);
              void refresh(searchDraft);
            }}
            busy={busy}
            run={run}
          />
        ) : section === "readers" ? (
          <ReadersView readers={readers} busy={busy} run={run} />
        ) : (
          <BorrowsView
            books={books}
            readers={readers}
            records={records}
            busy={busy}
            run={run}
          />
        )}
      </main>
    </div>
  );
}

function BooksView({
  books,
  searchDraft,
  setSearchDraft,
  onSearch,
  busy,
  run,
}: {
  books: Book[];
  searchDraft: string;
  setSearchDraft: (value: string) => void;
  onSearch: () => void;
  busy: boolean;
  run: (action: () => Promise<unknown>, success: string) => Promise<boolean>;
}) {
  const [form, setForm] = useState<BookInput>(emptyBook);
  const [editing, setEditing] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);

  function startCreate() {
    setEditing(null);
    setForm(emptyBook);
    setShowForm(true);
  }
  function startEdit(book: Book) {
    setEditing(book.id);
    setForm({
      isbn: book.isbn,
      title: book.title,
      author: book.author,
      publisher: book.publisher || "",
      totalStock: book.totalStock,
      availableStock: book.availableStock,
    });
    setShowForm(true);
  }
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      await run(
        () => (editing === null ? createBook(form) : updateBook(editing, form)),
        editing === null ? "图书已添加" : "图书已更新",
      )
    )
      setShowForm(false);
  }

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">BOOK CATALOG</p>
          <h2>馆藏目录</h2>
        </div>
        <button className="primary" onClick={startCreate}>
          ＋ 添加图书
        </button>
      </div>
      <div className="toolbar">
        <form
          onSubmit={(event) => {
            event.preventDefault();
            onSearch();
          }}
        >
          <label className="sr-only" htmlFor="book-search">
            搜索书名
          </label>
          <input
            id="book-search"
            value={searchDraft}
            onChange={(event) => setSearchDraft(event.target.value)}
            placeholder="按书名搜索…"
          />
          <button type="submit" className="secondary">
            搜索
          </button>
        </form>
        <span>共 {books.length} 条结果</span>
      </div>
      {showForm && (
        <form className="editor" onSubmit={submit}>
          <div className="editor-title">
            <h3>{editing === null ? "添加图书" : "编辑图书"}</h3>
            <button
              type="button"
              className="text-button"
              onClick={() => setShowForm(false)}
            >
              关闭
            </button>
          </div>
          <div className="field-grid">
            <label>
              书名
              <input
                required
                value={form.title}
                onChange={(event) =>
                  setForm({ ...form, title: event.target.value })
                }
              />
            </label>
            <label>
              ISBN
              <input
                required
                maxLength={20}
                disabled={editing !== null}
                value={form.isbn}
                onChange={(event) =>
                  setForm({ ...form, isbn: event.target.value })
                }
              />
            </label>
            <label>
              作者
              <input
                required
                value={form.author}
                onChange={(event) =>
                  setForm({ ...form, author: event.target.value })
                }
              />
            </label>
            <label>
              出版社
              <input
                value={form.publisher || ""}
                onChange={(event) =>
                  setForm({ ...form, publisher: event.target.value })
                }
              />
            </label>
            <label>
              总库存
              <input
                type="number"
                min="0"
                required
                value={form.totalStock}
                onChange={(event) =>
                  setForm({ ...form, totalStock: Number(event.target.value) })
                }
              />
            </label>
            <label>
              可借库存
              <input
                type="number"
                min="0"
                max={form.totalStock}
                required
                value={form.availableStock}
                onChange={(event) =>
                  setForm({
                    ...form,
                    availableStock: Number(event.target.value),
                  })
                }
              />
            </label>
          </div>
          <button className="primary" disabled={busy}>
            保存图书
          </button>
        </form>
      )}
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>书名 / ISBN</th>
              <th>作者</th>
              <th>出版社</th>
              <th>馆藏</th>
              <th>可借</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id}>
                <td>
                  <strong>{book.title}</strong>
                  <small>{book.isbn}</small>
                </td>
                <td>{book.author}</td>
                <td>{book.publisher || "—"}</td>
                <td>{book.totalStock}</td>
                <td>
                  <span
                    className={
                      book.availableStock > 0 ? "pill teal" : "pill muted"
                    }
                  >
                    {book.availableStock > 0
                      ? `${book.availableStock} 册`
                      : "已借完"}
                  </span>
                </td>
                <td className="actions">
                  <button onClick={() => startEdit(book)}>编辑</button>
                  <button
                    disabled={busy}
                    onClick={() => {
                      if (window.confirm(`确定删除《${book.title}》吗？`))
                        void run(() => deleteBook(book.id), "图书已删除");
                    }}
                  >
                    删除
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {books.length === 0 && (
          <div className="empty">
            没有找到图书。试试其他书名，或添加第一本图书。
          </div>
        )}
      </div>
    </section>
  );
}

function ReadersView({
  readers,
  busy,
  run,
}: {
  readers: Reader[];
  busy: boolean;
  run: (action: () => Promise<unknown>, success: string) => Promise<boolean>;
}) {
  const [form, setForm] = useState<ReaderInput>(emptyReader);
  const [showForm, setShowForm] = useState(false);
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (await run(() => createReader(form), "读者已登记")) {
      setForm(emptyReader);
      setShowForm(false);
    }
  }
  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">READER DIRECTORY</p>
          <h2>读者名录</h2>
        </div>
        <button className="primary" onClick={() => setShowForm(true)}>
          ＋ 登记读者
        </button>
      </div>
      {showForm && (
        <form className="editor" onSubmit={submit}>
          <div className="editor-title">
            <h3>登记读者</h3>
            <button
              type="button"
              className="text-button"
              onClick={() => setShowForm(false)}
            >
              关闭
            </button>
          </div>
          <div className="field-grid">
            <label>
              读者编号
              <input
                required
                value={form.readerNo}
                onChange={(event) =>
                  setForm({ ...form, readerNo: event.target.value })
                }
              />
            </label>
            <label>
              姓名
              <input
                required
                value={form.name}
                onChange={(event) =>
                  setForm({ ...form, name: event.target.value })
                }
              />
            </label>
            <label>
              电话
              <input
                value={form.phone || ""}
                onChange={(event) =>
                  setForm({ ...form, phone: event.target.value })
                }
              />
            </label>
          </div>
          <button className="primary" disabled={busy}>
            保存读者
          </button>
        </form>
      )}
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>姓名</th>
              <th>读者编号</th>
              <th>联系电话</th>
            </tr>
          </thead>
          <tbody>
            {readers.map((reader) => (
              <tr key={reader.id}>
                <td>
                  <strong>{reader.name}</strong>
                </td>
                <td className="mono">{reader.readerNo}</td>
                <td>{reader.phone || "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {readers.length === 0 && (
          <div className="empty">还没有读者。先登记一位读者，再办理借阅。</div>
        )}
      </div>
    </section>
  );
}

function BorrowsView({
  books,
  readers,
  records,
  busy,
  run,
}: {
  books: Book[];
  readers: Reader[];
  records: BorrowRecord[];
  busy: boolean;
  run: (action: () => Promise<unknown>, success: string) => Promise<boolean>;
}) {
  const [bookId, setBookId] = useState("");
  const [readerId, setReaderId] = useState("");
  const bookNames = useMemo(
    () => new Map(books.map((book) => [book.id, book.title])),
    [books],
  );
  const readerNames = useMemo(
    () => new Map(readers.map((reader) => [reader.id, reader.name])),
    [readers],
  );
  async function submit(event: FormEvent) {
    event.preventDefault();
    if (
      await run(
        () => createBorrow(Number(bookId), Number(readerId)),
        "借阅已办理",
      )
    ) {
      setBookId("");
      setReaderId("");
    }
  }
  return (
    <div className="circulation-layout">
      <section className="panel borrow-panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">NEW LOAN</p>
            <h2>办理借阅</h2>
          </div>
        </div>
        <p className="help">选择图书和读者，系统会自动扣减可借库存。</p>
        <form className="borrow-form" onSubmit={submit}>
          <label>
            图书
            <select
              required
              value={bookId}
              onChange={(event) => setBookId(event.target.value)}
            >
              <option value="">请选择图书</option>
              {books
                .filter((book) => book.availableStock > 0)
                .map((book) => (
                  <option key={book.id} value={book.id}>
                    {book.title} · 可借 {book.availableStock} 册
                  </option>
                ))}
            </select>
          </label>
          <label>
            读者
            <select
              required
              value={readerId}
              onChange={(event) => setReaderId(event.target.value)}
            >
              <option value="">请选择读者</option>
              {readers.map((reader) => (
                <option key={reader.id} value={reader.id}>
                  {reader.name} · {reader.readerNo}
                </option>
              ))}
            </select>
          </label>
          <button
            className="primary"
            disabled={busy || books.length === 0 || readers.length === 0}
          >
            确认借出
          </button>
        </form>
      </section>
      <section className="panel records-panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">CIRCULATION LOG</p>
            <h2>流通记录</h2>
          </div>
          <span className="count-label">最近 {records.length} 条</span>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>记录</th>
                <th>图书</th>
                <th>读者</th>
                <th>借出时间</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {records.map((record) => (
                <tr key={record.id}>
                  <td>
                    <strong>借阅 #{record.id}</strong>
                  </td>
                  <td>
                    {bookNames.get(record.bookId) || `图书 #${record.bookId}`}
                  </td>
                  <td>
                    {readerNames.get(record.readerId) ||
                      `读者 #${record.readerId}`}
                  </td>
                  <td>{formatDate(record.borrowedAt)}</td>
                  <td>
                    <span
                      className={
                        record.status === "BORROWED"
                          ? "pill amber"
                          : "pill teal"
                      }
                    >
                      {record.status === "BORROWED" ? "借阅中" : "已归还"}
                    </span>
                  </td>
                  <td className="actions">
                    {record.status === "BORROWED" && (
                      <button
                        disabled={busy}
                        onClick={() =>
                          void run(() => returnBorrow(record.id), "图书已归还")
                        }
                      >
                        归还
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {records.length === 0 && (
            <div className="empty">
              还没有借阅记录。使用左侧表单办理第一笔借阅。
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

function messageOf(problem: unknown) {
  return problem instanceof Error ? problem.message : "操作失败，请稍后重试";
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

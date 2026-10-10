# API 文档

基础地址：`http://localhost:8080`

除无请求体的 GET、DELETE、PUT 归还接口外，JSON 请求应携带：

```http
Content-Type: application/json
```

## 图书

### 新增图书

`POST /api/books`

```json
{
  "isbn": "JAVA-001",
  "title": "Java 编程思想",
  "author": "Bruce Eckel",
  "publisher": "机械工业出版社",
  "totalStock": 10,
  "availableStock": 10
}
```

`isbn` 不能为空且最长 20 个字符；`totalStock`、`availableStock` 必须大于等于 0，且可借库存不能超过总库存。

成功：`201 Created`

```json
{
  "id": 1,
  "isbn": "JAVA-001",
  "title": "Java 编程思想",
  "author": "Bruce Eckel",
  "publisher": "机械工业出版社",
  "totalStock": 10,
  "availableStock": 10
}
```

### 查询图书

`GET /api/books/{id}`

成功：`200 OK`

### 修改图书

`PUT /api/books/{id}`

请求体字段与新增图书一致。

成功：`200 OK`

> 当前实现保留图书原 ISBN；请求中的 `isbn` 用于通过参数校验，但不会修改已有 ISBN。

### 删除图书

`DELETE /api/books/{id}`

成功：`204 No Content`

这是逻辑删除，记录仍保留在数据库中，但常规查询不会返回它。

### 分页查询图书

`GET /api/books?title=Java&page=1&size=10`

参数：

- `title`：可选，按标题模糊查询。
- `page`：可选，默认 `1`。
- `size`：可选，默认 `10`。

成功：`200 OK`

```json
[
  {
    "id": 1,
    "isbn": "JAVA-001",
    "title": "Java 编程思想",
    "author": "Bruce Eckel",
    "publisher": "机械工业出版社",
    "totalStock": 10,
    "availableStock": 8
  }
]
```

## 读者

### 查询读者列表

`GET /api/readers`：返回未删除的全部读者，按 ID 从新到旧排序。

### 新增读者

`POST /api/readers`

```json
{
  "readerNo": "R-001",
  "name": "张三",
  "phone": "13800000000"
}
```

`readerNo` 和 `name` 不能为空；读者编号必须唯一。

成功：`201 Created`

```json
{
  "id": 1,
  "readerNo": "R-001",
  "name": "张三",
  "phone": "13800000000"
}
```

### 查询读者

`GET /api/readers/{id}`

成功：`200 OK`

## 借阅

### 查询最近借阅记录

`GET /api/borrow-records`：返回最近 100 条借阅记录，按借出时间从新到旧排序。

### 借书

`POST /api/borrow-records`

```json
{
  "bookId": 1,
  "readerId": 1
}
```

`bookId`、`readerId` 必须是正整数。图书和读者必须存在，且图书可借库存必须大于 0。

成功：`201 Created`

```json
{
  "id": 1,
  "bookId": 1,
  "readerId": 1,
  "borrowedAt": "2026-08-07T10:00:00",
  "returnedAt": null,
  "status": "BORROWED"
}
```

借书时执行原子库存扣减：只有数据库实际扣减成功时，才会创建借阅记录。

### 还书

`PUT /api/borrow-records/{id}/return`

不需要请求体。

成功：`200 OK`

```json
{
  "id": 1,
  "bookId": 1,
  "readerId": 1,
  "borrowedAt": "2026-08-07T10:00:00",
  "returnedAt": "2026-08-07T11:00:00",
  "status": "RETURNED"
}
```

同一借阅记录只能归还一次。

### 查询读者借阅记录

`GET /api/readers/{readerId}/borrow-records`

成功：`200 OK`

```json
[
  {
    "id": 1,
    "bookId": 1,
    "readerId": 1,
    "borrowedAt": "2026-08-07T10:00:00",
    "returnedAt": null,
    "status": "BORROWED"
  }
]
```

## 统一错误响应

异常响应格式：

```json
{
  "code": "BOOK_OUT_OF_STOCK",
  "message": "Book is out of stock: 1",
  "timestamp": "2026-08-07T10:00:00"
}
```

| HTTP 状态码 | 错误码 | 含义 |
| --- | --- | --- |
| 400 | `VALIDATION_ERROR` | 请求字段不合法，例如 ISBN 超过 20 个字符。 |
| 400 | `INVALID_STOCK` | 库存为负，或可借库存超过总库存。 |
| 404 | `BOOK_NOT_FOUND` | 图书不存在或已逻辑删除。 |
| 404 | `READER_NOT_FOUND` | 读者不存在或已逻辑删除。 |
| 404 | `BORROW_RECORD_NOT_FOUND` | 借阅记录不存在。 |
| 409 | `DUPLICATE_ISBN` | ISBN 已存在。 |
| 409 | `DUPLICATE_READER_NO` | 读者编号已存在。 |
| 409 | `BOOK_OUT_OF_STOCK` | 图书没有可借库存。 |
| 409 | `ALREADY_RETURNED` | 借阅记录已经归还。 |


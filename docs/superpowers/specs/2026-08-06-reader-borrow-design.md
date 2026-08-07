# 阶段 3：读者与借阅业务设计

## 目标

在现有图书 CRUD 基础上，增加读者管理和借阅/归还流程。

## 数据模型

### reader

- `id`
- `reader_no`：读者编号，唯一
- `name`
- `phone`
- `deleted`
- `created_at`
- `updated_at`

### borrow_record

- `id`
- `book_id`
- `reader_id`
- `borrowed_at`
- `returned_at`：未归还时为 null
- `status`：BORROWED 或 RETURNED

## 业务规则

- 读者编号唯一。
- 只能借阅未逻辑删除的图书和读者。
- `available_stock` 为 0 时不能借阅。
- 借阅成功后可用库存减 1。
- 归还成功后可用库存加 1。
- 同一借阅记录不能重复归还。
- 暂不处理逾期、续借和罚款。

## 接口范围

- `POST /api/readers`
- `GET /api/readers/{id}`
- `POST /api/borrow-records`
- `PUT /api/borrow-records/{id}/return`
- `GET /api/readers/{id}/borrow-records`

## 学习边界

用户手写：实体、Mapper SQL、Service 业务规则、Controller 测试。

AI 辅助：表结构检查、异常场景梳理、测试失败分析。

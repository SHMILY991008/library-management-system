# 阶段 2：参数校验与统一异常处理设计

## 目标

让接口能够在进入 Service 前拦截明显的非法请求，并把业务异常转换为稳定、易读的 JSON 响应。

## 范围

- `CreateBookRequest`、`UpdateBookRequest` 使用 Bean Validation 注解。
- Controller 的请求体使用 `@Valid`。
- 增加统一错误响应对象 `ErrorResponse`。
- 增加 `@RestControllerAdvice` 全局异常处理器。
- 处理参数校验异常、图书不存在异常、ISBN 冲突和非法库存。
- 为上述行为编写 Controller 测试。

## 约定

- 参数错误返回 HTTP 400。
- 图书不存在返回 HTTP 404。
- ISBN 冲突返回 HTTP 409。
- 错误响应至少包含 `code`、`message`、`timestamp`。
- Service 继续负责跨字段业务规则，例如 `availableStock <= totalStock`。

## 学习边界

用户手写：校验规则、异常类型、异常处理映射和测试断言。

AI 辅助：解释注解、检查编译错误、分析测试失败原因。

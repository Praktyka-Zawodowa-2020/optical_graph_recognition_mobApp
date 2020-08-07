[app](../index.md) / [com.pzpg.ogr.api.request](./index.md)

## Package com.pzpg.ogr.api.request

### Types

| Name | Summary |
|---|---|
| [GraphFormat](-graph-format/index.md) | Enum class to represent formats of graph which we use in the project`enum class GraphFormat` |
| [ProcessMode](-process-mode/index.md) | Enum class to represent a process modes to recognise a graph`enum class ProcessMode` |
| [RequestServer](-request-server/index.md) | Class responsible for request to server`class RequestServer` |

### Exceptions

| Name | Summary |
|---|---|
| [BadRequestException](-bad-request-exception/index.md) | BadRequestException class to throw this when request message is invalid for server Has thrown when code status of the request is 400`class BadRequestException : `[`RequestServerException`](-request-server-exception/index.md) |
| [NotAllowedMethodException](-not-allowed-method-exception/index.md) | BadRequestException class to throw this when method of request is not allowed Has thrown when code status of the request is 405`class NotAllowedMethodException : `[`RequestServerException`](-request-server-exception/index.md) |
| [RequestServerException](-request-server-exception/index.md) | Opens Exception class to throw this when something is wrong with requests to the server`open class RequestServerException : `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html) |
| [TimeOutException](-time-out-exception/index.md) | Has thrown when code status of the request is -1`class TimeOutException : `[`RequestServerException`](-request-server-exception/index.md) |
| [UnauthorizedException](-unauthorized-exception/index.md) | UnauthorizedException class to throw this when user has not authorized Has thrown when code status of the request is 401`class UnauthorizedException : `[`RequestServerException`](-request-server-exception/index.md) |

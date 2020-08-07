[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServerException](./index.md)

# RequestServerException

`open class RequestServerException : `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html)

Opens Exception class to throw this when something is wrong with requests to the server

### Parameters

`message` - a message to show, inherited from Exception standard class

**Author**
Władysław Jakołcewicz

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Opens Exception class to throw this when something is wrong with requests to the server`RequestServerException(message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?)` |

### Inheritors

| Name | Summary |
|---|---|
| [BadRequestException](../-bad-request-exception/index.md) | BadRequestException class to throw this when request message is invalid for server Has thrown when code status of the request is 400`class BadRequestException : `[`RequestServerException`](./index.md) |
| [NotAllowedMethodException](../-not-allowed-method-exception/index.md) | BadRequestException class to throw this when method of request is not allowed Has thrown when code status of the request is 405`class NotAllowedMethodException : `[`RequestServerException`](./index.md) |
| [TimeOutException](../-time-out-exception/index.md) | Has thrown when code status of the request is -1`class TimeOutException : `[`RequestServerException`](./index.md) |
| [UnauthorizedException](../-unauthorized-exception/index.md) | UnauthorizedException class to throw this when user has not authorized Has thrown when code status of the request is 401`class UnauthorizedException : `[`RequestServerException`](./index.md) |

[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [downloadGraph](./download-graph.md)

# downloadGraph

`suspend fun downloadGraph(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, format: `[`GraphFormat`](../-graph-format/index.md)`?, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`?`

Suspend method to download a file which representations a graph which has been recognitions by the server

### Parameters

`guid` - a guid of the uploaded image which we want to process

`jwtToken` - access token gotten from [refreshToken](refresh-token.md) method or [authorize](authorize.md)

`format` - recognized graph format which we want to take after processing

### Exceptions

`BadRequestException` -

`UnauthorizedException` -

`TimeOutException` -

`RequestServerException` -

**Return**
[File](https://docs.oracle.com/javase/6/docs/api/java/io/File.html) temporary file (graph)

**Author**
Władysław Jakołcewicz


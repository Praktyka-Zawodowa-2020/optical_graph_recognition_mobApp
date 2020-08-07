[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [processImage](./process-image.md)

# processImage

`suspend fun processImage(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../-process-mode/index.md)`?): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Suspend method to process a image for graph recognition

### Parameters

`guid` - a guid of the uploaded image which we want to process

`jwtToken` - access token gotten from [refreshToken](refresh-token.md) method or [authorize](authorize.md)

`mode` - a mode of the recognition

`format` - recognized graph format which we want to take after processing

### Exceptions

`BadRequestException` -

`UnauthorizedException` -

`TimeOutException` -

`RequestServerException` -

**Return**
temporary file (graph)

**Author**
Władysław Jakołcewicz


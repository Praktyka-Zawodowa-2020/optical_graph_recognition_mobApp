[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [uploadImage](./upload-image.md)

# uploadImage

`suspend fun uploadImage(dir: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

Suspend method to upload an image which will processed to the server

### Parameters

`dir` - a directory where the image is located, has String type

`name` - a name of the image has String type

`jwtToken` - access token gotten from [refreshToken](refresh-token.md) method or [authorize](authorize.md)

### Exceptions

`BadRequestException` -

`UnauthorizedException` -

`TimeOutException` -

`RequestServerException` -

**Return**
guid of the uploaded image, has String type

**Author**
Władysław Jakołcewicz


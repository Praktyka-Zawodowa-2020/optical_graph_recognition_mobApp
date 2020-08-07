[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [deleteData](./delete-data.md)

# deleteData

`suspend fun deleteData(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Suspend method to delete one of entities with processed graphs includes image, graphMl, g6 files

### Parameters

`guid` - a guid of the uploaded image which we want to process

`jwtToken` - access token gotten from [refreshToken](refresh-token.md) method or [authorize](authorize.md)

### Exceptions

`BadRequestException` -

`UnauthorizedException` -

`TimeOutException` -

`RequestServerException` -

**Return**
[JSONArray](https://developer.android.com/reference/org/json/JSONArray.html) array of entities where contains name, guid, ownersEmail, createdAt, isPublic

**Author**
Władysław Jakołcewicz


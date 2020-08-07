[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [getHistory](./get-history.md)

# getHistory

`suspend fun getHistory(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`JSONArray`](https://developer.android.com/reference/org/json/JSONArray.html)

Suspend method to get history from the server about the user activity

### Parameters

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


[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [refreshToken](./refresh-token.md)

# refreshToken

`suspend fun refreshToken(rToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`JSONObject`](https://developer.android.com/reference/org/json/JSONObject.html)

Suspend method to refresh token method to refresh the access token "jwtToken" when the time
of the token has expired.

### Parameters

`rToken` - refresh token which has string type

### Exceptions

`BadRequestException` -

`TimeOutException` -

`RequestServerException` -

**Return**
JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
this three keys have string value

**Author**
Władysław Jakołcewicz


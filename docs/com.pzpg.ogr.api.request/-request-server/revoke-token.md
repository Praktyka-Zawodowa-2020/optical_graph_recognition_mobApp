[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [revokeToken](./revoke-token.md)

# revokeToken

`suspend fun revokeToken(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, rToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Suspend method to revoke token when user logged out

### Parameters

`jwtToken` - access token gotten from [refreshToken](refresh-token.md) method or [authorize](authorize.md)

`rToken` - refresh token gotten when user authorize or refresh jwtToken

### Exceptions

`BadRequestException` -

`UnauthorizedException` -

`TimeOutException` -

`RequestServerException` -

**Author**
Władysław Jakołcewicz


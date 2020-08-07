[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](index.md) / [authorize](./authorize.md)

# authorize

`suspend fun authorize(account: GoogleSignInAccount): `[`JSONObject`](https://developer.android.com/reference/org/json/JSONObject.html)

Authorizes a user on the server to use the API

### Parameters

`account` - a google account has GoogleSignInAccount type

### Exceptions

`BadRequestException` -

`TimeOutException` -

`RequestServerException` -

**Return**
JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
this three keys have string value

**Author**
Władysław Jakołcewicz


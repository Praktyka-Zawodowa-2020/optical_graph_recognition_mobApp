[app](../../index.md) / [com.pzpg.ogr.api](../index.md) / [TokenManager](./index.md)

# TokenManager

`class TokenManager`

Class for managing the extraction of tokens from shared preferences.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Class for managing the extraction of tokens from shared preferences.`TokenManager(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [clear](clear.md) | Clean our shared preferences`fun clear(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getJwtToken](get-jwt-token.md) | Get Jwt token from shared preferences.`fun getJwtToken(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [getRefreshToken](get-refresh-token.md) | Get Refresh token from shared preferences.`fun getRefreshToken(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [setJwtToken](set-jwt-token.md) | Set Jwt token in shared preferences.`fun setJwtToken(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setRefreshToken](set-refresh-token.md) | Set Refresh token in shared preferences.`fun setRefreshToken(refreshToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

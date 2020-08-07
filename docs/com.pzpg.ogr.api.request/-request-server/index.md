[app](../../index.md) / [com.pzpg.ogr.api.request](../index.md) / [RequestServer](./index.md)

# RequestServer

`class RequestServer`

Class responsible for request to server

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Class responsible for request to server`RequestServer(serverUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [authorize](authorize.md) | Authorizes a user on the server to use the API`suspend fun authorize(account: GoogleSignInAccount): `[`JSONObject`](https://developer.android.com/reference/org/json/JSONObject.html) |
| [deleteAll](delete-all.md) | Suspend method to delete all entities with processed graphs includes image, graphMl, g6 files`suspend fun deleteAll(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [deleteData](delete-data.md) | Suspend method to delete one of entities with processed graphs includes image, graphMl, g6 files`suspend fun deleteData(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [downloadGraph](download-graph.md) | Suspend method to download a file which representations a graph which has been recognitions by the server`suspend fun downloadGraph(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, format: `[`GraphFormat`](../-graph-format/index.md)`?, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`?` |
| [getHistory](get-history.md) | Suspend method to get history from the server about the user activity`suspend fun getHistory(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`JSONArray`](https://developer.android.com/reference/org/json/JSONArray.html) |
| [processImage](process-image.md) | Suspend method to process a image for graph recognition`suspend fun processImage(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../-process-mode/index.md)`?): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [refreshToken](refresh-token.md) | Suspend method to refresh token method to refresh the access token "jwtToken" when the time of the token has expired.`suspend fun refreshToken(rToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`JSONObject`](https://developer.android.com/reference/org/json/JSONObject.html) |
| [revokeToken](revoke-token.md) | Suspend method to revoke token when user logged out`suspend fun revokeToken(jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, rToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [uploadImage](upload-image.md) | Suspend method to upload an image which will processed to the server`suspend fun uploadImage(dir: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, jwtToken: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

[app](../../index.md) / [com.pzpg.ogr.api](../index.md) / [RequestManager](./index.md)

# RequestManager

`class RequestManager`

Class for managing requests to the server.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Class for managing requests to the server.`RequestManager(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, account: GoogleSignInAccount)` |

### Functions

| Name | Summary |
|---|---|
| [authenticate](authenticate.md) | Suspend fun to called from a coroutine, required for user authentication to the server.`suspend fun authenticate(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [deleteAll](delete-all.md) | Suspend fun to called from a coroutine, used ee in [RequestServer.deleteAll](../../com.pzpg.ogr.api.request/-request-server/delete-all.md)`suspend fun deleteAll(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [deleteData](delete-data.md) | Suspend fun to called from a coroutine, used to ... see in [RequestServer.deleteData](../../com.pzpg.ogr.api.request/-request-server/delete-data.md)`suspend fun deleteData(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [downloadProcessedGraph](download-processed-graph.md) | Suspend fun to called from a coroutine, used to download the processed graph`suspend fun downloadProcessedGraph(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, format: `[`GraphFormat`](../../com.pzpg.ogr.api.request/-graph-format/index.md)` = GraphFormat.GraphML): `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`?` |
| [getHistory](get-history.md) | Suspend fun to called from a coroutine, used ee in [RequestServer.getHistory](../../com.pzpg.ogr.api.request/-request-server/get-history.md)`suspend fun getHistory(): `[`JSONArray`](https://developer.android.com/reference/org/json/JSONArray.html)`?` |
| [processImage](process-image.md) | Suspend fun to called from a coroutine, required for process uploaded image [uploadImage](upload-image.md)`suspend fun processImage(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../../com.pzpg.ogr.api.request/-process-mode/index.md)` = ProcessMode.AUTO): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>`suspend fun processImage(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../../com.pzpg.ogr.api.request/-process-mode/index.md)` = ProcessMode.AUTO): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [refresh](refresh.md) | Suspend fun to called from a coroutine, required for refresh jwtToken`suspend fun refresh(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [revokeToken](revoke-token.md) | Suspend fun to called from a coroutine, used to ... see in [RequestServer.revokeToken](../../com.pzpg.ogr.api.request/-request-server/revoke-token.md)`suspend fun revokeToken(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [uploadImage](upload-image.md) | Suspend fun to called from a coroutine to upload image to server`suspend fun uploadImage(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

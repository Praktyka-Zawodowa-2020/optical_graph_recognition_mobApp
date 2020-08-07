[app](../../index.md) / [com.pzpg.ogr.api](../index.md) / [RequestManager](index.md) / [processImage](./process-image.md)

# processImage

`suspend fun processImage(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../../com.pzpg.ogr.api.request/-process-mode/index.md)` = ProcessMode.AUTO): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Suspend fun to called from a coroutine, required for process uploaded image [uploadImage](upload-image.md)

### Parameters

`path` - path to the image without name

`name` - name of the image

`mode` - process mode [ProcessMode](../../com.pzpg.ogr.api.request/-process-mode/index.md)

**Return**
temporary file with graph inside or none, when something went wrong

**Author**
Władysław Jakołcewicz

`suspend fun processImage(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mode: `[`ProcessMode`](../../com.pzpg.ogr.api.request/-process-mode/index.md)` = ProcessMode.AUTO): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Suspend fun to called from a coroutine, required for process uploaded image [uploadImage](upload-image.md)

### Parameters

`guid` - universally unique identifier of uploaded image

`mode` - process mode [ProcessMode](../../com.pzpg.ogr.api.request/-process-mode/index.md)

**Return**
temporary file with graph inside or none, when something went wrong

**Author**
Władysław Jakołcewicz


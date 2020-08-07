[app](../../index.md) / [com.pzpg.ogr.api](../index.md) / [RequestManager](index.md) / [uploadImage](./upload-image.md)

# uploadImage

`suspend fun uploadImage(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Suspend fun to called from a coroutine to upload image to server

### Parameters

`path` - path to the image without name

`name` - name of the image

**Return**
[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) universally unique identifier (GUID) of uploaded image or none, when something went wrong

**Author**
Władysław Jakołcewicz


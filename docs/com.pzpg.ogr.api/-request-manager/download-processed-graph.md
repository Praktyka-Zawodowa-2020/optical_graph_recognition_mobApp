[app](../../index.md) / [com.pzpg.ogr.api](../index.md) / [RequestManager](index.md) / [downloadProcessedGraph](./download-processed-graph.md)

# downloadProcessedGraph

`suspend fun downloadProcessedGraph(guid: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, format: `[`GraphFormat`](../../com.pzpg.ogr.api.request/-graph-format/index.md)` = GraphFormat.GraphML): `[`File`](https://docs.oracle.com/javase/6/docs/api/java/io/File.html)`?`

Suspend fun to called from a coroutine, used to download the processed graph

### Parameters

`guid` - universally unique identifier of uploaded image

`format` - [GraphFormat](../../com.pzpg.ogr.api.request/-graph-format/index.md) of a graph which you wont to download

**Return**
temporary file with graph inside or none, when something went wrong

**Author**
Władysław Jakołcewicz


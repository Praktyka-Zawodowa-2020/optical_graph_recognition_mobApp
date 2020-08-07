[app](../../index.md) / [com.pzpg.ogr.graph](../index.md) / [GraphActivity](./index.md)

# GraphActivity

`abstract class GraphActivity : AppCompatActivity`

Code taken from https://github.com/Team-Blox/GraphView/blob/master/sample/src/main/java/de/blox/graphview/sample/GraphActivity.java
There is a small change to adapt to the project

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Code taken from https://github.com/Team-Blox/GraphView/blob/master/sample/src/main/java/de/blox/graphview/sample/GraphActivity.java There is a small change to adapt to the project`GraphActivity()` |

### Properties

| Name | Summary |
|---|---|
| [adapter](adapter.md) | `lateinit var adapter: GraphAdapter<*>` |
| [currentFilePath](current-file-path.md) | `lateinit var currentFilePath: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [graphView](graph-view.md) | `lateinit var graphView: GraphView` |

### Functions

| Name | Summary |
|---|---|
| [createGraph](create-graph.md) | `abstract fun createGraph(): Graph` |
| [getNodeText](get-node-text.md) | `open fun getNodeText(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [onCreate](on-create.md) | `open fun onCreate(savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [readGraphSix](read-graph-six.md) | `fun readGraphSix(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Graph` |
| [saveGraphGraphml](save-graph-graphml.md) | Save currently opens graph in graphml format`fun saveGraphGraphml(graph: Graph, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [saveGraphSix](save-graph-six.md) | `fun saveGraphSix(graph: Graph): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setLayout](set-layout.md) | `abstract fun setLayout(view: GraphView): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [FruchtermanReingoldActivity](../-fruchterman-reingold-activity/index.md) | Code taken from https://github.com/Team-Blox/GraphView/blob/master/sample/src/main/java/de/blox/graphview/sample/Algorithms/FruchtermanReingoldActivity.java`class FruchtermanReingoldActivity : `[`GraphActivity`](./index.md) |

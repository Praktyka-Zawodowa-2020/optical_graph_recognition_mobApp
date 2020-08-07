[app](../../index.md) / [com.pzpg.ogr.graph](../index.md) / [Algorithm](./index.md)

# Algorithm

`class Algorithm : Layout`
*

Same algorithm that we can find here link below, with an added line in method [Algorithm.randomize](#)
that helps to save the position of the node of the recognized graph GraphML.

https://github.com/Team-Blox/GraphView/blob/master/graphview/src/main/java/de/blox/graphview/energy/FruchtermanReingoldAlgorithm.kt

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | <ul><li></li></ul>`Algorithm(iterations: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = DEFAULT_ITERATIONS)` |

### Functions

| Name | Summary |
|---|---|
| [drawEdges](draw-edges.md) | `fun drawEdges(canvas: `[`Canvas`](https://developer.android.com/reference/android/graphics/Canvas.html)`, graph: Graph, linePaint: `[`Paint`](https://developer.android.com/reference/android/graphics/Paint.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [run](run.md) | `fun run(graph: Graph, shiftX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`, shiftY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`): Size` |
| [setEdgeRenderer](set-edge-renderer.md) | `fun setEdgeRenderer(renderer: EdgeRenderer): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [CLUSTER_PADDING](-c-l-u-s-t-e-r_-p-a-d-d-i-n-g.md) | `const val CLUSTER_PADDING: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [DEFAULT_ITERATIONS](-d-e-f-a-u-l-t_-i-t-e-r-a-t-i-o-n-s.md) | `const val DEFAULT_ITERATIONS: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

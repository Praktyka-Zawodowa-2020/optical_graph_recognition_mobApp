[app](../../index.md) / [com.pzpg.ogr](../index.md) / [ProcessActivity](./index.md)

# ProcessActivity

`class ProcessActivity : AppCompatActivity`

Activity responsible for processing an image and sharing result of the processing.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Activity responsible for processing an image and sharing result of the processing.`ProcessActivity()` |

### Functions

| Name | Summary |
|---|---|
| [clickShare](click-share.md) | Share click handling`fun clickShare(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [openGraph](open-graph.md) | Open graph click handling. Used [uriGraphMl](#), the file to be opened.`fun openGraph(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [process](process.md) | Process click handling and processing process, save information like guid, uriGraphMl, uriGraphSix for future use. Used [photoPath](#), Uri of the photo whose will be processed.`fun process(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

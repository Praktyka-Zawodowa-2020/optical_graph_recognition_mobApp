[app](../../index.md) / [com.pzpg.ogr.library.listContent](../index.md) / [ListContent](./index.md)

# ListContent

`object ListContent`

Helper class for providing sample content for user interfaces created by
Android template wizards.

TODO: Replace all uses of this class before publishing your app.

### Types

| Name | Summary |
|---|---|
| [ListItem](-list-item/index.md) | A graph item representing a piece of content.`data class ListItem` |

### Properties

| Name | Summary |
|---|---|
| [ITEM_MAP](-i-t-e-m_-m-a-p.md) | A map of sample (dummy) items, by ID.`val ITEM_MAP: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, ListItem>` |
| [ITEMS](-i-t-e-m-s.md) | An array of sample (dummy) items.`val ITEMS: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<ListItem>` |

### Functions

| Name | Summary |
|---|---|
| [addItem](add-item.md) | `fun addItem(item: ListItem): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [createListItem](create-list-item.md) | `fun createListItem(position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, content: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, uri: `[`Uri`](https://developer.android.com/reference/android/net/Uri.html)`): ListItem` |
| [reset](reset.md) | `fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

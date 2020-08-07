[app](../../index.md) / [com.pzpg.ogr.library](../index.md) / [MyItemGraphRecyclerViewAdapter](./index.md)

# MyItemGraphRecyclerViewAdapter

`class MyItemGraphRecyclerViewAdapter : Adapter<ViewHolder>`

[RecyclerView.Adapter](#) that can display a [ListItem](../../com.pzpg.ogr.library.list-content/-list-content/-list-item/index.md).
TODO: Replace the implementation with code for your data type.

### Types

| Name | Summary |
|---|---|
| [OnItemListener](-on-item-listener/index.md) | `interface OnItemListener` |
| [ViewHolder](-view-holder/index.md) | `inner class ViewHolder : ViewHolder, `[`OnClickListener`](https://developer.android.com/reference/android/view/View/OnClickListener.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | [RecyclerView.Adapter](#) that can display a [ListItem](../../com.pzpg.ogr.library.list-content/-list-content/-list-item/index.md). TODO: Replace the implementation with code for your data type.`MyItemGraphRecyclerViewAdapter(values: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<ListItem>, onItemListener: OnItemListener)` |

### Properties

| Name | Summary |
|---|---|
| [mOnItemListener](m-on-item-listener.md) | `var mOnItemListener: OnItemListener` |

### Functions

| Name | Summary |
|---|---|
| [getItemCount](get-item-count.md) | `fun getItemCount(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [onBindViewHolder](on-bind-view-holder.md) | `fun onBindViewHolder(holder: ViewHolder, position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateViewHolder](on-create-view-holder.md) | `fun onCreateViewHolder(parent: `[`ViewGroup`](https://developer.android.com/reference/android/view/ViewGroup.html)`, viewType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): ViewHolder` |

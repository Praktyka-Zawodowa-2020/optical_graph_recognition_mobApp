[app](../../index.md) / [com.pzpg.ogr.takePicture](../index.md) / [TakePictureFragment](./index.md)

# TakePictureFragment

`class TakePictureFragment : Fragment`

Fragment responsible for photographing, select an image from the gallery, editing photos.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Fragment responsible for photographing, select an image from the gallery, editing photos.`TakePictureFragment()` |

### Functions

| Name | Summary |
|---|---|
| [onActivityResult](on-activity-result.md) | `fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: `[`Intent`](https://developer.android.com/reference/android/content/Intent.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateView](on-create-view.md) | `fun onCreateView(inflater: `[`LayoutInflater`](https://developer.android.com/reference/android/view/LayoutInflater.html)`, container: `[`ViewGroup`](https://developer.android.com/reference/android/view/ViewGroup.html)`?, savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [EDIT_INTENT](-e-d-i-t_-i-n-t-e-n-t.md) | `const val EDIT_INTENT: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [REQUEST_CAMERA_PHOTO](-r-e-q-u-e-s-t_-c-a-m-e-r-a_-p-h-o-t-o.md) | `const val REQUEST_CAMERA_PHOTO: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [REQUEST_GALLERY_PHOTO](-r-e-q-u-e-s-t_-g-a-l-l-e-r-y_-p-h-o-t-o.md) | `const val REQUEST_GALLERY_PHOTO: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

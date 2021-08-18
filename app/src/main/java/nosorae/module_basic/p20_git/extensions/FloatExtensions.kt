package nosorae.module_basic.p20_git.extensions

import android.content.res.Resources

internal fun Float.fromToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}
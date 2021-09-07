package nosorae.changed_name.p20_git.extensions

import android.content.res.Resources

internal fun Float.fromToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}
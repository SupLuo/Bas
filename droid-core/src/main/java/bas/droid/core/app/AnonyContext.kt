package bas.droid.core.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Created by Lucio on 2019/7/21.
 * 匿名上下文
 */
interface AnonyContext {

    val realCtx: Context

    fun startActivity(intent: Intent)

    fun startActivityForResult(intent: Intent, requestCode: Int)

    private class ActivityContext(val activity: Activity): AnonyContext {
        override val realCtx: Context
            get() = activity

        override fun startActivity(intent: Intent) {
            activity.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private class JustContext(val ctx: Context): AnonyContext {
        override val realCtx: Context
            get() = ctx

        override fun startActivity(intent: Intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            startActivity(intent)
        }
    }

    private class FragmentContext(val fragment: Fragment): AnonyContext {
        override val realCtx: Context
            get() = fragment.requireContext()

        override fun startActivity(intent: Intent) {
            fragment.startActivity(intent)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int) {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    companion object {

        @JvmStatic
        fun new(ctx: Context): AnonyContext {
            if(ctx is AnonyContext)
                return ctx
            return if(ctx is Activity){
                ActivityContext(ctx)
            } else{
                JustContext(ctx)
            }
        }

        @JvmStatic
        fun new(fragment: Fragment): AnonyContext {
            if(fragment is AnonyContext)
                return fragment
            return (FragmentContext(fragment))
        }
    }

}
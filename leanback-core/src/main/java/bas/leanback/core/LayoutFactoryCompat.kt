//package bas.leanback.core
//
//import android.app.Activity
//import android.content.Context
//import android.os.Bundle
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.view.View
//import androidx.core.view.LayoutInflaterCompat
//import androidx.core.view.LayoutInflaterFactory
//
//class LayoutFactoryCompat : LayoutInflater.Factory2{
//    init {
//        LayoutInflaterCompat.setFactory2()
//    }
//
//    override fun onCreateView(
//        parent: View?,
//        name: String,
//        context: Context,
//        attrs: AttributeSet
//    ): View? {
//
//    }
//
//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//    }
//}
//
//class ss:Activity{
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        window.decorView.isInTouchMode
//        window.decorView.viewTreeObserver.addOnTouchModeChangeListener {
//
//        }
//    }
//}
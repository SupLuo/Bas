package bas.leanback.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import bas.lib.core.data.smartObservable
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Lucio on 2021/3/31.
 * 用于处理焦点记忆
 */
open class MemoryHelper private constructor(
    open val layout: ViewGroup,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    context: Context
) {

    constructor(
        viewGroup: ViewGroup,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : this(viewGroup, attrs, defStyleAttr, viewGroup.context)

    /**
     * 是否启用焦点记忆
     */
    var isMemoryEnabled: Boolean by Delegates.smartObservable(DEFAULT_MEMORY_ENABLED) { _, oldValue, newValue ->
        if (!newValue) {
            onMemoryEnableChanged()
        }
    }

    /**
     * 焦点记忆View使用的状态类型状态
     */
    @get:MemoryState
    var memoryState: Int by Delegates.smartObservable(DEFAULT_MEMORY_STATE) { _, oldValue, newValue ->
        //属性发生了变化
        onMemoryStateChanged()
    }

    /**
     * 当前焦点记忆的View
     */
    private var memoryView: View? = null

    init {
        logd("开始解析xml属性")
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.FocusMemoryView,
            defStyleAttr,
            0
        )
        isMemoryEnabled = ta.getBoolean(
            R.styleable.FocusMemoryView_lb_focusMemoryEnable,
            DEFAULT_MEMORY_ENABLED
        )
        memoryState =
            ta.getInt(
                R.styleable.FocusMemoryView_lb_focusMemoryStateType,
                DEFAULT_MEMORY_STATE
            )
        logd("解析结果：isMemoryEnabled=${isMemoryEnabled} memoryState=${memoryState}")
        ta.recycle()
    }

    protected open fun onMemoryEnableChanged() {
        //notify changed :nothing
    }

    protected open fun onMemoryStateChanged() {
        //notify changed :nothing
    }

    /**
     * 是否处理焦点分发：如果当前未启用焦点记忆或者当前控件已经具备焦点,则不采用焦点记忆规则
     * @return true:应该处理焦点分发规则
     */
    @SuppressLint("RestrictedApi")
    protected open fun shouldHandleFocusMemory(): Boolean {
        return isMemoryEnabled && !layout.hasFocus()
    }

    /**
     * child请求焦点
     * 在自定义[ViewGroup.requestChildFocus]对应方法中回调该方法
     */
    @bas.lib.core.lang.annotation.CallByOwner
    open fun requestChildFocus(child: View?, focused: View?) {
        onChildRequestFocused(child, focused)
    }

    protected fun onChildRequestFocused(child: View?, focused: View?) {
        if (!isMemoryEnabled) {
            memoryView = null
            return
        }

        memoryView = if (layout.descendantFocusability == ViewGroup.FOCUS_BLOCK_DESCENDANTS
            || focused == null
            || layout.indexOfChild(focused) < 0
        ) {
            null
        } else {
            //说明当前请求焦点的view是ViewGroup的child view，设置view的memory state
            applyMemoryState(focused, memoryState)
            child
        }
    }

    /**
     * childView 从ViewGroup中移除
     * 在自定义[ViewGroup.onViewRemoved]对应方法中回调该方法
     * @param child the removed child view
     */
    @bas.lib.core.lang.annotation.CallByOwner
    open fun onViewRemoved(child: View?) {
        if (memoryView == child) {
            memoryView = null
        }
    }

    /**
     * 添加可以获取焦点的View
     * 在自定义[ViewGroup.addFocusables]对应方法中回调该方法
     * @return 如果处理了焦点记忆，则返回true，否则返回false
     */
    @bas.lib.core.lang.annotation.CallByOwner
    open fun addFocusables(views: ArrayList<View>?, direction: Int, focusableMode: Int): Boolean {
        if (!shouldHandleFocusMemory()) {
            logd("addFocusables：不启用焦点记忆")
            return false
        }
        val memoryView = this.memoryView
        return if (memoryView == null) {
            logd("addFocusables：焦点记忆查找失败,memoryView is null.")
            false
        } else {
            logd("addFocusables：焦点记忆查找成功,memoryView = ${memoryView}")
            views?.add(memoryView)
            true
        }
    }

    /**
     * @return 处理了焦点分发则返回true，否则返回false
     */
    @bas.lib.core.lang.annotation.CallByOwner
    open fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        if (!shouldHandleFocusMemory()) {
            logd("onRequestFocusInDescendants：不启用焦点记忆分发")
            return false
        }

        val memoryView = this.memoryView
        return if (memoryView == null) {
            logd("addFocusables：焦点记忆查找失败,memoryView is null.")
            false
        } else {
            logd("onRequestFocusInDescendants：焦点记忆查找成功,memoryView = ${memoryView}")
            memoryView.requestFocus(direction)
            true
        }
    }


    companion object {

        private const val DEFAULT_MEMORY_ENABLED = false

        private const val DEFAULT_MEMORY_STATE = MemoryState.SELECTED

        @JvmStatic
        fun create(
            layout: ViewGroup,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
        ): MemoryHelper {
            return MemoryHelper(layout, attrs, defStyleAttr)
        }

        /**
         * 根据[memoryState]修改View的状态属性
         */
        @JvmStatic
        fun applyMemoryState(memoryView: View, @MemoryState memoryState: Int) {
            when (memoryState) {
                MemoryState.SELECTED -> {
                    setSingleSelectedInParent(memoryView)
                }
                MemoryState.ACTIVATED -> {
                    setSingleActivatedInParent(memoryView)
                }
                else -> {

                }
            }
        }

        /**
         * 设置child 选中状态，其他child为未选中状态
         */
        @JvmStatic
        fun setSingleSelectedInParent(child: View): Boolean {
            val parent = child.parent as ViewGroup? ?: return false
            parent.children.forEach {
                it.isSelected = it == child
            }
            return true
        }

        /**
         * 设置child激活状态，其他child为未激活状态
         * activated
         */
        @JvmStatic
        fun setSingleActivatedInParent(child: View): Boolean {
            val parent = child.parent as ViewGroup? ?: return false
            parent.children.forEach {
                it.isActivated = it == child
            }
            return true
        }

//        /**
//         * 查找第一个用于获取焦点的拥有hasMemoryTag为true的ChildView
//         */
//        @JvmStatic
//        fun addFocusablesByMemoryTag(
//            viewGroup: ViewGroup,
//            views: ArrayList<View>?,
//            direction: Int,
//            focusableMode: Int
//        ): Boolean {
//            if (viewGroup.hasFocus())
//                return false
//
//            val view = viewGroup.children.find {
//                it.hasMemoryTag && it.canTakeFocus
//            }
//            if (view != null) {
//                views?.add(view)
//                return true
//            }
//
//            return false
//        }
//
//        /**
//         * 设置单选的焦点记忆Tag
//         */
//        @JvmStatic
//        fun setSingleMemoryTag(child: View): Boolean {
//            val parent = child.parent as ViewGroup? ?: return false
//            parent.children.forEach {
//                it.hasMemoryTag = it == child
//            }
//            return true
//        }

//        /**
//         * 是否包含自定义的焦点记忆Tag
//         */
//        internal var View.hasMemoryTag: Boolean
//            get() = this.getTag(R.id.am_focus_memory_flag) as? Boolean ?: false
//            set(value) {
//                this.setTag(R.id.am_focus_memory_flag, value)
    }

}

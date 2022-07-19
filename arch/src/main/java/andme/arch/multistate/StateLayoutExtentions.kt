package andme.arch.multistate

import andme.arch.R
import andme.core.statelayout.StateLayout
import andme.core.statelayout.StateView
import android.view.View
import android.widget.Button
import android.widget.TextView
import bas.droid.core.app.isTelevisionUiMode
import bas.droid.core.view.extensions.setTextOrGone
import bas.droid.ui.loader.core.LoaderUiState

/**
 * Created by Lucio on 2021/5/16.
 */

@Deprecated(message = "太滥用了")
fun StateView.showLoadingMsg(msg: CharSequence?) {
    showTextWithoutButton(msg)
}

@Deprecated(message = "太滥用了")
fun StateView.showEmptyMsgWithButton(
    msg: CharSequence,
    buttonText: String = "重试",
    requestFocus: Boolean = false,
    onClick: (View) -> Unit
) {
    showTextWithButton(msg, buttonText, requestFocus, onClick)
}

@Deprecated(message = "太滥用了")
fun StateView.showEmptyMsgWithoutButton(msg: CharSequence) {
    showTextWithoutButton(msg)
}

@Deprecated(message = "太滥用了")
fun StateView.showErrorMsgWithButton(
    msg: CharSequence,
    buttonText: String = "重试",
    requestFocus: Boolean = false,
    onClick: (View) -> Unit
) {
    showTextWithButton(msg, buttonText, requestFocus, onClick)
}

@Deprecated(message = "太滥用了")
fun StateView.showErrorMsgWithoutButton(msg: CharSequence) {
    showTextWithoutButton(msg)
}

@Deprecated(message = "太滥用了")
private fun StateView.showTextWithButton(
    msg: CharSequence?,
    buttonText: String = "重试",
    requestFocus: Boolean = false,
    onClick: (View) -> Unit
) {
    textViewAM?.setTextOrGone(msg)
    buttonAM?.let {
        it.visibility = View.VISIBLE
        it.text = buttonText
        it.setOnClickListener(onClick)
        if (it.context.isTelevisionUiMode()) {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
        }
        if (requestFocus) {
            it.requestFocus()
        }
    }
}

private fun StateView.showTextWithoutButton(msg: CharSequence?) {
    textViewAM?.setTextOrGone(msg)
    buttonAM?.visibility = View.GONE
}

@Deprecated(message = "太滥用了")
fun StateView.setRetryButton(
    text: String = "重试",
    requestFocus: Boolean = false,
    onClick: (View) -> Unit
) {
    buttonAM?.let {
        it.setText(text)
        it.setOnClickListener(onClick)
        if (it.context.isTelevisionUiMode()) {
            it.isFocusable = true
            it.isFocusableInTouchMode = true
        }
        if (requestFocus) {
            it.requestFocus()
        }
    }
}


fun StateLayout.setLoaderUiState(
    applyRetryWhenError: Boolean = false,
    retryBtnRequestFocus: Boolean = false,
    uiState: LoaderUiState,
    onRetryClick: (View) -> Unit
) {
    when {
        uiState is LoaderUiState.Loading -> {
            this.showLoadingView {
                this.showLoadingMsg(uiState.message)
            }
        }

        uiState is LoaderUiState.Error -> {
            showErrorView {
                if (applyRetryWhenError) {
                    this.showErrorMsgWithButton(
                        uiState.message,
                        requestFocus = retryBtnRequestFocus,
                        onClick = onRetryClick
                    )
                } else {
                    this.showErrorMsgWithoutButton(uiState.message)
                }

            }
        }
        uiState.isDataState -> {
            showContentView()
        }
        uiState.isEmptyState -> {
            val extraMessage = uiState.extra as? String
            if (extraMessage.isNullOrEmpty()) {
                showEmptyView()
            } else {
                showEmptyView {
                    showEmptyMsgWithoutButton(extraMessage)
                }
            }

        }
    }
}

fun StateLayout.setLoaderUiStateWithoutRetry(uiState: LoaderUiState) {
    when {
        uiState is LoaderUiState.Loading -> {
            this.showLoadingView {
                this.showLoadingMsg(uiState.message)
            }
        }

        uiState is LoaderUiState.Error -> {
            showErrorView {
                this.showErrorMsgWithoutButton(uiState.message)
            }
        }
        uiState is LoaderUiState.Content<*> -> {
            showContentView()
        }
    }
}


private val StateView.imageViewAM: Button? get() = view.findViewById(R.id.am_id_state_view_image)


private val StateView.textViewAM: TextView? get() = view.findViewById(R.id.am_id_state_view_text)


private val StateView.buttonAM: Button? get() = view.findViewById(R.id.am_id_state_view_button)


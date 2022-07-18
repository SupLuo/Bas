package bas.droid.adapter.mediaplayer;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 播放器状态
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({VodPlayerState.IDLE, VodPlayerState.PREPARED, VodPlayerState.PLAYING, VodPlayerState.PAUSED})
public @interface VodPlayerState {

    /**
     * 空闲状态
     */
    int IDLE = 0;

    /**
     * 已就绪状态
     */
    int PREPARED = 1;

    /**
     * 播放中状态
     */
    int PLAYING = 2;

    /**
     * 暂停状态
     */
    int PAUSED = 3;
}

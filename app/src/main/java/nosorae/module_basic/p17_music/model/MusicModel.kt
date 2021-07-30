package nosorae.module_basic.p17_music.model

data class MusicModel(
    val id: Long, // diff 유틸을 위해 존재,
    val track: String,
    val streamUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false // 서버는 가지고 있지 않고 뷰에서 가지고 있는 값
)

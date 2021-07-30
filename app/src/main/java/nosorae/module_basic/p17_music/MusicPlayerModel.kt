package nosorae.module_basic.p17_music

import nosorae.module_basic.p17_music.model.MusicModel

data class MusicPlayerModel(
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1, // 몇번째 뮤직을 플레이해야하는가
    var isWatchingPlayListView: Boolean = true
) {

    fun getAdapterModels(): List<MusicModel> {
        // 새로운 객체를 생성해줘야 리사이클러뷰가 업데이트 된다고 함?
        return playMusicList.mapIndexed { index, musicModel ->
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }



    fun updateCurrentPosition(musicModel: MusicModel) {
        currentPosition = playMusicList.indexOf(musicModel)
    }


    fun nextMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) 0 else (currentPosition + 1)
            return playMusicList[currentPosition]

    }
    fun prevMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null
        currentPosition = if ((currentPosition - 1) < 0) playMusicList.lastIndex else (currentPosition - 1)

            return playMusicList[currentPosition]
    }

    fun currentMusicModel(): MusicModel? {
        if (playMusicList.isEmpty()) return null
        return playMusicList[currentPosition]
    }



}

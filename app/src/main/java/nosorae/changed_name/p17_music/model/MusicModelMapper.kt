package nosorae.changed_name.p17_music.model

import nosorae.changed_name.p17_music.MusicPlayerModel

fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = this.streamUrl,
        coverUrl = this.coverUrl,
        artist = this.artist,
        track = this.track
    )

fun MusicDTO.mapper(): MusicPlayerModel =
    MusicPlayerModel(
        playMusicList = this.musics.mapIndexed { index, musicEntity ->
            musicEntity.mapper(index.toLong())
        }
    )
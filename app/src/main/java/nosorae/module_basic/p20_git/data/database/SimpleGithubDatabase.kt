package nosorae.module_basic.p20_git.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import nosorae.module_basic.p20_git.data.dao.GitSearchHistoryDao
import nosorae.module_basic.p20_git.data.entity.GithubRepository

@Database(entities = [GithubRepository::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {
    abstract fun repositoryDao(): GitSearchHistoryDao
}
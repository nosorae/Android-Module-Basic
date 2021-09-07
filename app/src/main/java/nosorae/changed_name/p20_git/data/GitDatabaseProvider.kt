package nosorae.changed_name.p20_git.data

import android.content.Context
import androidx.room.Room
import nosorae.changed_name.p20_git.data.database.SimpleGithubDatabase

object GitDatabaseProvider {
    private const val DB_NAME = "github_repository_app.db"

    fun provideDB(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        SimpleGithubDatabase::class.java,
        DB_NAME
    ).build()
}
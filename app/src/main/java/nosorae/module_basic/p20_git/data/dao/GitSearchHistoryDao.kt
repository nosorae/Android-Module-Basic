package nosorae.module_basic.p20_git.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import nosorae.module_basic.p20_git.data.entity.GithubRepository

@Dao
interface GitSearchHistoryDao {
    @Insert
    suspend fun insert(repo: GithubRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepository>)

    @Query("SELECT * FROM githubrepository")
    suspend fun getHistory(): List<GithubRepository>

    @Query("DELETE FROM githubrepository")
    suspend fun clearAll()
}
package nosorae.changed_name.p20_git.data.dao

import androidx.room.*
import nosorae.changed_name.p20_git.data.entity.GithubRepository

@Dao
interface GitSearchHistoryDao {
    @Insert
    suspend fun insert(repo: GithubRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepository>)

    @Query("SELECT * FROM githubrepository")
    suspend fun getHistory(): List<GithubRepository>

    @Query("SELECT * FROM githubrepository WHERE fullName=:fullName")
    suspend fun getRepository(fullName: String): GithubRepository?

    @Query("DELETE FROM githubrepository WHERE fullName=:fullName")
    suspend fun remove(fullName: String)

//    @Delete
//    suspend fun remove(repo: GithubRepository)

    @Query("DELETE FROM githubrepository")
    suspend fun clearAll()


}
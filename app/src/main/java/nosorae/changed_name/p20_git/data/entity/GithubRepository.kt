package nosorae.changed_name.p20_git.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GithubRepository")
data class GithubRepository(
    val name: String,
    @PrimaryKey val fullName: String,
    @Embedded val owner: GithubOwner,
    val description: String?,
    val language: String?,
    val updatedAt: String,
    val stargazersCount: Int
)
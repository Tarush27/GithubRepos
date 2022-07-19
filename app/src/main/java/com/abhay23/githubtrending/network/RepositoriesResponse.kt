import com.abhay23.githubtrending.model.Repository
import com.google.gson.annotations.SerializedName

data class RepositoryResponse(
    @SerializedName(value = "id")
    val id: Long,
    @SerializedName(value = "name")
    val repositoryName: String,
    @SerializedName(value = "description")
    val repositoryDescription: String,
    @SerializedName(value = "language")
    val languageName: String?,
    @SerializedName(value = "owner")
    val owner: OwnerResponse,
    @SerializedName(value = "stargazers_count")
    val starsCount: Int,
    @SerializedName(value = "forks_count")
    val forksCount: Int,
) {
    fun toRepository() : Repository {
        return Repository(
            id = id,
            languageName = languageName,
            forksCount =  forksCount,
            starsCount = starsCount,
            avatarURL = owner.avatarURL,
            organizationName = owner.organizationName,
            repositoryDescription = repositoryDescription,
            repositoryName = repositoryName
        )
    }
}


data class OwnerResponse(
    @SerializedName(value = "avatar_url")
    val avatarURL: String,
    @SerializedName(value = "login")
    val organizationName: String,
)
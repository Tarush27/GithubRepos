package com.abhay23.githubtrending.model

data class Repository(
    val id: Long,
    val organizationName: String,
    val repositoryName: String,
    val repositoryDescription: String,
    val languageName: String?,
    val avatarURL: String,
    val starsCount: Int,
    val forksCount: Int,
)

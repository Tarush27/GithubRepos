package com.abhay23.githubtrending.network

import RepositoryResponse
import com.abhay23.githubtrending.model.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    @GET("/orgs/{org}/repos")
    fun getRepositoriesForOrganization(@Path("org") organization: String?): Call<List<RepositoryResponse>>
}
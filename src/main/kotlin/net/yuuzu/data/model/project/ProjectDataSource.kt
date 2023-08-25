package net.yuuzu.data.model.project

interface ProjectDataSource {
    suspend fun getProject(id: String): Project?
    suspend fun getProjects(): List<Project>
    suspend fun insertProject(project: Project): Boolean
    suspend fun updateProject(project: Project): Boolean
}
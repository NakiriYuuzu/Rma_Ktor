package net.yuuzu.data.model.project

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq

class ProjectDataSourceImpl(
    db: CoroutineDatabase,
): ProjectDataSource {
    private val projects = db.getCollection<Project>()

    override suspend fun getProject(id: String): Project? {
        return projects.findOne(Project::id eq id)
    }

    override suspend fun getProjects(): List<Project> {
        return projects.find().toList()
    }

    override suspend fun insertProject(project: Project): Boolean {
        return projects.insertOne(project).wasAcknowledged()
    }

    override suspend fun updateProject(project: Project): Boolean {
        return projects.updateOneById(project.id, project).wasAcknowledged()
    }
}
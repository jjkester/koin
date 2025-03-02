package org.koin.full

import kotlin.test.*
import org.koin.core.logger.Level
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class TODOAppTest {

    val defaultModule = module {
        single { TasksRepository(get()) }
        single<TasksDataSource> { FakeTasksRemoteDataSource() }
    }

    val overrideModule = module {
        single<TasksDataSource> { TasksLocalDataSource() }
    }

    val overrideDef = module {
        single<TasksDataSource>{ TasksLocalDataSource() }
    }

    interface TasksDataSource
    class FakeTasksRemoteDataSource : TasksDataSource
    class TasksLocalDataSource : TasksDataSource
    class TasksRepository(val dataSource: TasksDataSource)

    @Test
    fun default_module() {
        val koinApp = koinApplication {
            printLogger(Level.DEBUG)
            modules(defaultModule)
        }
        val koin = koinApp.koin

        val tasksDataSource = koin.get<TasksDataSource>()
        assertTrue(tasksDataSource is FakeTasksRemoteDataSource)
    }

    @Test
    fun overloaded_module() {
        val koinApp = koinApplication {
            printLogger(Level.DEBUG)
            modules(defaultModule + overrideModule)
        }
        val koin = koinApp.koin

        val tasksDataSource = koin.get<TasksDataSource>()
        assertTrue(tasksDataSource is TasksLocalDataSource)
    }

    @Test
    fun overloaded_definition() {
        val koinApp = koinApplication {
            printLogger(Level.DEBUG)
            modules(defaultModule + overrideDef)
        }
        val koin = koinApp.koin

        val tasksDataSource = koin.get<TasksDataSource>()
        assertTrue(tasksDataSource is TasksLocalDataSource)
    }
}
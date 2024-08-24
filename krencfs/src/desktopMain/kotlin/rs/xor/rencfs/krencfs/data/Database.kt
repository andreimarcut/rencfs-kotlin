package rs.xor.rencfs.krencfs.data

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual suspend fun provideSQLDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
    databaseName: String,
): SqlDriver {
    val databaseFileName = "${databaseName}.sqlite.db"
    return JdbcSqliteDriver("jdbc:sqlite:$databaseFileName")
        .also {
            if (!File(databaseName).exists()) {
                schema.awaitCreate(it)
            }
            return it
        }
}

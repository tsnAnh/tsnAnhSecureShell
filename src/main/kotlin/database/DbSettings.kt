package database

import org.jetbrains.exposed.sql.Database

object DbSettings {
    val db by lazy {
        Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")
    }
}

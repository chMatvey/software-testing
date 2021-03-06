package ru.chudakov.cucumber.steps

import cucumber.api.java.After
import cucumber.api.java8.En
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chudakov.DBManager
import ru.chudakov.PgDBManager
import ru.chudakov.dao.AuthorDao
import ru.chudakov.dao.Authors
import ru.chudakov.dao.CompositionDao
import ru.chudakov.dao.Compositions
import ru.chudakov.data.Composition
import kotlin.test.assertEquals

class CompositionAddSteps : En {
    private val dbManager: DBManager = PgDBManager()

    private lateinit var compositionName: String
    private lateinit var authorName: String
    private lateinit var genreName: String

    private var composition: Composition? = null

    init {
        Given("we have new composition {string}, {string} and {string}") { compositionName: String, authorName: String, genreName: String ->
            this.compositionName = compositionName
            this.authorName = authorName
            this.genreName = genreName
        }

        When("we try add this composition") {
            composition = dbManager.addComposition(compositionName, authorName, genreName)
        }

        Then("new composition equals null {string}") { result: String ->
            assertEquals(result.toBoolean(), composition == null)
        }
    }

    @After("@addComposition")
    fun after() {
        transaction {
            composition?.let {
                CompositionDao.find { Compositions.name eq it.name }.firstOrNull { c -> c.author.name == it.author.name }?.delete()
            }
            AuthorDao.find { Authors.name eq "author1" }.firstOrNull()?.delete()
        }
    }
}

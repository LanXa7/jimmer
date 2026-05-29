package org.babyfish.jimmer.sql.kt.query

import org.babyfish.jimmer.sql.ast.LikeMode
import org.babyfish.jimmer.sql.kt.ast.expression.ilike
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.common.AbstractQueryTest

import org.babyfish.jimmer.sql.kt.model.classic.book.Book
import org.babyfish.jimmer.sql.kt.model.classic.book.name
import kotlin.test.Test

class LikeEscapeTest : AbstractQueryTest() {

    // --- No special characters: no escape clause ---

    @Test
    fun testLikeWithoutSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "GraphQL")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ?""".trimMargin()
            )
            variables("%GraphQL%")
        }
    }

    @Test
    fun testIlikeWithoutSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name ilike "graphql")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where lower(tb_1_.NAME) like ?""".trimMargin()
            )
            variables("%graphql%")
        }
    }

    // --- Underscore: should be escaped ---

    @Test
    fun testLikeWithUnderscore() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "test_1")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%test\\_1%")
        }
    }

    // --- Percent: should be escaped ---

    @Test
    fun testLikeWithPercent() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "100%")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%100\\%")
        }
    }

    // --- Percent in middle ---

    @Test
    fun testLikeWithPercentInMiddle() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "100%off")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%100\\%off%")
        }
    }

    // --- Backslash: should be escaped ---

    @Test
    fun testLikeWithBackslash() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "a\\b")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%a\\\\b%")
        }
    }

    // --- Mixed special characters ---

    @Test
    fun testLikeWithMixedSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "test_100%\\end")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%test\\_100\\%\\\\end%")
        }
    }

    // --- ilike with special characters ---

    @Test
    fun testIlikeWithUnderscore() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name ilike "test_1")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where lower(tb_1_.NAME) like ? escape '\'""".trimMargin()
            )
            variables("%test\\_1%")
        }
    }

    // --- LikeMode.EXACT with special characters ---

    @Test
    fun testLikeExactWithSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name.like("test_1", LikeMode.EXACT))
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("test\\_1")
        }
    }

    // --- LikeMode.START with special characters ---

    @Test
    fun testLikeStartWithSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name.like("test%", LikeMode.START))
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("test\\%")
        }
    }

    // --- LikeMode.END with special characters ---

    @Test
    fun testLikeEndWithSpecialChars() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name.like("_end", LikeMode.END))
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ? escape '\'""".trimMargin()
            )
            variables("%\\_end")
        }
    }

    // --- Empty pattern: no escape clause ---

    @Test
    fun testLikeEmptyPattern() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ?""".trimMargin()
            )
            variables("")
        }
    }

    // --- Single space: wrapped with % ---

    @Test
    fun testLikeSingleSpace() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like " ")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ?""".trimMargin()
            )
            variables("% %")
        }
    }

    // --- Multiple consecutive spaces: wrapped with % ---

    @Test
    fun testLikeMultipleSpaces() {
        executeAndExpect(
            sqlClient.createQuery(Book::class) {
                where(table.name like "   ")
                select(table)
            }
        ) {
            sql(
                """select tb_1_.ID, tb_1_.NAME, tb_1_.EDITION, tb_1_.PRICE, tb_1_.STORE_ID 
                    |from BOOK tb_1_ 
                    |where tb_1_.NAME like ?""".trimMargin()
            )
            variables("%   %")
        }
    }
}

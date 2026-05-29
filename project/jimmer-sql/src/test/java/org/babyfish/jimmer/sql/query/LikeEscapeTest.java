package org.babyfish.jimmer.sql.query;

import org.babyfish.jimmer.sql.common.AbstractQueryTest;
import org.babyfish.jimmer.sql.model.BookTable;
import org.junit.jupiter.api.Test;

public class LikeEscapeTest extends AbstractQueryTest {

    // --- No special characters: no escape clause ---

    @Test
    public void testLikeWithoutSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("GraphQL"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ?"
                    );
                    ctx.variables("%GraphQL%");
                }
        );
    }

    @Test
    public void testIlikeWithoutSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().ilike("graphql"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME ilike ?"
                    );
                    ctx.variables("%graphql%");
                }
        );
    }

    // --- Underscore: should be escaped ---

    @Test
    public void testLikeWithUnderscore() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("test_1"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%test\\_1%");
                }
        );
    }

    // --- Percent: should be escaped ---

    @Test
    public void testLikeWithPercent() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("100%"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%100\\%");
                }
        );
    }

    @Test
    public void testLikeWithPercentInMiddle() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("100%off"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%100\\%off%");
                }
        );
    }

    // --- Backslash: should be escaped ---

    @Test
    public void testLikeWithBackslash() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("a\\b"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%a\\\\b%");
                }
        );
    }

    // --- Mixed special characters ---

    @Test
    public void testLikeWithMixedSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("test_100%\\end"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%test\\_100\\%\\\\end%");
                }
        );
    }

    // --- ilike with special characters ---

    @Test
    public void testIlikeWithUnderscore() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().ilike("test_1"))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME ilike ? escape '\\'"
                    );
                    ctx.variables("%test\\_1%");
                }
        );
    }

    // --- not like with special characters ---

    @Test
    public void testNotLikeWithUnderscore() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(org.babyfish.jimmer.sql.ast.Predicate.not(table.name().like("test_1")))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME not like ? escape '\\'"
                    );
                    ctx.variables("%test\\_1%");
                }
        );
    }

    // --- LikeMode.EXACT with special characters ---

    @Test
    public void testLikeExactWithSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("test_1", org.babyfish.jimmer.sql.ast.LikeMode.EXACT))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("test\\_1");
                }
        );
    }

    // --- LikeMode.START with special characters ---

    @Test
    public void testLikeStartWithSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("test%", org.babyfish.jimmer.sql.ast.LikeMode.START))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("test\\%");
                }
        );
    }

    // --- LikeMode.END with special characters ---

    @Test
    public void testLikeEndWithSpecialChars() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("_end", org.babyfish.jimmer.sql.ast.LikeMode.END))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ? escape '\\'"
                    );
                    ctx.variables("%\\_end");
                }
        );
    }

    // --- Empty pattern: no escape clause ---

    @Test
    public void testLikeEmptyPattern() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like(""))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ?"
                    );
                    ctx.variables("");
                }
        );
    }

    // --- Single space: wrapped with % ---

    @Test
    public void testLikeSingleSpace() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like(" "))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ?"
                    );
                    ctx.variables("% %");
                }
        );
    }

    // --- Multiple consecutive spaces: wrapped with % ---

    @Test
    public void testLikeMultipleSpaces() {
        BookTable table = BookTable.$;
        executeAndExpect(
                getSqlClient().createQuery(table)
                        .where(table.name().like("   "))
                        .select(table.name()),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NAME " +
                                    "from BOOK tb_1_ " +
                                    "where tb_1_.NAME like ?"
                    );
                    ctx.variables("%   %");
                }
        );
    }
}

package nosorae.changed_name.p12_book

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import nosorae.changed_name.p12_book.dao.BookReviewDao
import nosorae.changed_name.p12_book.dao.BookSearchHistoryDao
import nosorae.changed_name.p12_book.model.BookReview
import nosorae.changed_name.p12_book.model.BookSearchHistory

// DAO란 Data Access Object의 약어로서 실질적으로 DB에 접근하는 객체를 말한다

@Database(entities = [BookSearchHistory::class, BookReview::class], version = 2)
abstract class BookAppDatabase: RoomDatabase() {
    abstract fun historyDao(): BookSearchHistoryDao
    abstract fun reviewDao(): BookReviewDao
}

fun getAppDatabase(context: Context): BookAppDatabase {
    val migration_1_2 = object : Migration(1,2) {
        // 데이터베이스에 직접 쿼리문을 작성해서 어떻게 어떤 새로운 데이터테이블을 바꿨는지 알려줘야한다.
        // 버전 1에서 6까지 가려면 1부터 2, 3, 4... 6 이렇게 순차적으로 마이그레이션 해줘야한다.
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'BOOKREVIEW' ('id' INTEGER, 'review' TEXT," + "PRIMARY KEY('id'))")
        }
    }
    return Room.databaseBuilder(
        context,
        BookAppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}
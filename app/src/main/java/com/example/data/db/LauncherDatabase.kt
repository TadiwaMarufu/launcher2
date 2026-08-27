package com.example.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "launcher_notes")
data class LauncherNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_customizations")
data class AppCustomizationEntity(
    @PrimaryKey val packageName: String,
    val customLabel: String? = null,
    val customCategory: String? = null,
    val customColorHex: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val usageCount: Int = 0
)

@Dao
interface LauncherDao {
    @Query("SELECT * FROM launcher_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<LauncherNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: LauncherNoteEntity): Long

    @Query("DELETE FROM launcher_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Query("SELECT * FROM app_customizations")
    fun getAllAppCustomizations(): Flow<List<AppCustomizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppCustomization(customization: AppCustomizationEntity)

    @Query("UPDATE app_customizations SET usageCount = usageCount + 1 WHERE packageName = :packageName")
    suspend fun incrementUsage(packageName: String)
}

@Database(entities = [LauncherNoteEntity::class, AppCustomizationEntity::class], version = 1, exportSchema = false)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun launcherDao(): LauncherDao
}

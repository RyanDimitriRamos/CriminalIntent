package com.dimitriusramos.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.dimitriusramos.criminalintent.database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

/* Crime Repository is a singleton. Which means that there will only ever be on instance of
 * it in the application
 */
class CrimeRepository private constructor(context: Context) {

    private val database : CrimeDatabase = Room.databaseBuilder(context.applicationContext, CrimeDatabase::class.java, DATABASE_NAME).build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor() //used to explicitly execute DAO calls on a background thread
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)
    companion object{
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }
        fun get(): CrimeRepository{
            return INSTANCE ?:
                    throw IllegalStateException("CrimeRepository must be initialized")

        }
    }
    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }
}
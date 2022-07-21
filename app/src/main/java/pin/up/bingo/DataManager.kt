package pin.up.bingo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataManager (var context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "${context.resources.getString(R.string.app_name)}_data")

    object PreferenceKeys {
        val LINK_KEY = stringPreferencesKey("a")
        val VISITOR_KEY = stringPreferencesKey("b")
        val USER_KEY = booleanPreferencesKey("c")
    }

    suspend fun setData(data : DataPref){
        context.datastore.edit { preferences ->
            preferences[PreferenceKeys.USER_KEY] = data.user!!
            preferences[PreferenceKeys.VISITOR_KEY] = if (data.visitor != null) data.visitor!! else ""
            preferences[PreferenceKeys.LINK_KEY] = if (data.link != null) data.link!! else ""
        }
    }

    private val userFlow: Flow<Boolean?> = context.datastore.data
        .map { preferences ->
            preferences[PreferenceKeys.USER_KEY]
        }

    private val visitorFlow: Flow<String?> = context.datastore.data
        .map { preferences ->
            preferences[PreferenceKeys.VISITOR_KEY]
        }

    private val linkFlow: Flow<String?> = context.datastore.data
        .map { preferences ->
            preferences[PreferenceKeys.LINK_KEY]
        }


    suspend fun getData() : DataPref?{
        val visitor =  visitorFlow.first()
        val link =  linkFlow.first()
        val user = userFlow.first()
        if (user != null || link != null)
            return DataPref(link, visitor, user)
        return null
    }
}
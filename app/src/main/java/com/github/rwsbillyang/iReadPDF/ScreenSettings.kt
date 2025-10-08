package com.github.rwsbillyang.iReadPDF


import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.DropDownPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    val viewModel: MyViewModel = LocalViewModel.current
    val ctx =  LocalContext.current
    val scope = rememberCoroutineScope()
    val groupName = stringResource(id = R.string.settings)
    PrefsScreen(dataStore = LocalContext.current.dataStore, modifier = Modifier.padding(paddingValues)) {

        prefsGroup(groupName) {
            prefsItem{
                CheckBoxPref(key = AppConstants.SettingsKey.EnterBookDirectly,
                    title = stringResource(id = R.string.enter_book),
                    summary = stringResource(id = R.string.enter_book_desc),
                    defaultChecked = false,
                    onCheckedChange = {v-> viewModel.enterBookDirectly},
                    enabled = true)
            }

            prefsItem{
                TextPref(
                    title = stringResource(id = R.string.clear_book_cache),
                    summary =stringResource(id = R.string.clear_all_book_cache_desc),
                    onClick = {
                        scope.launch { CacheManager.delAllCachedPages(ctx) }
                    }
                )
            }
        }
    }
}
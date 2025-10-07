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
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.DropDownPref

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class PdfQuality(val id: Int){Low(R.string.quality_l), Middle(R.string.quality_m), High(R.string.quality_h),}

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    val viewModel: MyViewModel = LocalViewModel.current
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
                DropDownPref(
                    key = AppConstants.SettingsKey.Quality,
                    title = stringResource(id = R.string.quality),
                    useSelectedAsSummary = true,
                    entries = PdfQuality.values().associate { Pair(it.name, stringResource(id = it.id)) },
                    defaultValue = PdfQuality.Middle.name,
                    onValueChange = {
                        viewModel.quality.value = PdfQuality.valueOf(it)
                    }
                )
            }
        }
    }
}
package com.github.rwsbillyang.iReadPDF


import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.rwsbillyang.iReadPDF.db.PdfQuality
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import com.github.rwsbillyang.iReadPDF.ui.theme.ThemeEnum
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
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val groupName = stringResource(id = R.string.settings)
    Box(Modifier.padding(paddingValues).fillMaxSize(), Alignment.Center){

        PrefsScreen(dataStore = LocalContext.current.dataStore) {
            prefsGroup(groupName) {
                prefsItem{
                    CheckBoxPref(key = AppConstants.SettingsKey.EnterBookDirectly,
                        title = stringResource(id = R.string.enter_book),
                        summary = stringResource(id = R.string.enter_book_desc),
                        textColor = MaterialTheme.colorScheme.primary,
                        defaultChecked = false,
                        onCheckedChange = {v-> viewModel.enterBookDirectly = v},
                        enabled = true)
                }

                prefsItem{
                    CheckBoxPref(key = AppConstants.SettingsKey.DisableMovePdf,
                        title = stringResource(id = R.string.disable_move_pdf),
                        summary = stringResource(id = R.string.disable_move_pdf_desc),
                        textColor = MaterialTheme.colorScheme.primary,
                        defaultChecked = true,
                        onCheckedChange = {v-> viewModel.disableMovePdf = v},
                        enabled = true)
                }
                prefsItem {
                    val minutes = stringResource(id = R.string.minutes)
                    val minute = stringResource(id = R.string.minute)
                    DropDownPref(
                        key = AppConstants.SettingsKey.KeepScreenOn,
                        title = stringResource(id = R.string.keep_screen_on),
                        textColor = MaterialTheme.colorScheme.primary,
                        useSelectedAsSummary = true,
                        entries = mapOf(
                            "0" to stringResource(id = R.string.follow_system),
                            "1" to "1 $minute",
                            "2" to "2 $minutes",
                            "3" to "3 $minutes",
                            "4" to "4 $minutes",
                            "5" to "5 $minutes",
                            "10" to "10 $minutes",
                            "20" to "20 $minutes",
                            "30" to "30 $minutes"
                        ),
                        defaultValue = "0",
                        onValueChange = {
                            viewModel.screenOn.value = it.toInt()
                        }
                    )
                }
                prefsItem {
                    DropDownPref(
                        key = AppConstants.SettingsKey.Theme,
                        title = stringResource(id = R.string.theme),
                        textColor = MaterialTheme.colorScheme.primary,
                        useSelectedAsSummary = true,
                        entries = ThemeEnum.values()
                            .associate { Pair(it.name, stringResource(id = it.resId)) },
                        defaultValue = ThemeEnum.Default.name,
                        onValueChange = {
                            viewModel.theme.value = ThemeEnum.valueOf(it)
                        }
                    )
                }

                prefsItem{
                    val done = stringResource(R.string.done)
                    TextPref(
                        title = stringResource(id = R.string.clear_book_cache),
                        summary = stringResource(id = R.string.clear_all_book_cache_desc),
                        textColor = MaterialTheme.colorScheme.primary,
                        enabled = true,
                        onClick = {
                            scope.launch {
                                CacheManager.delAllCachedPages(ctx)
                                snackState.showSnackbar(done,duration = SnackbarDuration.Short)
                            }
                        }
                    )
                }
            }
        }

        SnackbarHost(hostState = snackState){
            Snackbar(snackbarData = it, Modifier.wrapContentSize(Alignment.Center),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}
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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    val viewModel: MyViewModel = LocalViewModel.current
    val scope = rememberCoroutineScope()
    val groupName = stringResource(id = R.string.settings)
    PrefsScreen(dataStore = LocalContext.current.dataStore, modifier = Modifier.padding(paddingValues)) {

//        prefsGroup(groupNameYun) {
//            prefsItem{
//                CheckBoxPref(key = SettingsKey.XiaoXian,
//                    title = "小限",
//                    summary = "是否展示小限运盘",
//                    defaultChecked = false,
//                    onCheckedChange = {v-> viewModel.enableXiaoXianYun.value = v},
//                    enabled = true)
//            }
//            prefsItem{
//                DropDownPref(
//                    key = SettingsKey.LeapModeLiuPan,
//                    title = "流盘闰月",
//                    useSelectedAsSummary = true,
//                    entries = LunarLeapMonthAdjustMode.values().associate { Pair(it.name, it.label) },
//                    defaultValue = LunarLeapMonthAdjustMode.Half.name,
//                    onValueChange = {
//                        viewModel.leapModeMonthLiuPan.value = LunarLeapMonthAdjustMode.valueOf(it)
//                    }
//                )
//            }
//        }
    }
}
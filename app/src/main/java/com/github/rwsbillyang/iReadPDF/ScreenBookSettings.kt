package com.github.rwsbillyang.iReadPDF

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.rwsbillyang.composerouter.ScreenCall
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.PdfQuality
import com.github.rwsbillyang.iReadPDF.db.db
import com.github.rwsbillyang.iReadPDF.pdfview.CacheManager
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.DropDownPref
import com.jamal.composeprefs3.ui.prefs.EditTextPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Long.utcToLocalDateTime(zoneOffset: ZoneOffset = ZoneOffset.UTC): LocalDateTime = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(this),zoneOffset)


/**
 * @Deprecated("wrong to use PrefsScreen, because it's for Preference using dataStore as backend")
 * */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScreenBookSettings(call: ScreenCall) {
    val book: Book? = call.props as? Book
    if(book == null){
        Column(Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
            Text("no book")
        }
        return
    }

    val scope = rememberCoroutineScope()
    val ctx =  LocalContext.current
    val dao = db(ctx).dao()
    val groupNameRender = stringResource(id = R.string.book_settings_render)
    val groupNameOperation = stringResource(id = R.string.book_settings_operation)
    val groupNameInfo = stringResource(id = R.string.book_settings_info)
    PrefsScreen(dataStore = LocalContext.current.dataStore, modifier = Modifier.padding(call.scaffoldPadding)) {
        prefsGroup(groupNameRender) {
            prefsItem {
                CheckBoxPref(
                    key = AppConstants.BookSettingsKey.DarkMode,
                    title = stringResource(id = R.string.disable_dark_mode),
                    summary = stringResource(id = R.string.disable_dark_mode_desc),
                    defaultChecked = false,
                    onCheckedChange = { v ->
                        if (v){
                            book.disableDarkMode = 1
                        }else {
                            book.disableDarkMode = 0
                        }
                        scope.launch { dao.updateOne(book) }
                    },
                    enabled = true
                )
            }
            prefsItem {
                DropDownPref(
                    key = AppConstants.BookSettingsKey.Quality,
                    title = stringResource(id = R.string.quality),
                    useSelectedAsSummary = true,
                    entries = PdfQuality.values()
                        .associate { Pair(it.name, stringResource(id = it.id)) },
                    defaultValue = PdfQuality.Middle.name,
                    onValueChange = {
                        book.quality = it
                        scope.launch {
                            dao.updateOne(book)
                            CacheManager.delCachedPages(ctx, book.id)
                        }
                    }
                )
            }
        }

        prefsGroup(groupNameInfo) {
            prefsItem{
                CheckBoxPref(key = AppConstants.BookSettingsKey.Cover,
                    title = stringResource(id = R.string.first_page_cover),
                    summary = stringResource(id = R.string.first_page_cover_desc),
                    defaultChecked = false,
                    onCheckedChange = {v->
                        book.hasCover = if(v) 1 else 0
                        scope.launch {
                            dao.updateOne(book)
                            if(!v) CacheManager.delCover(ctx, book.id)//delete cover file
                        }
                    },
                    enabled = true)
            }
            prefsItem{
                EditTextPref(key = AppConstants.BookSettingsKey.Name,
                    title = stringResource(id = R.string.book_name),
                    summary = book.name?:"",
                    dialogTitle= stringResource(id = R.string.book_name),
                    defaultValue = book.name,
                    onValueChange = {
                        book.name = it
                        scope.launch { dao.updateOne(book) }
                    })
            }
            prefsItem{
                EditTextPref(key = AppConstants.BookSettingsKey.Author,
                    title = stringResource(id = R.string.book_author),
                    summary = book.author?:"",
                    dialogTitle= stringResource(id = R.string.book_author),
                    defaultValue = book.author?:"",
                    onValueChange = {
                        book.author = it
                        scope.launch { dao.updateOne(book) }
                    })
            }
            prefsItem{
                EditTextPref(key = AppConstants.BookSettingsKey.Publisher,
                    title = stringResource(id = R.string.book_publisher),
                    summary = book.publisher?:"",
                    dialogTitle= stringResource(id = R.string.book_publisher),
                    defaultValue = book.publisher?:"",
                    onValueChange = {
                        book.publisher = it
                        scope.launch { dao.updateOne(book) }
                    })
            }
            prefsItem{
                TextPref(
                    title = stringResource(id = R.string.last_open),
                    summary = book.lastOpen.utcToLocalDateTime(ZoneOffset.ofHours(8)).format(AppConstants.formatter)
                  )
            }
            prefsItem{
                TextPref(
                    title = stringResource(id = R.string.reading_progress),
                    summary = "${book.page} / ${book.total}"
                )
            }
        }

        prefsGroup(groupNameOperation) {
            prefsItem{
                TextPref(
                    title = stringResource(id = R.string.clear_book_cache),
                    summary =stringResource(id = R.string.clear_book_cache_desc),
                    onClick = {
                        scope.launch { CacheManager.delCachedPages(ctx, book.id) }
                    }
                )
            }
        }
    }
}
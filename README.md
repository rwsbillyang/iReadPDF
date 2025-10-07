# iReadPDF
An Android PDFViewer app which can restore reading state of last time based on PdfRenderer and pure Jetpack Compose, not using any traditional Android View. 

## Background
It is usually impossible to read a book continuously due to the interference of various affairs or activities. 

So when I read PDF books, I hope to restore the last reading state, including page position, scroll position,  zoom level, portrait or landscape, just for using the fragmented time as fully as possible. 

Then iReadPDF app is born.

## Features
- Completely use Jetpack Compose UI
- Small apk size using native Android API, not use any 3rd library
- Support large pdf file because not load all file into memory


### BookShelf
- Add/remove books into/from shelf
- extract first page as cover, unset cover
- enable/disable dark mode for every book

### PDFView
 remember and restore the following states:
- zoom/move
- page
- page offset
- landscape/portrait
- fullScreen
- go to page

### Settings
- whether enter the book of last reading time directly
- choose pdf quality: High, Middle, Low

## TODO
- PDF Content
- Jump according to content
- Text selection and copy
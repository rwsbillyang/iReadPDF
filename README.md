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
- Extract first page as cover, unset cover
- Enable/disable dark mode for every pdf book
- Support choose quality for every pdf book

### PDFView
- Jump to the specified page
- rotation/no rotation of page
- Full screen
- Remember and restore the following states of every pdf book:
  - zoom
  - enable/disable move pdf
  - page
  - page offset
  - rotation/no rotation
  - fullScreen


### Settings
- Can set whether enter the book of last reading time directly
- Keeping screen on can be set
- Support color scheme

## TODO
- Show DF Content
- Jump when click item in content
- Text selection and copy
- Not copy PDF file when add book into shelf


## Problems
- Not smooth when scroll pdf in landscape mode
- The user experience of move pdf view is not good
- In dark mode, some text font colors in toolbar of pdfview takes not effect
# Sudoku

This is an Android Sudoku game. It should work on all versions of Android starting from KitKat (4.4).

If you trust me feel free to download and install the .apk file.
You can also take the source as a basis to build your own game.

If you have any questions or remarks please send me a mail on ds21h@hotmail.com

History

Version 1.4 - 08-04-2023
-   Upgraded to API 33 (Android 13)
-   Generate now starts 3 parallel tasks. The result of the first finished task is used and the remaining tasks are canceled.
    This is because generation is a cpu-intensive task that takes sometimes a long time.
-   Screen is kept alive. Auto-sleep is disabled.

Version 1.3.3 - 25-06-2022
-   Reset made possible on a finished game

Version 1.3.2 - 13-04-2022
-   Bugfix: Difficulty for 'Medium' displayed as 'Hard'

Version 1.3.1 - 21-10-2021
-   Upgraded to API 31 (Android 12)
-   Introduced Combine function to find equal cells in two playfields
-   Introduced Reset function to reset a game completely

Version 1.3 - 31-07-2021
-   Deleted the 'new' menu option as it was never used.
-   Renamed 'Generate' menu option to 'new'
-   Replaced AsyncTask by Runnable in a threadpool
-   Introduced threadpool on application level
-   Changed all thread uses to threadpool

Version 1.2.1 - 18-02-2021
-   Introduced transactions on the DB. The multi-threaded approach introduced in Version 1.2 sometimes caused problems with simultaneous access.

Version 1.2 - 05-11-2020
-   Minimum API level changed to 19 (Android 4.4 - KitKat)
-   Changed compatibility libraries to AndroidX
-   Split SudokuGame in two Classes:
    - SudokuGameBase which has all the playing functionality
    - SudokuGame which inherits SudokuGameBase and has all the multi-Playfield functionality
-   Moved the save action from onPause to onStop
-   Moved the restore action from onResume to onStart
-   Save actions are now processed on a different thread in order to enhance responsiveness
-   AutoPencil mode can be turned off
-   When auto pencil is enabled and a cellselection is changed or erased (not added!) all pencilentries are erased as they then become unreliable.
-   Exception: When an empty cell is filled and marked as erroneous en then erased the pencil entries are not erased.

Version 1.1 - 30-01-2020
-   Multiple playing fields
    - Extra menu option to manage PlayFields
    - Functions: Clone current PlayField in a new one, Switch between PlayFields and Delete current Playfield
    - Playfield 0 cannot be deleted
    - If multiple playfields exist an indicator is included under the playfield.

Version 1.0 - 09-03-2019
-   Complete game
-   two ways to create games:
    -   Generator  
        Really creates random game  
        Difficulty only very approximative  
        Very cpu intensive so can take some time (especially in the harder levels)
    -   Library  
        Uses previously stored game as seed
-   Has Setup mode to enter game from other source
-   Can store manually entered or generated games to use as seed
-   Has solver
-   User interface in English and Dutch

Version 0.1 - 13-02-2019
-	First stable version

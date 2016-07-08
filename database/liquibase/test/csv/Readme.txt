note that if exporting from PL/SQL dev you must make sure:
- to select csv and then ANSI csv as the format
- to replace all ";" with ","
- to replace all "NULL" with "NULL"
- to replace all ,000000" with " (date format)
- make sure that dates are formated as iso formate (see http://www.liquibase.org/documentation/changes/load_data.html)
 (replace ("\d\d\d\d)/(\d\d)/(\d\d)( \d\d:\d\d:\d\d") with $1-$2-$3$4)

the followin regx helps find the 4 audit fields
,"[^"]*?","[^"]*?","[^"]*?","[^"]*?"$

notes:
http://stackoverflow.com/questions/16890723/list-all-liquibase-sql-types/16894478#16894478

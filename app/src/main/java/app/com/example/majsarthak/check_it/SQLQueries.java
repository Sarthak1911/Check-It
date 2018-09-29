package app.com.example.majsarthak.check_it;


class SQLQueries
{
    String addTable(String table_name, String id, String todo_name, String checked, String createdAt)
    {
        /*
        Attributes in ToDos
        id INTEGER AUTOINCREMENT Primary Key,
        todo_name VARCHAR(25),
        checked INTEGER DEFAULT 0,
        createdAt TEXT

     */

        return (
                " CREATE TABLE " + table_name +
                " ( "
                        + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + todo_name + " VARCHAR(25) NOT NULL, "
                        + checked + " INTEGER DEFAULT 0, "
                        + createdAt + " VARCHAR " +
                " ); "
        );
    }

    String dropTableIf(String table_name)
    {
        return " DROP TABLE IF EXISTS " + table_name;
    }

    String getAllForToday(String table_name, String currentDate)
    {
        return "SELECT * FROM " + table_name + " WHERE createdAt = ' " + currentDate + " ' ORDER BY checked ; " ;
    }

    String addTodo(String table_name, String todo_name, String date)
    {
        return (
                " INSERT INTO " + table_name + "(id, todo_name, checked, createdAt)" + " VALUES( ?, '" + todo_name + " ', " + 0 + " ,' "+ date + " '); "
                );
    }

    String getLastIdItems(String table_name)
    {
        return (" SELECT * FROM " + table_name + " WHERE id = (SELECT MAX(id) FROM " + table_name + ");" );
    }

    String deleteSelected(String table_name, int id)
    {
        return (" DELETE FROM " + table_name + " WHERE id = " + id);
    }

    String setChecked(String table_name, int checked, int id)
    {
        return ("UPDATE " + table_name + " SET checked = " + checked + " WHERE id = " + id);
    }

    String getChecked(String table_name)
    {
        return (" SELECT * FROM " + table_name + " WHERE checked = 1 ");
    }

    String getUnChecked(String table_name)
    {
        return (" SELECT * FROM " + table_name + " WHERE checked = 0 ");
    }

    String getUnCheckedToday(String table_name, String currentDate)
    {
        return (" SELECT * FROM " + table_name + " WHERE checked = 0 AND createdAt LIKE '%" + currentDate + "%'");
    }
}

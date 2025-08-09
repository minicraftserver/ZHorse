package com.github.zedd7.zhorse.database;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.DatabaseEnum;

public class MariaDBImporter extends SQLDatabaseImporter {

        public static boolean importData(ZHorse zh) {
                if (zh.getCM().getDatabaseType() != DatabaseEnum.MARIADB) {
                        SQLDatabaseConnector db = new MariaDBConnector(zh);
                        return fullImport(zh, db);
                }
                else {
                        zh.getLogger().severe("Data import from MariaDB to MariaDB is not supported, please use a different database !");
                }
                return false;
        }

}

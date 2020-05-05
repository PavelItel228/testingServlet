package com.kpi.testing.dao.impl;

import com.kpi.testing.dao.ArchiveDAO;
import com.kpi.testing.dao.DaoFactory;
import com.kpi.testing.dao.ReportDAO;
import com.kpi.testing.dao.UserDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCDaoFactory extends DaoFactory {
    private  Connection connection;
    @Override
    public UserDAO createUserDao() {
        return new JDBCUserDAO(getConnection());
    }
    @Override
    public ReportDAO createReportDao() {
        return new JDBCReportDAO(getConnection());
    }

    @Override
    public ArchiveDAO createArchiveDao() {
        return new JDBCArchiveDAO(getConnection());
    }



    private Connection getConnection(){
        if (connection == null) {
            synchronized (JDBCDaoFactory.class) {
                if (connection == null){
                    try {
                        connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/testing?serverTimezone=UTC",
                                "pavlo" ,
                                "grib1111" );
                        connection.prepareStatement("SET @@SESSION.information_schema_stats_expiry = 0;").executeQuery();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return connection;
    }
}

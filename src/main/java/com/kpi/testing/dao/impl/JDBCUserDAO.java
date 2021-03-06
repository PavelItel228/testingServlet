package com.kpi.testing.dao.impl;

import com.kpi.testing.dao.UserDAO;
import com.kpi.testing.entity.Report;
import com.kpi.testing.entity.User;
import com.kpi.testing.entity.enums.Role;
import com.kpi.testing.entity.enums.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JDBCUserDAO implements UserDAO {
    private final DataSource ds;

    public JDBCUserDAO(DataSource ds) {
        this.ds = ds;
    }

    public static User extractUser(ResultSet rs) throws SQLException {
        try {
            return User.builder()
                    .id(rs.getLong("usr.id"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .password(rs.getString("password"))
                    .created(rs.getDate("usr.created").toLocalDate())
                    .updated(rs.getDate("usr.updated").toLocalDate())
                    .role(Role.valueOf(rs.getString("role")))
                    .status(Status.valueOf(rs.getString("usr.status")))
                    .build();
        } catch (NullPointerException npe){
            return new User();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> user;
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT *  FROM usr " +
                "left join report_inspectors " +
                "on usr.id = usr_id " +
                "left join reports " +
                "on usr.id = reports.owner_id or reports.id = report_id " +
                "where username=?")) {
            ps.setString(1, username);
            ResultSet rs1 = ps.executeQuery();
            user = getNewUser(rs1);
            Map<Long, Report> reportsOwned = new HashMap<>();
            Map<Long, Report> reportsInspected = new HashMap<>();
            while (rs1.next()) {
                Report report = JDBCReportDAO.extractReport(rs1);
                if (isUniqReport(reportsInspected, report)
                        && rs1.getLong("report_id") == rs1.getLong("reports.id")) {
                    reportsInspected.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsInspected().add(report);
                }
                if (isUniqReport(reportsOwned, report)
                        && rs1.getLong("owner_id") == rs1.getLong("usr.id")) {
                    reportsOwned.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsOwned().add(report);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private boolean isUniqReport(Map<Long, Report> reports, Report report) {
        return !reports.containsKey(report.getId());
    }

    private User makeUniqueUser(Map<Long, User> users, User user) {
        users.putIfAbsent(user.getId(), user);
        return users.get(user.getId());
    }


    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> user;
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                "SELECT *  FROM usr " +
                "left join report_inspectors " +
                "on usr.id = usr_id " +
                "left join reports " +
                "on usr.id = reports.owner_id or reports.id = report_id " +
                "where email=? ")) {
            ps.setString(1, email);
            ResultSet rs1 = ps.executeQuery();
            user = getNewUser(rs1);
            Map<Long, Report> reportsOwned = new HashMap<>();
            Map<Long, Report> reportsInspected = new HashMap<>();
            while (rs1.next()) {
                Report report = JDBCReportDAO.extractReport(rs1);
                if (isUniqReport(reportsInspected, report)
                        && rs1.getLong("report_id") == rs1.getLong("reports.id")) {
                    reportsInspected.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsInspected().add(report);
                }
                if (isUniqReport(reportsOwned, report)
                        && rs1.getLong("owner_id") == rs1.getLong("usr.id")) {
                    reportsOwned.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsOwned().add(report);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return user;
    }

    @Override
    public List<User> findAllByRole(Role role) {
        List<User> result;
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                "SELECT *  FROM usr " +
                "left join report_inspectors " +
                "on usr.id = usr_id " +
                "left join reports " +
                "on usr.id = reports.owner_id or reports.id = report_id " +
                "where usr.role = ?")) {
            ps.setString(1, role.name());
            ResultSet rs1 = ps.executeQuery();
            Map<Long, Report> reportsOwned = new HashMap<>();
            Map<Long, User> users = new HashMap<>();
            Map<Long, Report> reportsInspected = new HashMap<>();
            while (rs1.next()) {
                User user = makeUniqueUser(users, extractUser(rs1));
                Report report = JDBCReportDAO.extractReport(rs1);
                if (isUniqReport(reportsInspected, report)
                        && rs1.getLong("report_id") == rs1.getLong("reports.id")) {
                    reportsInspected.putIfAbsent(report.getId(), report);
                    user.getReportsInspected().add(report);
                }
                if (isUniqReport(reportsOwned, report)
                        && rs1.getLong("owner_id") == rs1.getLong("usr.id")) {
                    reportsOwned.putIfAbsent(report.getId(), report);
                    user.getReportsOwned().add(report);
                }
            }
            result = new ArrayList<>(users.values());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void create(User entity) {
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement
                ("INSERT INTO usr " +
                        "(`username`, `email`, `password`, `role`, `status`, `updated`, `created`)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getRole().name());
            ps.setString(5, entity.getStatus().name());
            ps.setString(6, LocalDate.now().toString());
            ps.setString(7, LocalDate.now().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> user;
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT *  FROM usr " +
                "left join report_inspectors " +
                "on usr.id = usr_id " +
                "left join reports " +
                "on usr.id = reports.owner_id or reports.id = report_id " +
                "where usr.id=?")) {
            ps.setLong(1, id);
            ResultSet rs1 = ps.executeQuery();
            user = getNewUser(rs1);
            Map<Long, Report> reportsOwned = new HashMap<>();
            Map<Long, Report> reportsInspected = new HashMap<>();
            while (rs1.next()) {
                Report report = JDBCReportDAO.extractReport(rs1);
                if (isUniqReport(reportsInspected, report)
                        && rs1.getLong("report_id") == rs1.getLong("reports.id")) {
                    reportsInspected.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsInspected().add(report);
                }
                if (isUniqReport(reportsOwned, report)
                        && rs1.getLong("owner_id") == rs1.getLong("usr.id")) {
                    reportsOwned.putIfAbsent(report.getId(), report);
                    user.orElse(new User()).getReportsOwned().add(report);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    private Optional<User> getNewUser(ResultSet rs) throws SQLException {
        Optional<User> user = (rs.next()) ? Optional.of(extractUser(rs)) : Optional.empty();
        rs.previous();
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> result;
        try (   Connection connection = ds.getConnection();
                Statement ps = connection.createStatement()) {

            ResultSet rs1 = ps.executeQuery(
                    "SELECT *  FROM usr " +
                            "left join report_inspectors " +
                            "on usr.id = usr_id " +
                            "left join reports " +
                            "on usr.id = reports.owner_id or reports.id = report_id "
            );
            Map<Long, Report> reportsOwned = new HashMap<>();
            Map<Long, User> users = new HashMap<>();
            Map<Long, Report> reportsInspected = new HashMap<>();
            while (rs1.next()) {
                User user = makeUniqueUser(users, extractUser(rs1));
                Report report = JDBCReportDAO.extractReport(rs1);
                if (isUniqReport(reportsInspected, report)
                        && rs1.getLong("report_id") == rs1.getLong("reports.id")) {
                    reportsInspected.putIfAbsent(report.getId(), report);
                    user.getReportsInspected().add(report);
                }
                if (isUniqReport(reportsOwned, report)
                        && rs1.getLong("owner_id") == rs1.getLong("usr.id")) {
                    reportsOwned.putIfAbsent(report.getId(), report);
                    user.getReportsOwned().add(report);
                }
            }
            result = new ArrayList<>(users.values());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void update(User entity) {
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement
                ("Update usr set status = ?, username = ?, email = ?, password = ?, role = ?, updated = ?" +
                        "where id = ?")) {
            ps.setString(1, entity.getStatus().name());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getPassword());
            ps.setString(5, entity.getRole().name());
            ps.setString(7, LocalDate.now().toString());
            ps.setLong(8, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        try (   Connection connection = ds.getConnection();
                PreparedStatement ps = connection.prepareStatement("Update usr set status = 'Deleted' where id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

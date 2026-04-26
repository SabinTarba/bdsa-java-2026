import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {

    public static Optional<Account> findByIban(String iban) throws SQLException {
        String sql = "SELECT id, iban, owner_name, balance, currency, status, created_at FROM accounts WHERE iban = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, iban);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getLong("id"));
                    acc.setIban(rs.getString("iban"));
                    acc.setOwnerName(rs.getString("owner_name"));
                    acc.setBalance(rs.getDouble("balance"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setStatus(rs.getString("status"));
                    acc.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    return Optional.of(acc);
                }
            }

            return Optional.empty();
        }
    }

    public static List<Account> findByStatus(String status) throws SQLException {
        String sql = "SELECT id, iban, owner_name, balance, currency, status, created_at FROM accounts WHERE status = ?";

        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, status);

            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getLong("id"));
                    acc.setIban(rs.getString("iban"));
                    acc.setOwnerName(rs.getString("owner_name"));
                    acc.setBalance(rs.getDouble("balance"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setStatus(rs.getString("status"));
                    acc.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    accounts.add(acc);
                }
            }

            return accounts;
        }
    }

    public static boolean block(long accountId) throws SQLException {
        String sql = "UPDATE accounts SET status = 'BLOCKED' WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(long accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ? AND balance = 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    public static List<Account> getActiveAccounts() throws SQLException {
        String sql = "{ call get_active_accounts(?) }";

        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql))
        {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getLong("id"));
                    acc.setIban(rs.getString("iban"));
                    acc.setOwnerName(rs.getString("owner_name"));
                    acc.setBalance(rs.getDouble("balance"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setStatus(rs.getString("status"));
                    acc.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    accounts.add(acc);
                }
            }
        }

        return accounts;
    }

    public static int countTransactions(long accountId) throws SQLException {
        String sql = "{ ? = call count_transactions(?) }";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql))
        {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, accountId);
            cs.execute();

            return cs.getInt(1);
        }
    }
}

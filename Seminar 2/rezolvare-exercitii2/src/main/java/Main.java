import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static List<Account> findAll() throws SQLException {
        String sql = "SELECT id, iban, owner_name, balance, currency, status, created_at FROM accounts";

        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
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
            return accounts;
        }
    }

    public static Optional<Account> findById(long id) throws SQLException {
        String sql = "SELECT id, iban, owner_name, balance, currency, status, created_at FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id); // primul ? devine valoarea id

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

    public static long insert(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (iban, owner_name, balance, currency) VALUES (?, ?, ?, ?)";
        String[] generatedColumns = {"ID"};

        // in generatedColumns putem prelua ID-urie inapoi prin mecanismul de mai jos generatedKeys

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, generatedColumns))
        {
            ps.setString(1, account.getIban());
            ps.setString(2, account.getOwnerName());
            ps.setDouble(3, account.getBalance());
            ps.setString(4, account.getCurrency());
            int rowsAffected = ps.executeUpdate();
            System.out.println("Randuri inserate: " + rowsAffected);

            // preluare ID generat automat
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }

        throw new SQLException("INSERT esuat, nu s-a generat niciun ID.");
    }

    public static boolean updateBalance(long accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setDouble(1, newBalance);
            ps.setLong(2, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTransaction(long transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, transactionId);

            return ps.executeUpdate() >0;
        }
    }

    public static String transferFunds(long fromId, long toId, double amount) throws SQLException {
        String sql = "{ call transfer_funds(?, ?, ?, ?) }";

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql))
        {
            // parametri IN
            cs.setLong(1, fromId);
            cs.setLong(2, toId);
            cs.setDouble(3, amount);

            // parametru OUT — trebuie inregistrat cu tipul sau Oracle
            cs.registerOutParameter(4, Types.VARCHAR);

            cs.execute();

            // citire valoare OUT dupa execute()
            String result = cs.getString(4);
            return result;
        }
    }

    public static List<Transaction> getTransactionsByAccount(long accountId) throws SQLException {
        String sql = "{ call get_transactions_by_account(?, ?) }";
        List<Transaction> result = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cs = conn.prepareCall(sql))

        {
            cs.setLong(1, accountId);
            cs.registerOutParameter(2, OracleTypes.CURSOR); // din oracle.jdbc.OracleTypes

            cs.execute();

            // cursorul e un ResultSet — se inchide automat la iesirea din try
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getLong("id"));
                    t.setAccountId(rs.getLong("account_id"));
                    t.setType(rs.getString("type"));
                    t.setAmount(rs.getDouble("amount"));
                    t.setDescription(rs.getString("description"));
                    t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
                    result.add(t);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws SQLException {
        // Metodele statice sunt cele din seminar, in continuare vom rezolva si exemplifica apelurile de la "6. Exercitii".

        // A
        Optional<Account> account = AccountRepository.findByIban("RO49AAAA1B31007593840000");
        if(account.isPresent()){
            System.out.println(account.get());
        } else {
            System.out.println("Contul nu a fost gasit.");
        }


        System.out.println("\nACTIVE");
        AccountRepository.findByStatus("ACTIVE").forEach(System.out::println);

        System.out.println("\nBLOCKED - acc 1");
        if(AccountRepository.block(1)){
            System.out.println("Contul a fost blocat.");
        } else {
            System.out.println("Contul NU A PUTUT FI BLOCAT.");
        }

        System.out.println("\nDELETE - acc 2, balance 0");
        if(AccountRepository.delete(2)){
            System.out.println("Contul a fost sters.");
        } else {
            System.out.println("Contul NU A PUTUT FI STERS - nu exista sau are balance != 0.");
        }

        System.out.println("\nACTTIVE ACCOUNTS with procedure");
        AccountRepository.getActiveAccounts().forEach(System.out::println);

        System.out.println("\nACTIVE accounts and their count transaction");
        AccountRepository.getActiveAccounts().forEach(a -> {
            System.out.println(a);
            try {
                System.out.println(AccountRepository.countTransactions(a.getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
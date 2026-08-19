package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.LoginDAO;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.model.Credentials;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAODB implements LoginDAO {

    @Override
    public Credentials execute(String email, String plainPassword) throws LoginException {
        String hashedPassword = plainPassword;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call login(?, ?, ?, ?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, hashedPassword);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.VARCHAR);

            cs.execute();

            String roleStr = cs.getString(6);

            if (roleStr == null || roleStr.equals("NOT_FOUND")) {
                throw new LoginException("Credenziali non valide. Riprova.");
            }

            Role role = Role.valueOf(roleStr.toUpperCase());
            return new Credentials(email, hashedPassword, role);

        } catch (SQLException e) {
            throw new LoginException("Errore DB durante il login: " + e.getMessage(), e);
        }
    }

}

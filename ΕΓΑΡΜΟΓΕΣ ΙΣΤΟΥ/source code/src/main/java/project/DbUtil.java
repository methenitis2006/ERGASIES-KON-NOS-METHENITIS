package hua.dit.web.project;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DbUtil {

	private static final String DB_URL = getConfig("DB_URL",
		"jdbc:h2:mem:projectdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
	private static final String DB_USER = getConfig("DB_USER", "sa");
	private static final String DB_PASSWORD = getConfig("DB_PASSWORD", "");
	private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError("H2 JDBC driver not found in runtime classpath.");
		}
	}

	private DbUtil() {
	}

	private static String getConfig(String key, String defaultValue) {
		String env = System.getenv(key);
		return env != null && !env.isBlank() ? env : defaultValue;
	}

	public static Connection getConnection() throws SQLException {
		ensureDatabase();
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}

	private static void ensureDatabase() throws SQLException {
		if (INITIALIZED.get()) {
			return;
		}
		synchronized (DbUtil.class) {
			if (INITIALIZED.get()) {
				return;
			}
			try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
				createSchema(con);
				seedData(con);
				INITIALIZED.set(true);
			}
		}
	}

	private static void createSchema(Connection con) throws SQLException {
		try (java.sql.Statement st = con.createStatement()) {
			st.execute("""
				CREATE TABLE IF NOT EXISTS users (
					id INT AUTO_INCREMENT PRIMARY KEY,
					uname VARCHAR(50) NOT NULL UNIQUE,
					upasshash VARCHAR(64) NOT NULL
				)
			""");
			st.execute("""
				CREATE TABLE IF NOT EXISTS topics (
					id INT AUTO_INCREMENT PRIMARY KEY,
					name VARCHAR(100) NOT NULL UNIQUE,
					description VARCHAR(256) NOT NULL
				)
			""");
			st.execute("""
				CREATE TABLE IF NOT EXISTS messages (
					id INT AUTO_INCREMENT PRIMARY KEY,
					topic_id INT NOT NULL,
					user_id INT NOT NULL,
					msg VARCHAR(256) NOT NULL,
					date_sent TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
					CONSTRAINT messages_ibfk_1 FOREIGN KEY (topic_id) REFERENCES topics(id),
					CONSTRAINT messages_ibfk_2 FOREIGN KEY (user_id) REFERENCES users(id)
				)
			""");
		}
	}

	private static void seedData(Connection con) throws SQLException {
		try (java.sql.Statement st = con.createStatement()) {
			st.executeUpdate("INSERT INTO users(id, uname, upasshash) VALUES " +
				"(1, 't', 'e3b98a4da31a127d4bde6e43033f66ba274cab0eb7eb1c70ec41402bf6273dd8')," +
				"(2, 'test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08')," +
				"(3, 'tim', 'c0d19e4483571ff07cb01a4d3f5484102d7f333c4cafa64a2821f55031ea6041')," +
				"(4, 'vas', '74356fbb67fef5edcfb2809b4b5e86def771fc45920009d4ab83d3b7e191284e')," +
				"(5, 'maria', '94aec9fbed989ece189a7e172c9cf41669050495152bc4c1dbf2a38d7fd85627')," +
				"(6, 'eleni', 'ea25818a98c22c877759fd528eb38b3aec1d6ef92610b0bd819aac32dbf35077')");
			st.executeUpdate("INSERT INTO topics(id, name, description) VALUES " +
				"(1, 'Client-side Development', 'Discussion about client-side technologies such as HTML, CSS and JavaScript.')," +
				"(2, 'Server-side Development', 'Discussion about server-side technologies, such as Java, Servlet, JSP, Sessions, Cookies, and Interaction with RDBMS using JDBC API.')," +
				"(3, 'Network-Protocols', 'Discussion about Network Protocols, such as HTTP/HTTPS and DNS.')," +
				"(4, 'Operating Systems', 'Discussion about Operating Systems.')," +
				"(5, 'Hardware', 'Discussion about Hardware, including but not limited to CPU, GPU and TPU!')");
			st.executeUpdate("INSERT INTO messages(id, topic_id, user_id, msg, date_sent) VALUES " +
				"(1, 2, 2, 'Test Message ...', TIMESTAMP '2026-05-17 13:04:04')," +
				"(2, 3, 3, 'In this course we mainly focus on Application Layer.', TIMESTAMP '2026-05-17 13:04:30')," +
				"(3, 3, 6, 'Which one ?', TIMESTAMP '2026-05-17 13:04:41')," +
				"(5, 3, 3, 'Mainly, HTTP and DNS.', TIMESTAMP '2026-05-17 13:04:58')," +
				"(6, 3, 3, 'Adequate knowledge of TCP and IP protocols is necessary.', TIMESTAMP '2026-05-17 13:05:08')," +
				"(8, 4, 3, 'In this course we do not focus on Operating Systems (OS).', TIMESTAMP '2026-05-17 13:05:32')," +
				"(9, 4, 3, 'Nevertheless they are rather important for both client and server.', TIMESTAMP '2026-05-17 13:05:42')," +
				"(10, 5, 3, 'Servers should be powerful machines.', TIMESTAMP '2026-05-17 13:05:58')," +
				"(11, 5, 3, 'GPU is essential for Deep Neural Networks (DNNs).', TIMESTAMP '2026-05-17 13:06:13')," +
				"(12, 5, 5, 'Can a train a transformer-based DNN in my laptop.', TIMESTAMP '2026-05-17 13:06:28')," +
				"(13, 5, 4, 'To be honest, no !!!', TIMESTAMP '2026-05-17 13:06:39')");
		}
	}

	public static int updatePassword(String username, String oldHash, String newHash) throws SQLException {
		final String sql = "UPDATE users SET upasshash = ? WHERE id IN (SELECT id FROM users WHERE uname = ? and upasshash = ?)";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, newHash);
			ps.setString(2, username);
			ps.setString(3, oldHash);
			return ps.executeUpdate();
		}
	}

	public static Integer authenticateUser(String username, String passwordHash) throws SQLException {
		final String sql = "SELECT id FROM users WHERE uname = ? and upasshash = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			ps.setString(2, passwordHash);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt(1) : null;
			}
		}
	}

	public static List<Topic> getTopics() throws SQLException {
		final String sql = "SELECT * FROM topics ORDER BY id";
		List<Topic> topics = new ArrayList<>();
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				topics.add(new Topic(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
			}
		}
		return topics;
	}

	public static int insertMessage(int topicId, int userId, String message) throws SQLException {
		final String sql = "INSERT INTO messages (topic_id, user_id, msg) VALUES (?, ?, ?)";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, topicId);
			ps.setInt(2, userId);
			ps.setString(3, message);
			return ps.executeUpdate();
		}
	}

	public static int countMessagesForTopic(int topicId) throws SQLException {
		final String sql = "SELECT count(*) FROM messages WHERE topic_id = ?";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, topicId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt(1) : 0;
			}
		}
	}

	public static Integer insertTopic(String name, String description) throws SQLException {
		final String sql = "INSERT INTO topics VALUES (null, ?, ?)";
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name);
			ps.setString(2, description);
			int updated = ps.executeUpdate();
			if (updated == 0) {
				return null;
			}
			try (ResultSet keys = ps.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : null;
			}
		}
	}
}

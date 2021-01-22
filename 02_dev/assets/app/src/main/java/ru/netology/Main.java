package ru.netology;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

public class Main {
  //language=HTML
  public static final String INDEX = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"> <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\"> <title>Document</title> </head> <body> <h1>Home page</h1> </body> </html>";
  //language=HTML
  public static final String USERS = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"> <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\"> <title>Document</title> </head> <body> <h1>Users Page</h1> <ul>%users%</ul> </body> </html>";

  public static void main(String[] args) {
    try {
      initDb(getConnection(), false);

      final var server = HttpServer.createSimpleServer();
      server.getServerConfiguration().addHttpHandler(new HttpHandler() {
        @Override
        public void service(Request request, Response response) throws Exception {
          final var authorization = request.getAuthorization();
          System.out.println(authorization);

          if (authorization == null || !authorization.equals("Basic " + Base64.getEncoder().encodeToString("admin:secret".getBytes(StandardCharsets.UTF_8)))) {
            response.addHeader(Header.WWWAuthenticate, "Basic realm='Restricted Area'");
            response.sendError(HttpStatus.UNAUTHORIZED_401.getStatusCode(), "Not Authorized");
            return;
          }

          final var users = getUsers(getConnection());
          response.getWriter().write(USERS.replace("%users%", users));
        }
      }, "/users.html");

      server.start();
      Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
      Thread.currentThread().join();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:h2:~/h2.db", "sa", "");
  }

  private static void initDb(Connection conn, boolean clean) {
    clean = true;
    try (
        final var stmt = conn.createStatement();
    ) {
      if (clean) {
        stmt.execute("DROP TABLE IF EXISTS users");
      }
      stmt.execute("CREATE TABLE users (id INTEGER PRIMARY KEY AUTO_INCREMENT, name TEXT NOT NULL, username TEXT NOT NULL)");
      stmt.execute("INSERT INTO users (id, name, username) VALUES (1, 'Ivanova Svetlana', 'svetlana.ivanova'), (2, 'Nikolaeva Alexandra', 'nikolaeva.alexandra'), (3, 'Vasilieva Anastasia', 'vasilieva.anastasia')");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getUsers(Connection conn) {
    var builder = new StringBuilder();
    try (
        conn;
        final var stmt = conn.prepareStatement("SELECT * FROM users ORDER BY id");
        final var rs = stmt.executeQuery();
    ) {
      while (rs.next()) {
        builder.append("<li>");
        builder.append(rs.getInt("id"));
        builder.append(", ");
        builder.append(rs.getString("name"));
        builder.append(", ");
        builder.append(rs.getString("username"));
        builder.append(", ");
        builder.append("</li>");
      }

      return builder.toString();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler implements IHandler {
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	public DatabaseHandler(String url, String username, String password) {
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + url.trim() + "?user=" + username.trim() + "&password=" + password.trim());
			// conn =
			// DriverManager.getConnection("jdbc:mysql://"
			// + "user=sebo&password=");

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	@Override
	public ArrayList<Movie> selectAllMovies() {
		ArrayList<Movie> queryRes = new ArrayList<>();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"SELECT movie.name,director.name,year,duration,	seen FROM movie JOIN director ON movie.director=director.dirKey;");
			while (rs.next()) {
				Movie m = new Movie(rs.getString("movie.name"), rs.getString("director.name"), rs.getString("year"),
						rs.getString("duration"), rs.getBoolean("seen"));
				queryRes.add(m);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException sqlEx) {
			}
		}

		return queryRes;

	}

	@Override
	public String addMovie(Movie m) {
		ArrayList<Movie> movies = new ArrayList<>();

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT name FROM director WHERE name=\"" + m.getDirector() + "\";");
			String dirName = new String(rs.getString("name"));

			rs = stmt.executeQuery(
					"SELECT movie.name, director.name, year, duration, seen FROM movie JOIN director ON movie.director=director.dirKey WHERE movie.name=\""
							+ m.getName() + "\" AND year=" + m.getYear() + ";");

			while (rs.next()) {
				Movie mov = new Movie(rs.getString("movie.name"), rs.getString("director.name"), rs.getString("year"),
						rs.getString("duration"), rs.getBoolean("seen"));
				movies.add(mov);
			}

			if (movies.isEmpty()) {
				if (dirName.isEmpty()) {

					System.out.println(
							"INSERT INTO director (name) VALUES ( \"" + m.getDirector() + "\");"); /** debug **/
					stmt.executeUpdate("INSERT INTO director (name) VALUES ( \"" + m.getDirector() + "\");"); // insert
																												// into
																												// director
																												// relation
				}

				System.out.println("INSERT INTO movie VALUES ( \"" + m.getName() + "\", " + m.getYear() + ", "
						+ m.getSeen() + ", (" + "SELECT dirKey FROM director WHERE name=\"" + m.getDirector() + "\"), "
						+ m.getDuration() + " );"); /** debug **/
				stmt.executeUpdate("INSERT INTO movie VALUES ( \"" + m.getName() + "\", " + m.getYear() + ", "
						+ m.getSeen() + ", (" + "SELECT dirKey FROM director WHERE name=\"" + m.getDirector() + "\"), "
						+ m.getDuration() + " );"); // insert
													// into
													// movie
													// relation
				return new String("Movie added");

			} else {
				return new String("Movie already exists");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException sqlEx) {
			}
		}

		return new String("Error.");
	}

	@Override
	public void editMovie(Movie newMovie, Movie selectedMovie) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE director SET name=\"" + newMovie.getDirector() + "\" WHERE name=\""
					+ selectedMovie.getDirector() + "\";");

			stmt.executeUpdate("UPDATE movie SET name=\"" + newMovie.getName() + "\", year=" + newMovie.getYear()
					+ ", seen=" + newMovie.getSeen() + ", director=(SELECT dirKey FROM director WHERE name=\""
					+ newMovie.getDirector() + "\"), duration=" + newMovie.getDuration() + " WHERE name=\""
					+ selectedMovie.getName() + "\" AND year=" + selectedMovie.getYear() + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void delMovie(Movie m) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM movie WHERE name=\"" + m.getName() + "\" AND year=" + m.getYear() + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ArrayList<Movie> filterMovies(Movie m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markSeen(Movie m) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE movie SET seen=true WHERE movie.name=\"" + m.getName() + "\" AND year="
					+ m.getYear() + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void markUnseen(Movie m) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE movie SET seen=false WHERE movie.name=\"" + m.getName() + "\" AND year="
					+ m.getYear() + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public ArrayList<Movie> selectSeenMovies() {
		ArrayList<Movie> queryRes = new ArrayList<>();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"SELECT movie.name, director.name, year, duration, seen FROM movie JOIN director ON movie.director=director.dirKey WHERE seen=true;");
			while (rs.next()) {
				Movie m = new Movie(rs.getString("movie.name"), rs.getString("director.name"), rs.getString("year"),
						rs.getString("duration"), rs.getBoolean("seen"));
				queryRes.add(m);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException sqlEx) {
			}
		}

		return queryRes;
	}

	@Override
	public ArrayList<Movie> selectUnseenMovies() {
		ArrayList<Movie> queryRes = new ArrayList<>();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"SELECT movie.name, director.name, year, duration, seen FROM movie JOIN director ON movie.director=director.dirKey WHERE seen=false;");
			while (rs.next()) {
				Movie m = new Movie(rs.getString("movie.name"), rs.getString("director.name"), rs.getString("year"),
						rs.getString("duration"), rs.getBoolean("seen"));
				queryRes.add(m);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException sqlEx) {
			}

		}

		return queryRes;
	}

	/**
	 * WARNING CODE EDITED AND I DIDN'T COMPILE IT AND CHECK IF IT WORKS the
	 * edit was removing the parse to integer methods from the Movie class
	 * instancing in a few cases (search "Movie m")
	 */
	@Override
	public void closeConn() {
		try {
			conn.close();
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
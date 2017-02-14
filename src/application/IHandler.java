package application;

import java.util.ArrayList;

public interface IHandler {
	public ArrayList<Movie> selectAllMovies();

	public ArrayList<Movie> selectSeenMovies();

	public ArrayList<Movie> selectUnseenMovies();

	public String addMovie(Movie m);

	public void editMovie(Movie newMovie, Movie selectedMovie);

	public void delMovie(Movie m);

	public void markSeen(Movie m);

	public void markUnseen(Movie m);

	public void closeConn();

	public ArrayList<Movie> filterMovies(Movie m);

}

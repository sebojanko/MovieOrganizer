package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler implements IHandler {
	private FileWriter fw;
	private FileReader fr;
	private BufferedReader br;
	private File f;
	private ArrayList<Movie> movies;

	public FileHandler(File f) {
		movies = new ArrayList<>();
		if (f == null) {
			throw new NullPointerException();
		}
		this.f = f;
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// read all movies and save them in arraylist movies
		loadMovies();
	}

	private void loadMovies() {
		String movie = null;

		try {
			while ((movie = br.readLine()) != null) {
				System.out.println(movie);
				String[] data = movie.split("\t");
				movies.add(new Movie(data[0], data[1], data[2], data[3], Boolean.parseBoolean(data[4])));
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<Movie> selectAllMovies() {
		return movies;
	}

	@Override
	public String addMovie(Movie m) {
		if (movies.add(new Movie(m.getName(), m.getDirector(), Integer.toString(m.getYear()),
				Integer.toString(m.getDuration()), m.getSeen())))
			return new String("Movie added.");
		else
			return new String("Error.");
	}

	@Override
	public void editMovie(Movie newMovie, Movie selectedMovie) {
		for (Movie m : movies) {
			if (m.getName().equals(selectedMovie.getName()) && m.getYear().equals(selectedMovie.getYear())) {
				m.setName(newMovie.getName());
				m.setDirector(newMovie.getDirector());
				m.setYear(newMovie.getYear());
				m.setDuration(newMovie.getDuration());
				m.setSeen(newMovie.getSeen());
			}
		}
	}

	@Override
	public void delMovie(Movie m) {
		Movie toRemove = null;

		for (Movie mov : movies) {
			if (mov.getName().equals(m.getName()) && mov.getYear().equals(m.getYear())) {
				toRemove = mov;
				break;
			}
		}

		movies.remove(toRemove);
	}

	@Override
	public ArrayList<Movie> filterMovies(Movie filter) {
		// TODO - check opciju koji se fieldovi gledaju
		ArrayList<Movie> filteredMovies = new ArrayList<>();
		String tempFilterName = filter.getName();
		String tempFilterDir = filter.getDirector();

		/**
		 * temporary hack - if the search does not contain director or name
		 * values, set them to "zzzz" because I expect there won't exist a movie
		 * with those values.
		 **/
		if (tempFilterDir.equals("")) // because an empty string occurs in every
										// string
			tempFilterDir = "zzzz";
		if (tempFilterName.equals("")) // because an empty string occurs in
										// every string
			tempFilterName = "zzzz";

		for (Movie m : movies) {
			if (m.getName().contains(tempFilterName) || m.getDirector().contains(tempFilterDir)
					|| m.getYear() == filter.getYear() || m.getDuration() == filter.getDuration())

				filteredMovies.add(m);
		}

		return filteredMovies;
	}

	@Override
	public void markSeen(Movie m) {
		for (Movie mov : movies) {
			if (m.getName().equals(mov.getName()) && m.getYear().equals(mov.getYear())) {
				mov.setSeen(true);
				break;
			}
		}

	}

	@Override
	public void markUnseen(Movie m) {
		for (Movie mov : movies) {
			if (m.getName().equals(mov.getName()) && m.getYear().equals(mov.getYear())) {
				mov.setSeen(false);
				break;
			}
		}

	}

	@Override
	public ArrayList<Movie> selectSeenMovies() {
		ArrayList<Movie> moviesSeen = new ArrayList<>();

		for (Movie m : movies)
			if (m.getSeen())
				moviesSeen.add(m);

		return moviesSeen;
	}

	@Override
	public ArrayList<Movie> selectUnseenMovies() {
		ArrayList<Movie> moviesUnseen = new ArrayList<>();

		for (Movie m : movies)
			if (m.getSeen() == false)
				moviesUnseen.add(m);

		return moviesUnseen;
	}

	@Override
	public void closeConn() {
		try {
			fw = new FileWriter(f);
			for (Movie m : movies) {
				fw.write(m.toString());
			}
			fw.close();
			fr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

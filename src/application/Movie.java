package application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Movie {
	private SimpleStringProperty name, director;
	private SimpleIntegerProperty year, duration;
	private SimpleBooleanProperty seen;

	public Movie(String name, String director, String year, String duration, Boolean seen) {
		// this if-s are here so the fields can remain empty. also, so that the
		// year and duration can be strings
		if (!year.equals("")) {
			this.year = new SimpleIntegerProperty(Integer.parseInt(year));
		} else {
			this.year = new SimpleIntegerProperty(0);
		}
		if (!duration.equals("")) {
			this.duration = new SimpleIntegerProperty(Integer.parseInt(duration));
		} else {
			this.duration = new SimpleIntegerProperty(0);
		}

		this.name = new SimpleStringProperty(name);
		this.director = new SimpleStringProperty(director);
		this.seen = new SimpleBooleanProperty(seen);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name.set(name);
	}

	/**
	 * @return the director
	 */
	public String getDirector() {
		return director.get();
	}

	/**
	 * @param director
	 *            the director to set
	 */
	public void setDirector(String director) {
		this.director.set(director);
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year.get();
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Integer year) {
		this.year.set(year);
	}

	/**
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration.get();
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(Integer duration) {
		this.duration.set(duration);
	}

	/**
	 * @return the seen
	 */
	public Boolean getSeen() {
		return seen.getValue();
	}

	/**
	 * @param seen
	 *            the seen to set
	 */
	public void setSeen(Boolean seen) {
		this.seen.set(seen);
	}

	@Override
	public String toString() {
		return new String(name.get() + "\t" + director.get() + "\t" + year.get() + "\t" + duration.get() + "\t"
				+ seen.get() + "\n");
	}
}

package application;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UICreator {

	private ObservableList<Movie> movieData;
	private IHandler handler;
	private DatabaseInfo dbInfo;
	private Preferences prefs;
	private File f;

	public UICreator(Stage stage) {
		prefs = Preferences.userNodeForPackage(this.getClass());
		// if already started once, returns the argument (false in this case) if
		// fails
		if (prefs.getBoolean("STARTED_ALREADY", false)) {
			create(stage);
		} else {
			prefs.putBoolean("STARTED_ALREADY", true);
			create(stage);
			startUpDialog(stage); // the startup dialog contains information for
									// users who start the app for the first
									// time
		}

	}

	public void startUpDialog(Stage stage) {
		Alert first = new Alert(AlertType.INFORMATION);
		first.setHeaderText("Information");
		first.setContentText("Select from the menu the database or\nthe file to save the movies.");
		first.showAndWait();

	}

	public void connectDialog() {
		// Create the custom dialog.
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Login");
		dialog.setHeaderText("Login");

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField url = new TextField();
		url.setText("104.236.65.152:3306/movies");
		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("URL:"), 0, 0);
		grid.add(url, 1, 0);
		grid.add(new Label("Username:"), 0, 1);
		grid.add(username, 1, 1);
		grid.add(new Label("Password:"), 0, 2);
		grid.add(password, 1, 2);

		dialog.getDialogPane().setContent(grid);

		// Request focus on the url field
		Platform.runLater(() -> url.requestFocus());

		dialog.showAndWait().ifPresent(response -> {
			if (response == loginButtonType) {
				dbInfo = new DatabaseInfo(url.getText(), username.getText(), password.getText());
			}
		});

	}

	public void createFileDialog(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save file");
		f = fileChooser.showSaveDialog(stage);
		try {
			prefs.putBoolean("FILE_SELECTED", true);
			prefs.put("PATH", f.getAbsolutePath());
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void create(Stage primaryStage) {
		GridPane grid = new GridPane();

		ColumnConstraints column = new ColumnConstraints();
		column.setPercentWidth(15);
		grid.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setPercentWidth(15);
		grid.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setPercentWidth(70);
		grid.getColumnConstraints().add(column);
		// grid.setPrefSize(1000, 400);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15, 15, 15, 15));

		MenuBar menuBar = new MenuBar();

		Menu menuFile = new Menu("Load");
		MenuItem dbMItem = new MenuItem("Database");
		MenuItem fileMItem = new MenuItem("File");
		menuFile.getItems().addAll(dbMItem, fileMItem);

		Menu menuHelp = new Menu("Help");
		MenuItem aboutMItem = new MenuItem("About");
		menuHelp.getItems().addAll(aboutMItem);

		menuBar.getMenus().addAll(menuFile, menuHelp);

		Button selectAllBtn = new Button("Select all");
		selectAllBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(selectAllBtn, 0, 3);

		// TextField userTx = new TextField();
		Button selectSeenBtn = new Button("Select seen");
		selectSeenBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(selectSeenBtn, 0, 4);

		Button selectUnseenBtn = new Button("Select unseen");
		selectUnseenBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(selectUnseenBtn, 0, 5);

		Button markSeenBtn = new Button("Mark seen");
		markSeenBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(markSeenBtn, 0, 6);

		Button markUnseenBtn = new Button("Mark unseen");
		markUnseenBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(markUnseenBtn, 0, 7);

		Button addBtn = new Button("Add movie");
		addBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(addBtn, 1, 3);

		Button delBtn = new Button("Delete movie");
		delBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(delBtn, 1, 4);

		Button editBtn = new Button("Edit movie");
		editBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(editBtn, 1, 5);

		Button filterBtn = new Button("Filter");
		filterBtn.setMaxWidth(Double.MAX_VALUE);
		grid.add(filterBtn, 1, 7);

		Text infoTxt = new Text("Started.");
		grid.add(infoTxt, 0, 12);

		Text numberOfRowsTxt = new Text();
		grid.add(numberOfRowsTxt, 1, 12);

		TableView<Movie> movieTable = new TableView<>();

		TableColumn nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.prefWidthProperty().bind(movieTable.widthProperty().multiply(0.38));

		TableColumn directorCol = new TableColumn<>("Director");
		directorCol.setCellValueFactory(new PropertyValueFactory<>("director"));
		directorCol.prefWidthProperty().bind(movieTable.widthProperty().multiply(0.30));

		TableColumn yearCol = new TableColumn<>("Year");
		yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));

		TableColumn durationCol = new TableColumn<>("Duration");
		durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

		TableColumn seenCol = new TableColumn<>("Seen");
		seenCol.setCellValueFactory(new PropertyValueFactory<>("seen"));

		movieTable.getColumns().addAll(nameCol, directorCol, yearCol, durationCol, seenCol);

		grid.add(movieTable, 2, 2, 10, 8);

		dbMItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				connectDialog();
				if (dbInfo != null)
					handler = new DatabaseHandler(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());

				infoTxt.setText("Database mode.");
			}
		});

		fileMItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (prefs.getBoolean("FILE_SELECTED", false)) { // prompts the
																// user for
																// creating a
																// file to save
																// movies
					f = new File(prefs.get("PATH", null));
				} else {
					createFileDialog(primaryStage);
				}

				handler = new FileHandler(f);

				infoTxt.setText("File mode.");
				selectAllBtn.fire();
			}
		});

		aboutMItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Alert aboutAlert = new Alert(AlertType.INFORMATION);
				aboutAlert.setTitle("About");
				aboutAlert.setHeaderText(null);
				aboutAlert.setContentText(
						"Movie Application\n\nCreator: Sebastian Janko\nContact: janko.sebastian@gmail.com\n\nLicense: GPL");

				aboutAlert.showAndWait();

			}
		});

		selectAllBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (handler == null)
					infoTxt.setText("Error - not connected.");
				else {
					movieData = FXCollections.observableArrayList(handler.selectAllMovies());
					movieTable.setItems(movieData);
					movieTable.refresh();

					infoTxt.setText("Selected all.");
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));
				}
			}
		});

		selectSeenBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (handler == null)
					infoTxt.setText("Error - not connected.");
				else {
					movieData = FXCollections.observableArrayList(handler.selectSeenMovies());
					movieTable.setItems(movieData);

					infoTxt.setText("Selected seen.");
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));
				}
			}
		});

		selectUnseenBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (handler == null)
					infoTxt.setText("Error - not connected.");
				else {
					movieData = FXCollections.observableArrayList(handler.selectUnseenMovies());
					movieTable.setItems(movieData);

					infoTxt.setText("Selected unseen.");
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));
				}
			}
		});

		markSeenBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (handler == null)
					infoTxt.setText("Error - not connected.");
				else {
					Movie m = movieTable.getSelectionModel().getSelectedItem();
					handler.markSeen(m);

					infoTxt.setText("Seen.");

					System.out.println(m);
					selectAllBtn.fire();
				}
			}
		});
		markUnseenBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (handler == null)
					infoTxt.setText("Error - not connected.");
				else {
					Movie m = movieTable.getSelectionModel().getSelectedItem();
					handler.markUnseen(m);

					infoTxt.setText("Unseen.");

					System.out.println(m);
					selectAllBtn.fire();
				}
			}
		});

		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Dialog<Movie> dialog = new Dialog<>();
				dialog.setTitle("Add movie");

				// Set the button types.
				ButtonType okBtn = new ButtonType("Add", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

				// Create the username and password labels and fields.
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 20, 10, 10));

				TextField nameTxt = new TextField();
				TextField dirTxt = new TextField();
				TextField yearTxt = new TextField();
				TextField durTxt = new TextField();
				CheckBox seenBox = new CheckBox();

				grid.add(new Label("Name:"), 0, 0);
				grid.add(nameTxt, 1, 0);
				grid.add(new Label("Director:"), 0, 1);
				grid.add(dirTxt, 1, 1);
				grid.add(new Label("Year:"), 0, 2);
				grid.add(yearTxt, 1, 2);
				grid.add(new Label("Duration:"), 0, 3);
				grid.add(durTxt, 1, 3);
				grid.add(new Label("Seen:"), 0, 4);
				grid.add(seenBox, 1, 4);

				dialog.getDialogPane().setContent(grid);

				// Request focus on the username field by default.
				Platform.runLater(() -> nameTxt.requestFocus());

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == okBtn) {
						return new Movie(nameTxt.getText(), dirTxt.getText(), yearTxt.getText(), durTxt.getText(),
								seenBox.isSelected());
					}
					return null;
				});

				Optional<Movie> result = dialog.showAndWait();

				result.ifPresent(m -> {
					System.out.println("Name=" + m.getName() + " Dir=" + m.getDirector() + " Year=" + m.getYear()
							+ " Dur=" + m.getDuration() + " Seen=" + m.getSeen());
					String resultText = (handler.addMovie(m));

					infoTxt.setText(resultText);
					selectAllBtn.fire();
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));

				});

			}
		});
		delBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Movie selMov = movieTable.getSelectionModel().getSelectedItem();
				System.out.println(
						"DELETE FROM movie WHERE name=\"" + selMov.getName() + "\" AND year=" + selMov.getYear() + ";");
				handler.delMovie(selMov);

				/** if not exception raised **/
				infoTxt.setText("Movie deleted.");
				selectAllBtn.fire();
				numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));
			}
		});

		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Dialog<Movie> dialog = new Dialog<>();
				dialog.setTitle("Edit movie");

				Movie selMov = movieTable.getSelectionModel().getSelectedItem();
				if (selMov == null) {
					infoTxt.setText("No movie selected.");
					return;
				}

				// Set the button types.
				ButtonType okBtn = new ButtonType("Edit", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

				// Create the username and password labels and fields.
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 20, 10, 10));

				TextField nameTxt = new TextField(selMov.getName());
				TextField dirTxt = new TextField(selMov.getDirector());
				TextField yearTxt = new TextField(Integer.toString(selMov.getYear()));
				TextField durTxt = new TextField(Integer.toString(selMov.getDuration()));
				CheckBox seenBox = new CheckBox();
				if (selMov.getSeen())
					seenBox.setSelected(true);

				grid.add(new Label("Name:"), 0, 0);
				grid.add(nameTxt, 1, 0);
				grid.add(new Label("Director:"), 0, 1);
				grid.add(dirTxt, 1, 1);
				grid.add(new Label("Year:"), 0, 2);
				grid.add(yearTxt, 1, 2);
				grid.add(new Label("Duration:"), 0, 3);
				grid.add(durTxt, 1, 3);
				grid.add(new Label("Seen:"), 0, 4);
				grid.add(seenBox, 1, 4);

				dialog.getDialogPane().setContent(grid);

				// Request focus on the name field by default.
				Platform.runLater(() -> nameTxt.requestFocus());

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == okBtn) {
						return new Movie(nameTxt.getText(), dirTxt.getText(), yearTxt.getText(), durTxt.getText(),
								seenBox.isSelected());
					}
					return null;
				});

				Optional<Movie> result = dialog.showAndWait();

				result.ifPresent(m -> {
					System.out.println("UPDATE movie SET name=\"" + m.getName() + "\", year=" + m.getYear() + ", seen="
							+ m.getSeen() + ", director=(SELECT dirKey FROM director WHERE name=\"" + m.getDirector()
							+ "\"), duration=" + m.getDuration() + " WHERE name=\"" + selMov.getName() + "\" AND year="
							+ selMov.getYear() + ";");
					System.out.println("UPDATE director SET name=\"" + m.getDirector() + "\" WHERE name=\""
							+ selMov.getDirector() + "\";");
					handler.editMovie(m, selMov);

					infoTxt.setText("Movie edited.");
					selectAllBtn.fire();
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));

				});

			}
		});

		filterBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Dialog<Movie> dialog = new Dialog<>();
				dialog.setTitle("Filter");

				// Set the button types.
				ButtonType okBtn = new ButtonType("Filter", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

				// Create the username and password labels and fields.
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 20, 10, 10));

				TextField nameTxt = new TextField();
				TextField dirTxt = new TextField();
				TextField yearTxt = new TextField();
				TextField durTxt = new TextField();
				CheckBox seenBox = new CheckBox();

				grid.add(new Label("Name:"), 0, 0);
				grid.add(nameTxt, 1, 0);
				grid.add(new Label("Director:"), 0, 1);
				grid.add(dirTxt, 1, 1);
				grid.add(new Label("Year:"), 0, 2);
				grid.add(yearTxt, 1, 2);
				grid.add(new Label("Duration:"), 0, 3);
				grid.add(durTxt, 1, 3);
				grid.add(new Label("Seen:"), 0, 4);
				grid.add(seenBox, 1, 4);

				dialog.getDialogPane().setContent(grid);

				// Request focus on the username field by default.
				Platform.runLater(() -> nameTxt.requestFocus());

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == okBtn) {
						return new Movie(nameTxt.getText(), dirTxt.getText(), yearTxt.getText(), durTxt.getText(),
								seenBox.isSelected());
					}
					return null;
				});

				Optional<Movie> result = dialog.showAndWait();

				result.ifPresent(m -> {
					movieData = FXCollections.observableArrayList(handler.filterMovies(m));
					movieTable.setItems(movieData);
					movieTable.refresh();

					infoTxt.setText("Movies filtered.");
					numberOfRowsTxt.setText(Integer.toString(movieTable.getItems().size()));
				});

			}
		});

		/**
		 * DONE - ostale gumbe napraviti - add movie kreira novi dialog za upis
		 * podataka - edit movie otvara dialog sa podacima upisanima - exec
		 * otvara dialog sa textboxom za query - txt file mode - menu di je
		 * opcija za biranje db ili txt i onda se mijenja gumb connect jer ne
		 * treba connect za txt file - tj maknuti connect altogether i napraviti
		 * meni koji ima connect... i local file... - trebat ce window za
		 * sejvati text file prvi put kad se pokrene i to zapisati negdje u neku
		 * datoteku (provjera win ili *nix?) - na pocetku staviti dialog koji
		 * kaze da je prvo pokretanje i da treba otvoriti file
		 **/

		/**
		 * TODO - neki basic security, da ne moze se samo tako napraviti neki
		 * drop db or smth - web app mozda? - napredni filter sa > i < za
		 * year/duration - biranje izmedu OR i AND filtriranja
		 */

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				// System.out.println(prefs.get("PATH", "a"));
				// prefs.putBoolean("STARTED_ALREADY", false);
				// prefs.putBoolean("FILE_SELECTED", false);
				if (handler != null)
					handler.closeConn();
			}
		});

		Scene scene = new Scene(new VBox(), 1100, 400);
		((VBox) scene.getRoot()).getChildren().addAll(menuBar, grid);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Movie Organizer");

		primaryStage.show();
	}
}

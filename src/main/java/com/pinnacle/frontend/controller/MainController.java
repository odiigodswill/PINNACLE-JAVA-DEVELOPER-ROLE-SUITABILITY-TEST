package com.pinnacle.frontend.controller;


import com.pinnacle.frontend.model.Book;
import com.pinnacle.frontend.service.BookService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

//@Data
public class MainController {
    @FXML private TextField searchField;
    @FXML private TableView<Book> tableView;
    @FXML private TableColumn<Book, Long> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, String> colPublished;
    @FXML private Pagination pagination;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField publishedField;

    private final BookService bookService = new BookService();
    private final ObservableList<Book> books = FXCollections.observableArrayList();

    private int pageSize = 10;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colTitle.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTitle()));
        colAuthor.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAuthor()));
        colIsbn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getIsbn()));
        colPublished.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPublishedDate() != null ? cell.getValue().getPublishedDate().toString() : ""));

        tableView.setItems(books);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                titleField.setText(newSel.getTitle());
                authorField.setText(newSel.getAuthor());
                isbnField.setText(newSel.getIsbn());
                publishedField.setText(newSel.getPublishedDate() != null ? newSel.getPublishedDate().toString() : "");
            }
        });

        pagination.setPageFactory(this::createPage);
        reloadPage(0);
    }

    private javafx.scene.Node createPage(int pageIndex) {
        loadPageAsync(pageIndex, searchField.getText());
        return tableView; // pagination control will show tableView — we keep table as main node.
    }

    private void loadPageAsync(int pageIndex, String search) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                BookService.Page<Book> page = bookService.query(pageIndex, pageSize, search);
                List<Book> items = page.getItems();
                Platform.runLater(() -> {
                    books.setAll(items);
                    int totalPages = Math.max(1, page.getTotalPages());
                    pagination.setPageCount(totalPages);
                    if (pagination.getCurrentPageIndex() != pageIndex) {
                        pagination.setCurrentPageIndex(pageIndex);
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onSearch() {
        reloadPage(0);
    }

    @FXML
    private void onReload() {
        bookService.reload();
        reloadPage(0);
    }

    private void reloadPage(int pageIndex) {
        pagination.setCurrentPageIndex(pageIndex);
        loadPageAsync(pageIndex, searchField.getText());
    }

    @FXML
    private void onAdd() {
        try {
            Book b = new Book();
            b.setTitle(titleField.getText());
            b.setAuthor(authorField.getText());
            b.setIsbn(isbnField.getText());
            if (!publishedField.getText().isBlank()) {
                b.setPublishedDate(LocalDate.parse(publishedField.getText()));
            }
            bookService.save(b);
            showInfo("Added", "Book added successfully.");
            reloadPage(pagination.getCurrentPageIndex());
        } catch (DateTimeParseException ex) {
            showError("Invalid date", "Published date must be in yyyy-mm-dd format.");
        }
    }

    @FXML
    private void onUpdate() {
        Book sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("No selection", "Select a book to update.");
            return;
        }
        try {
            sel.setTitle(titleField.getText());
            sel.setAuthor(authorField.getText());
            sel.setIsbn(isbnField.getText());
            if (!publishedField.getText().isBlank()) {
                sel.setPublishedDate(LocalDate.parse(publishedField.getText()));
            } else {
                sel.setPublishedDate(null);
            }
            bookService.save(sel);
            showInfo("Updated", "Book updated successfully.");
            reloadPage(pagination.getCurrentPageIndex());
        } catch (DateTimeParseException ex) {
            showError("Invalid date", "Published date must be in yyyy-mm-dd format.");
        }
    }

    @FXML
    private void onDelete() {
        Book sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("No selection", "Select a book to delete.");
            return;
        }
        boolean ok = AlertHelper.confirm("Delete", "Delete this book?\n" + sel.getTitle());
        if (!ok) return;
        bookService.delete(sel.getId());
        showInfo("Deleted", "Book deleted.");
        reloadPage(pagination.getCurrentPageIndex());
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }
}

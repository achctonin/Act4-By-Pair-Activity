package backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class TodoBackend {

    private static final String FILE_PATH = "todos.txt";

    public static class TodoItem {
        private int     id;
        private String  title;
        private boolean completed;

        public TodoItem(int id, String title, boolean completed) {
            this.id        = id;
            this.title     = title;
            this.completed = completed;
        }

        public int     getId()        { return id; }
        public String  getTitle()     { return title; }
        public boolean isCompleted()  { return completed; }

        public void setTitle(String title)          { this.title     = title; }
        public void setCompleted(boolean completed) { this.completed = completed; }

        public String toFileLine() {
            return id + "|" + title + "|" + completed;
        }

        public static TodoItem fromFileLine(String line) {
            String[] parts = line.split("\\|", 3);   
            if (parts.length < 3) return null;
            int     id        = Integer.parseInt(parts[0].trim());
            String  title     = parts[1].trim();
            boolean completed = Boolean.parseBoolean(parts[2].trim());
            return new TodoItem(id, title, completed);
        }

        @Override
        public String toString() {
            return "[" + (completed ? "✓" : " ") + "] " + title;
        }
    }

    public List<TodoItem> loadTodos() {
        List<TodoItem> todos = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return todos;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    TodoItem item = TodoItem.fromFileLine(line);
                    if (item != null) todos.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading todos: " + e.getMessage());
        }

        return todos;
    }

    public void saveTodos(List<TodoItem> todos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (TodoItem item : todos) {
                writer.write(item.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving todos: " + e.getMessage());
        }
    }

    public TodoItem addTodo(String title) {
        List<TodoItem> todos = loadTodos();
        int newId = todos.stream().mapToInt(TodoItem::getId).max().orElse(0) + 1;
        TodoItem newItem = new TodoItem(newId, title, false);
        todos.add(newItem);
        saveTodos(todos);
        return newItem;
    }

    public boolean deleteTodo(int id) {
        List<TodoItem> todos = loadTodos();
        boolean removed = todos.removeIf(item -> item.getId() == id);
        if (removed) saveTodos(todos);
        return removed;
    }

    public boolean toggleComplete(int id) {
        List<TodoItem> todos = loadTodos();
        for (TodoItem item : todos) {
            if (item.getId() == id) {
                item.setCompleted(!item.isCompleted());
                saveTodos(todos);
                return true;
            }
        }
        return false;
    }

    public boolean updateTitle(int id, String newTitle) {
        List<TodoItem> todos = loadTodos();
        for (TodoItem item : todos) {
            if (item.getId() == id) {
                item.setTitle(newTitle);
                saveTodos(todos);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        TodoBackend backend = new TodoBackend();

        System.out.println("=== Backend Test ===");
        backend.addTodo("Buy groceries");
        backend.addTodo("Read a Java book");
        backend.addTodo("Go for a walk");

        List<TodoItem> todos = backend.loadTodos();
        System.out.println("\nLoaded todos:");
        todos.forEach(System.out::println);

        backend.toggleComplete(todos.get(0).getId());
        backend.deleteTodo(todos.get(1).getId());

        System.out.println("\nAfter toggle + delete:");
        backend.loadTodos().forEach(System.out::println);
    }
}

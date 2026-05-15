package frontend;
import backend.TodoBackend;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class ToDoFrontend extends JFrame {

    private final TodoBackend backend = new TodoBackend();

    private static final Color BG_DARK      = new Color(18,  18,  28);
    private static final Color BG_CARD      = new Color(28,  28,  42);
    private static final Color BG_INPUT     = new Color(38,  38,  55);
    private static final Color ACCENT       = new Color(99, 179, 237);   // sky-blue
    private static final Color ACCENT_DONE  = new Color(72, 199, 142);   // mint-green
    private static final Color TEXT_PRIMARY = new Color(237, 237, 245);
    private static final Color TEXT_MUTED   = new Color(120, 120, 150);
    private static final Color BTN_DELETE   = new Color(252,  87,  87);
    private static final Color BTN_HOVER    = new Color(120, 195, 255);

    private DefaultListModel<TodoBackend.TodoItem> listModel;
    private JList<TodoBackend.TodoItem>            todoList;
    private JTextField                             inputField;
    private JLabel                                 statusLabel;
    private JLabel                                 counterLabel;

    public ToDoFrontend() {
        super("✅  My To-Do List");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 640);
        setMinimumSize(new Dimension(420, 500));
        setLocationRelativeTo(null);                     // centre on screen
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        buildUI();
        refreshList();

        setVisible(true);
    }

    private void buildUI() {
        add(buildHeader(),     BorderLayout.NORTH);
        add(buildListPanel(),  BorderLayout.CENTER);
        add(buildFooter(),     BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(BG_DARK);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 28, 12, 28));

        JLabel title = new JLabel("My To-Do List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        counterLabel = new JLabel("Loading...");
        counterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        counterLabel.setForeground(TEXT_MUTED);
        counterLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(counterLabel);
        return header;
    }

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));

        panel.add(buildInputRow(), BorderLayout.NORTH);
        panel.add(buildScrollList(), BorderLayout.CENTER);
        return panel;
    }

    // ── Input row ─────────────────────────────────────────────────────────
    private JPanel buildInputRow() {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_DARK);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setForeground(TEXT_PRIMARY);
        inputField.setBackground(BG_INPUT);
        inputField.setCaretColor(ACCENT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 60, 85), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        inputField.putClientProperty("JTextField.placeholderText", "Add a new task…");

        inputField.addActionListener(e -> addTodo());

        JButton addBtn = makeButton("+ Add", ACCENT, BG_DARK);
        addBtn.addActionListener(e -> addTodo());

        row.add(inputField, BorderLayout.CENTER);
        row.add(addBtn,     BorderLayout.EAST);
        return row;
    }

    private JScrollPane buildScrollList() {
        listModel = new DefaultListModel<>();
        todoList  = new JList<>(listModel);
        todoList.setBackground(BG_DARK);
        todoList.setForeground(TEXT_PRIMARY);
        todoList.setSelectionBackground(BG_CARD);
        todoList.setSelectionForeground(TEXT_PRIMARY);
        todoList.setFixedCellHeight(62);
        todoList.setCellRenderer(new TodoCellRenderer());
        todoList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        todoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) toggleSelected();
            }
        });

        JScrollPane scroll = new JScrollPane(todoList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.getVerticalScrollBar().setBackground(BG_DARK);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setBackground(BG_DARK);
        footer.setBorder(new EmptyBorder(8, 20, 20, 20));

        JPanel btnRow = new JPanel(new GridLayout(1, 3, 10, 0));
        btnRow.setBackground(BG_DARK);

        JButton toggleBtn = makeButton("☑  Toggle Done", ACCENT_DONE, BG_DARK);
        JButton editBtn   = makeButton("✏  Edit",        ACCENT,      BG_DARK);
        JButton deleteBtn = makeButton("🗑  Delete",      BTN_DELETE,  BG_DARK);

        toggleBtn.addActionListener(e -> toggleSelected());
        editBtn  .addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        btnRow.add(toggleBtn);
        btnRow.add(editBtn);
        btnRow.add(deleteBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setBorder(new EmptyBorder(4, 2, 0, 0));

        footer.add(btnRow,      BorderLayout.CENTER);
        footer.add(statusLabel, BorderLayout.SOUTH);
        return footer;
    }

    private JButton makeButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg.darker(), 1, true),
                new EmptyBorder(9, 14, 9, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg.darker()); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(BG_CARD);     }
        });
        return btn;
    }

    private void addTodo() {
        String title = inputField.getText().trim();
        if (title.isEmpty()) { setStatus("⚠  Please enter a task.", false); return; }

        backend.addTodo(title);
        inputField.setText("");
        refreshList();
        setStatus("Task added: \"" + title + "\"", true);
    }

    private void toggleSelected() {
        TodoBackend.TodoItem item = todoList.getSelectedValue();
        if (item == null) { setStatus("⚠  Select a task first.", false); return; }

        backend.toggleComplete(item.getId());
        refreshList();
        setStatus("☑  Toggled: \"" + item.getTitle() + "\"", true);
    }

    private void deleteSelected() {
        TodoBackend.TodoItem item = todoList.getSelectedValue();
        if (item == null) { setStatus("⚠  Select a task to delete.", false); return; }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete task: \"" + item.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        backend.deleteTodo(item.getId());
        refreshList();
        setStatus("🗑  Deleted: \"" + item.getTitle() + "\"", true);
    }

    private void editSelected() {
        TodoBackend.TodoItem item = todoList.getSelectedValue();
        if (item == null) { setStatus("⚠  Select a task to edit.", false); return; }

        String newTitle = JOptionPane.showInputDialog(
                this, "Edit task:", item.getTitle());
        if (newTitle == null || newTitle.trim().isEmpty()) return;

        backend.updateTitle(item.getId(), newTitle.trim());
        refreshList();
        setStatus("✏  Updated to: \"" + newTitle.trim() + "\"", true);
    }

    private void refreshList() {
        int selectedId = -1;
        if (todoList.getSelectedValue() != null)
            selectedId = todoList.getSelectedValue().getId();

        listModel.clear();
        List<TodoBackend.TodoItem> todos = backend.loadTodos();
        todos.forEach(listModel::addElement);

        final int fid = selectedId;
        for (int i = 0; i < listModel.getSize(); i++) {
            if (listModel.getElementAt(i).getId() == fid) {
                todoList.setSelectedIndex(i); break;
            }
        }

        long done  = todos.stream().filter(TodoBackend.TodoItem::isCompleted).count();
        counterLabel.setText(done + " of " + todos.size() + " task(s) completed");
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setForeground(ok ? ACCENT_DONE : new Color(252, 140, 87));
        statusLabel.setText(msg);
    }

    static class TodoCellRenderer implements ListCellRenderer<TodoBackend.TodoItem> {
        @Override
        public Component getListCellRendererComponent(
                JList<? extends TodoBackend.TodoItem> list,
                TodoBackend.TodoItem item,
                int index,
                boolean isSelected,
                boolean hasFocus) {

            JPanel cell = new JPanel(new BorderLayout(14, 0));
            cell.setBackground(isSelected ? new Color(38, 38, 60) : new Color(24, 24, 36));
            cell.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, new Color(40, 40, 58)),
                    new EmptyBorder(10, 16, 10, 16)
            ));

            JLabel dot = new JLabel(item.isCompleted() ? "●" : "○");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            dot.setForeground(item.isCompleted()
                    ? new Color(72, 199, 142)
                    : new Color(80, 80, 110));
            dot.setPreferredSize(new Dimension(28, 28));

            JLabel lbl = new JLabel(item.getTitle());
            lbl.setFont(new Font("Segoe UI", item.isCompleted() ? Font.PLAIN : Font.PLAIN, 15));
            lbl.setForeground(item.isCompleted()
                    ? new Color(90, 100, 120)
                    : new Color(225, 225, 240));

            if (item.isCompleted()) {
                lbl.setText("<html><strike>" + item.getTitle() + "</strike></html>");
            }

            JLabel idBadge = new JLabel("#" + item.getId());
            idBadge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            idBadge.setForeground(new Color(65, 65, 90));

            cell.add(dot,     BorderLayout.WEST);
            cell.add(lbl,     BorderLayout.CENTER);
            cell.add(idBadge, BorderLayout.EAST);
            return cell;
        }
    }

    public static void main(String[] args) {
        // Use system look-and-feel as base, then override with custom colours
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(ToDoFrontend::new);
    }
}
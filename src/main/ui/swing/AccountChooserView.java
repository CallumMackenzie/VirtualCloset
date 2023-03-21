package ui.swing;

import model.Account;
import model.AccountManager;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

// A view to choose and edit accounts
public class AccountChooserView extends View {

    private static final int SELECTED_ACCOUNT_VIEW_X = 1;
    private static final int SELECTED_ACCOUNT_VIEW_Y = 0;
    private static final int CREATE_ACCOUNT_VIEW_X = 1;
    private static final int CREATE_ACCOUNT_VIEW_Y = 4;

    private static final String CREATE_ACCOUNT_STR = "Name for New Account";

    private final AccountManager accountManager;
    private Account selectedAccount;

    private JLabel activeAccountNameField;

    private JList<Account> accountJList;
    private JScrollPane accountJListScrollPane;

    private JLabel accountNameField;
    private JTextField accountNameEditField;
    private JButton setAccountNameButton;
    private JButton setActiveButton;
    private JButton deleteAccountButton;
    private boolean deleteConfirmed;

    private JButton createAccountButton;
    private JTextField createAccountNameField;

    // EFFECTS: Creates a new account chooser view from the given account manager
    public AccountChooserView(AccountManager accountManager) {
        super();
        this.accountManager = accountManager;
    }

    // REQUIRES: this.addAccountSelectedView has been called
    // EFFECTS: Sets selected account
    // MODIFIES: this
    private void setSelectedAccount(Account a) {
        this.selectedAccount = a;
        this.accountNameField.setText(a == null ? "No Account Selected" :
                ("Selected Account: " + a.getName()));
        this.accountNameEditField.setEditable(a != null);
        this.accountNameEditField.setText("");
        this.setAccountNameButton.setEnabled(a != null);
        this.setActiveButton.setText(a == null ? "Set No Active Account"
                : "Set as Active Account");
        this.deleteAccountButton.setEnabled(a != null);
        this.deleteConfirmed = false;
        this.deleteAccountButton.setText("Delete Account");
    }

    // REQUIRES: this.addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes active account components
    private void refreshActiveAccountComponents() {
        if (accountManager.hasActiveAccount()) {
            this.activeAccountNameField.setText("Active Account: "
                    + accountManager.getActiveAccount().get().getName());
        } else {
            this.activeAccountNameField.setText("No Active Account");
        }
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.addAccountListView();
        this.addAccountSelectedView();
        this.addCreateAccountView();

        this.setSelectedAccount(null);
        this.refreshActiveAccountComponents();
    }

    // REQUIRES: addAccountListView has not been called before
    // MODIFIES: this
    // EFFECTS: Sets up and adds account list view
    private void addAccountListView() {
        JLabel accountsLabel = new JLabel("Account List");
        this.add(accountsLabel, GBC.at(0, 0).center());

        this.accountJList = new JList<>();
        this.accountJList.setCellRenderer(AccountListItem::new);
        this.refreshAccountListData();
        this.accountJListScrollPane = new JScrollPane(this.accountJList);
        this.add(this.accountJListScrollPane,
                GBC.at(0, 1)
                        .north()
                        .fillBoth()
                        .weight(0.5, 1)
                        .insets(5)
                        .gridheight(GridBagConstraints.REMAINDER));
    }

    // REQUIRES: addAccountListView has been called
    // MODIFIES: this
    // EFFECTS: Sets listeners for the account list view
    private void setAccountListViewListeners() {
        this.accountJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                int idx = accountJList.getSelectedIndex();
                if (idx >= 0 && idx < this.accountManager.getAccounts().size()) {
                    this.setSelectedAccount(this.accountManager
                            .getAccounts().get(accountJList.getSelectedIndex()));
                } else {
                    this.setSelectedAccount(null);
                }
            }
        });
    }

    // REQUIRES: this.addAccountCreationView has not been called
    // MODIFIES: this
    // EFFECTS: Sets up and adds the account creation view
    private void addCreateAccountView() {
        final int x = CREATE_ACCOUNT_VIEW_X;
        final int y = CREATE_ACCOUNT_VIEW_Y;

        this.createAccountButton = new JButton("Create Account");
        this.add(createAccountButton, GBC.hFillNorth(x + 1, y + 1)
                .insets(2));

        this.createAccountNameField = PromptedTextField.prompt(CREATE_ACCOUNT_STR);
        this.add(createAccountNameField, GBC.hFillNorth(x, y + 1)
                .insets(2));
    }

    // MODIFIES: this
    // EFFECTS: Sets up listeners for create account view components
    private void setCreateAccountViewListeners() {
        this.createAccountButton.addActionListener(e -> {
            String t = createAccountNameField.getText();
            if (!t.isEmpty() && !t.equals(CREATE_ACCOUNT_STR)) {
                accountManager.addAccount(new Account(t));
                createAccountNameField.setText("");
                this.refreshAccountListData();
            }
        });
    }

    // REQUIRES: this.addAccountSelectedView has not been called
    // MODIFIES: this
    // EFFECTS: Sets up and adds the account selected view
    private void addAccountSelectedView() {
        final int x = SELECTED_ACCOUNT_VIEW_X;
        final int y = SELECTED_ACCOUNT_VIEW_Y;

        this.activeAccountNameField = new JLabel();
        this.add(this.activeAccountNameField,
                GBC.hFillNorth(x, y).gridwidth(2).insets(2));

        this.accountNameField = new JLabel();
        this.add(this.accountNameField,
                GBC.hFillNorth(x, y + 1).gridwidth(2).insets(2));

        this.accountNameEditField = PromptedTextField.prompt("Account Name");
        this.add(this.accountNameEditField,
                GBC.hFillNorth(x, y + 2).weightx(0.5).insets(2));

        this.setAccountNameButton = new JButton("Set Account Name");
        this.add(this.setAccountNameButton,
                GBC.hFillNorth(x + 1, y + 2).weightx(0.2).insets(2));

        this.setActiveButton = new JButton("Set as Active Account");
        this.add(this.setActiveButton, GBC.hFillNorth(x + 1, y + 3).insets(2));

        this.deleteAccountButton = new JButton("Delete Account");
        this.add(this.deleteAccountButton, GBC.hFillNorth(x, y + 3).insets(2));
    }

    // REQUIRES: addAccountSelectedView has been called
    // MODIFIES: this
    // EFFECTS: Sets listeners for selected account controls
    private void setAccountSelectedListeners() {
        this.setAccountNameButton.addActionListener(this::onSetSelectedAccountName);
        this.setActiveButton.addActionListener(this::onSetActiveAccount);
        this.deleteAccountButton.addActionListener(this::onRemoveActiveAccount);
    }

    // MODIFIES: this
    // EFFECTS: Sets selected account name
    private void onSetSelectedAccountName(ActionEvent e) {
        Optional<Account> activeBefore = this.accountManager.getActiveAccount();
        int selectedIndex = this.accountJList.getSelectedIndex();
        this.accountManager.setActiveAccount(selectedAccount.getName());
        this.accountManager.setActiveAccountName(accountNameEditField.getText());
        if (activeBefore.isPresent()) {
            this.accountManager.setActiveAccount(activeBefore.get().getName());
        } else {
            this.accountManager.removeActiveAccount();
        }
        this.setSelectedAccount(selectedAccount);
        this.refreshAccountListData();
        this.refreshActiveAccountComponents();
        this.accountJList.setSelectedIndex(selectedIndex);
    }

    // MODIFIES: this
    // EFFECTS: Sets active account
    private void onSetActiveAccount(ActionEvent e) {
        int selectedIdx = this.accountJList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < this.accountManager.getAccounts().size()) {
            Account selected = this.accountManager.getAccounts().get(selectedIdx);
            this.accountManager.setActiveAccount(selected.getName());
        } else {
            this.accountManager.removeActiveAccount();
        }
        this.refreshActiveAccountComponents();
    }

    // MODIFIES: this
    // EFFECTS: Removes the given account if confirmed
    private void onRemoveActiveAccount(ActionEvent e) {
        int selectedIdx = this.accountJList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < this.accountManager.getAccounts().size()) {
            if (this.deleteConfirmed) {
                this.accountManager.removeAccount(selectedAccount.getName());
                this.refreshAccountListData();
                this.refreshActiveAccountComponents();
                this.setSelectedAccount(null);
            } else {
                this.deleteConfirmed = true;
                this.deleteAccountButton.setText("Press Again to Delete");
            }
        } else {
            deleteConfirmed = false;
            this.deleteAccountButton.setText("Delete Account");
        }
    }

    // REQUIRES: accountJList != null
    // MODIFIES: this
    // EFFECTS: Refreshes account list data
    private void refreshAccountListData() {
        this.accountJList.setListData(this.accountManager.getAccounts()
                .toArray(new Account[0]));
    }

    @Override
    void addEventListeners() {
        this.setAccountListViewListeners();
        this.setAccountSelectedListeners();
        this.setCreateAccountViewListeners();
    }

    // A list view item for an account
    private class AccountListItem extends JPanel {

        private final Account account;

        // EFFECTS: Constructs a new account list item
        public AccountListItem(JList<? extends Account> list,
                               Account value,
                               int index,
                               boolean isSelected,
                               boolean cellHasFocus) {
            this.account = value;

            if (isSelected) {
                this.setBackground(UIManager
                        .getDefaults().getColor("List.selectionBackground"));
            }
            JTextField name = new JTextField(account.getName());
            name.setEditable(false);
            this.add(name);
        }

    }
}

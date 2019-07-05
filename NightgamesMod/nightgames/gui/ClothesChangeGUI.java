package nightgames.gui;

import nightgames.characters.Character;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSorter;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ClothesChangeGUI extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = -912778444912041408L;
    private Character character;
    private CompletableFuture<Boolean> changeClothes;
    private JLabel appearanceLabel;
    private JLabel exposureLabel;
    DefaultListModel<Clothing> closetListModel;
    DefaultListModel<Clothing> outfitListModel;
    private ClothingList outfitList;
    private ClothingList closetList;

    private void removeAllClothing() {
        character.closet.addAll(character.outfitPlan);
        character.outfitPlan.clear();
        character.change();
        refreshLists();
    }

    private void remove(Clothing article) {
        if (article == null) {
            return;
        }
        if (!character.outfitPlan.contains(article)) {
            System.err.println("Error: tried to remove nonexistent article: " + article.getName());
            return;
        }
        character.outfitPlan.remove(article);
        character.closet.add(article);
        character.change();
        refreshLists();
        if (outfitListModel.size() > 0 ) {
            outfitList.setSelectedIndex(0);
        }
    }

    private void add(Clothing article) {
        if (article == null) {
            return;
        }
        if (!character.closet.contains(article)) {
            System.err.println("Error: tried to equip nonexistent article: " + article.getName());
            return;
        }
        // remove the article from the closet
        character.closet.remove(article);
        // change to make sure everything is equipped correctly
        character.change();
        // get the currently equipped items
        Set<Clothing> unequipped = new HashSet<Clothing>(character.outfit.getEquipped());
        // equip the new item
        character.outfit.equip(article);
        // get {previously equipped} - {currently equipped} to see what was
        // unequipped
        unequipped.removeAll(character.outfit.getEquipped());
        // add all the unequipped items back into the closet
        character.closet.addAll(unequipped);
        // make the outfit plan the currently equipped items
        character.outfitPlan.clear();
        character.outfitPlan.addAll(character.outfit.getEquipped());
        // make sure the player is dressed correctly
        character.change();
        // refresh the ClothingLists
        refreshLists();
        if (closetListModel.size() > 0 ) {
            closetList.setSelectedIndex(0);
        }
    }

    private void refreshLists() {
        closetListModel.clear();
        List<Clothing> tempList = new ArrayList<>(character.closet);
        tempList.sort(new ClothingSorter());
        tempList.forEach(article -> closetListModel.addElement(article));
        outfitListModel.clear();
        tempList = new ArrayList<>(character.outfit.getEquipped());
        tempList.sort(new ClothingSorter());
        tempList.forEach(article -> outfitListModel.addElement(article));
        DecimalFormat format = new DecimalFormat("#.##");
        appearanceLabel.setText("Attractiveness: " + format.format(character.body.getHotness()));
        exposureLabel.setText("Exposure: " + format.format(character.outfit.getExposure()));
        GUI.gui.refresh();
    }

    private void styleButton(JButton button) {
        button.setOpaque(true);
        button.setForeground(Color.white);
        button.setBackground(Color.DARK_GRAY);
    }

    ClothesChangeGUI(Character character, CompletableFuture<Boolean> changeClothes) {
        this.character = character;
        this.changeClothes = changeClothes;
        setBackground(GUIColor.BG_DARK.color);
        setForeground(GUIColor.TEXT_LIGHT.color);
        setLayout(new BorderLayout());

        int width = GUI.gui.getWidth();
        int height = GUI.gui.getHeight();
        int strutSize = (height - 500) / 3;
        int listWidth = (width - 400) / 3;

        Box closetBox = Box.createVerticalBox();
        closetListModel = new DefaultListModel<>();
        closetList = new ClothingList(closetListModel);
        closetList.setBackground(GUIColor.BG_LIGHT.color);
        closetList.setForeground(GUIColor.TEXT_LIGHT.color);
        closetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel closetLabel = new JLabel("Closet");
        closetLabel.setForeground(GUIColor.TEXT_LIGHT.color);
        closetBox.add(closetLabel);
        JScrollPane closetListPane = new JScrollPane(closetList);
        closetListPane.setMinimumSize(new Dimension(listWidth, 0));
        closetBox.add(closetListPane);

        JButton removeall = new JButton("Remove All");
        Box centerChangePanel = Box.createVerticalBox();
        JButton addButton = new JButton("Add ->");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(100, 50));
        JButton removeButton = new JButton("<- Remove");
        styleButton(removeall);
        styleButton(addButton);
        styleButton(removeButton);

        removeButton.setMaximumSize(new Dimension(100, 50));
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerChangePanel.add(Box.createVerticalStrut(strutSize));
        centerChangePanel.add(addButton);
        centerChangePanel.add(Box.createVerticalStrut(strutSize));
        centerChangePanel.add(removeButton);
        centerChangePanel.add(Box.createVerticalStrut(strutSize));
        centerChangePanel.setOpaque(false);

        Box outfitBox = Box.createVerticalBox();
        outfitBox.setOpaque(false);
        outfitListModel = new DefaultListModel<>();
        outfitList = new ClothingList(outfitListModel);
        outfitList.setBackground(GUIColor.BG_LIGHT.color);
        outfitList.setForeground(GUIColor.TEXT_LIGHT.color);
        outfitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane outfitListPane = new JScrollPane(outfitList);
        outfitListPane.setMinimumSize(new Dimension(listWidth, 0));
        outfitListPane.setPreferredSize(new Dimension(listWidth, height));

        JLabel outfitLabel = new JLabel("Current Outfit");
        outfitLabel.setForeground(GUIColor.TEXT_LIGHT.color);
        outfitBox.add(outfitLabel);
        outfitBox.add(outfitListPane);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(arg0 -> changeClothes.complete(true));
        styleButton(btnOk);
        btnOk.setAlignmentX(CENTER_ALIGNMENT);
        addButton.addActionListener(aevent -> add(closetList.getSelectedValue()));
        removeButton.addActionListener(aevent -> remove(outfitList.getSelectedValue()));
        removeall.addActionListener(aevent -> removeAllClothing());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(closetBox, BorderLayout.CENTER);
        leftPanel.add(new JLabel(), BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(listWidth, 100));
        leftPanel.setOpaque(false);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(outfitBox, BorderLayout.CENTER);
        rightPanel.add(removeall, BorderLayout.SOUTH);
        rightPanel.setPreferredSize(new Dimension(listWidth, 100));
        JPanel centerPanel = new JPanel(new BorderLayout());
        Box cBPanel = Box.createHorizontalBox();
        cBPanel.add(centerChangePanel);
        cBPanel.setOpaque(false);
        centerPanel.add(cBPanel, BorderLayout.CENTER);
        centerPanel.setMinimumSize(new Dimension(200, 0));
        centerPanel.setPreferredSize(new Dimension(100, 0));
        Box labelPanel = Box.createVerticalBox();
        appearanceLabel = new JLabel("Appearance: ");
        appearanceLabel.setToolTipText("Bonus to your natural body charisma and hotness");
        exposureLabel = new JLabel("Exposure: ");
        exposureLabel.setToolTipText("How much of your natural body charisma and hotness is exposed");
        labelPanel.add(appearanceLabel);
        labelPanel.add(exposureLabel);
        appearanceLabel.setForeground(GUIColor.TEXT_LIGHT.color);
        exposureLabel.setForeground(GUIColor.TEXT_LIGHT.color);

        Box miscPanel = Box.createHorizontalBox();
        miscPanel.add(labelPanel);
        miscPanel.add(Box.createHorizontalStrut(20));
        miscPanel.add(btnOk);
        miscPanel.setAlignmentX(CENTER_ALIGNMENT);
        centerChangePanel.add(miscPanel);
        centerChangePanel.add(Box.createVerticalStrut(20));
        centerChangePanel.add(Box.createVerticalGlue());
        centerPanel.setOpaque(false);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        refreshLists();
    }
}

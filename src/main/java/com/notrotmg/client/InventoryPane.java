package com.notrotmg.client;

import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.protocol.servertoclient.InventorySlotSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;
import com.notrotmg.protocol.servertoclient.ItemSnapshot;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Compact, game-style view for private inventory and equipped-item state. */
public final class InventoryPane extends VBox {
    private static final List<EquipmentSlot> EQUIPMENT_SLOTS = List.of(EquipmentSlot.values());
    private static final int INVENTORY_COLUMNS = 4;

    private final GridPane equipmentGrid = new GridPane();
    private final GridPane inventoryGrid = new GridPane();
    private final Button equipButton = new Button("Equip");
    private final Button dropButton = new Button("Drop");
    private final HBox actions = new HBox(7, equipButton, dropButton);
    private final SpriteCatalog spriteCatalog;
    private final IntConsumer equipHandler;
    private final IntConsumer dropHandler;
    private final Consumer<EquipmentSlot> unequipHandler;

    private InventorySnapshot currentSnapshot;
    private int selectedInventorySlot = -1;

    public InventoryPane(
            SpriteCatalog spriteCatalog,
            IntConsumer equipHandler,
            IntConsumer dropHandler,
            Consumer<EquipmentSlot> unequipHandler
    ) {
        super(9);
        this.spriteCatalog = Objects.requireNonNull(spriteCatalog);
        this.equipHandler = Objects.requireNonNull(equipHandler);
        this.dropHandler = Objects.requireNonNull(dropHandler);
        this.unequipHandler = Objects.requireNonNull(unequipHandler);

        getStyleClass().add("inventory-pane");
        setPrefWidth(304);
        setMinWidth(304);
        equipmentGrid.setHgap(5);
        inventoryGrid.setHgap(5);
        inventoryGrid.setVgap(5);

        configureActionButton(equipButton, "equip-button");
        configureActionButton(dropButton, "drop-button");
        equipButton.setOnAction(event -> equipSelectedItem());
        dropButton.setOnAction(event -> dropSelectedItem());

        actions.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(
                equipmentGrid,
                inventoryGrid,
                actions
        );
        showWaitingState();
    }

    public void render(InventorySnapshot snapshot) {
        currentSnapshot = Objects.requireNonNull(snapshot);
        if (selectedItem() == null) {
            selectedInventorySlot = -1;
        }
        renderEquipment();
        renderInventory();
        updateActions();
    }

    private void renderEquipment() {
        equipmentGrid.getChildren().clear();
        for (int index = 0; index < EQUIPMENT_SLOTS.size(); index++) {
            EquipmentSlot slot = EQUIPMENT_SLOTS.get(index);
            ItemSnapshot item = currentSnapshot.equippedItems().get(slot);
            Button button = itemButton(slot.name(), item, false);
            button.setDisable(item == null);
            if (item != null) {
                button.setOnAction(event -> unequipHandler.accept(slot));
            }
            equipmentGrid.add(button, index, 0);
        }
    }

    private void renderInventory() {
        inventoryGrid.getChildren().clear();
        for (InventorySlotSnapshot slot : currentSnapshot.inventorySlots()) {
            Button button = itemButton(Integer.toString(slot.index() + 1), slot.item(),
                    slot.index() == selectedInventorySlot);
            button.setOnAction(event -> selectInventorySlot(slot.index()));
            inventoryGrid.add(
                    button,
                    slot.index() % INVENTORY_COLUMNS,
                    slot.index() / INVENTORY_COLUMNS
            );
        }
    }

    private Button itemButton(String slotText, ItemSnapshot item, boolean selected) {
        Label slotLabel = new Label(slotText);
        slotLabel.getStyleClass().add("slot-label");

        Label name = new Label(item == null ? "Empty" : item.name());
        name.getStyleClass().add("item-name");
        if (item == null) {
            name.getStyleClass().add("empty-item-name");
        }
        name.setMaxWidth(58);
        name.setTextOverrun(OverrunStyle.ELLIPSIS);

        VBox content = new VBox(1, slotLabel, itemIcon(item), name);
        content.setAlignment(Pos.CENTER);

        Button button = new Button();
        button.setGraphic(content);
        button.setFocusTraversable(false);
        button.setPrefSize(65, 79);
        button.setMinSize(65, 79);
        button.setPadding(new Insets(2));
        button.getStyleClass().add("inventory-slot");
        if (selected) {
            button.getStyleClass().add("selected-inventory-slot");
        }
        if (item != null) {
            button.setTooltip(new Tooltip(item.name() + " (" + readable(item.itemType()) + ")"));
        }
        return button;
    }

    private Node itemIcon(ItemSnapshot item) {
        if (item == null) {
            Region emptyIcon = new Region();
            emptyIcon.getStyleClass().add("empty-item-icon");
            emptyIcon.setMinSize(32, 32);
            emptyIcon.setPrefSize(32, 32);
            emptyIcon.setMaxSize(32, 32);
            return emptyIcon;
        }

        ImageView icon = new ImageView(spriteCatalog.itemSprite(item.itemType()));
        icon.setFitWidth(32);
        icon.setFitHeight(32);
        icon.setPreserveRatio(true);
        icon.setSmooth(false);
        return icon;
    }

    private void selectInventorySlot(int inventorySlot) {
        selectedInventorySlot = itemInSlot(inventorySlot) == null ? -1 : inventorySlot;
        renderInventory();
        updateActions();
    }

    private void equipSelectedItem() {
        ItemSnapshot item = selectedItem();
        if (item != null && item.equipmentSlot() != null) {
            equipHandler.accept(selectedInventorySlot);
        }
    }

    private void dropSelectedItem() {
        if (selectedItem() != null) {
            dropHandler.accept(selectedInventorySlot);
        }
    }

    private void updateActions() {
        ItemSnapshot item = selectedItem();
        boolean itemSelected = item != null;
        actions.setVisible(itemSelected);
        actions.setManaged(itemSelected);
        equipButton.setDisable(!itemSelected || item.equipmentSlot() == null);
        dropButton.setDisable(!itemSelected);
    }

    private ItemSnapshot selectedItem() {
        if (currentSnapshot == null || selectedInventorySlot < 0) {
            return null;
        }
        return itemInSlot(selectedInventorySlot);
    }

    private void showWaitingState() {
        equipmentGrid.getChildren().clear();
        inventoryGrid.getChildren().clear();
        actions.setVisible(false);
        actions.setManaged(false);
    }

    private ItemSnapshot itemInSlot(int inventorySlot) {
        if (currentSnapshot == null) {
            return null;
        }
        return currentSnapshot.inventorySlots().stream()
                .filter(slot -> slot.index() == inventorySlot)
                .filter(slot -> slot.item() != null)
                .map(InventorySlotSnapshot::item)
                .findFirst()
                .orElse(null);
    }

    private static void configureActionButton(Button button, String styleClass) {
        button.setFocusTraversable(false);
        button.setPrefWidth(86);
        button.getStyleClass().addAll("inventory-action-button", styleClass);
    }

    private static String readable(Enum<?> value) {
        return value.name().toLowerCase().replace('_', ' ');
    }
}

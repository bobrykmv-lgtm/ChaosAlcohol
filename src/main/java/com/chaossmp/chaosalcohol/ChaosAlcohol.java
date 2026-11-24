package com.chaossmp.chaosalcohol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChaosAlcohol extends JavaPlugin implements Listener {

    private static final String CATALOG_TITLE = ChatColor.GOLD.toString() + ChatColor.BOLD + "Таверна ChaosSMP";
    private static final String RECIPE_TITLE_PREFIX = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Рецепт: ";

    private NamespacedKey alcoholKey;
    private final Map<String, DrinkDefinition> drinks = new LinkedHashMap<>();
    private final List<NamespacedKey> registeredRecipeKeys = new ArrayList<>();

    @Override
    public void onEnable() {
        alcoholKey = new NamespacedKey(this, "drink_type");

        registerDrinkDefinitions();
        registerAllRecipes();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ChaosAlcohol успешно запущен (" + drinks.size() + " напитков)!");
    }

    @Override
    public void onDisable() {
        for (NamespacedKey key : registeredRecipeKeys) {
            Bukkit.removeRecipe(key);
        }
        registeredRecipeKeys.clear();
        drinks.clear();
        getLogger().info("ChaosAlcohol выключен.");
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда доступна только игрокам.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("alcohol") || command.getName().equalsIgnoreCase("drinks")) {
            openCatalog(player);
            return true;
        }

        return false;
    }

    private void registerDrinkDefinitions() {
        drinks.put("samogon", new DrinkDefinition(
                "samogon",
                ChatColor.WHITE + "" + ChatColor.BOLD + "Самогонка",
                "Крепкий деревенский напиток",
                Color.fromRGB(150, 160, 170),
                Material.WHEAT,
                new String[]{
                        "WSW",
                        "CPC",
                        "   "
                },
                Map.of(
                        'W', new RecipeChoice.MaterialChoice(Material.WHEAT),
                        'S', new RecipeChoice.MaterialChoice(Material.SUGAR),
                        'P', new RecipeChoice.MaterialChoice(Material.POTATO),
                        'C', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.RED + "Сила III " + ChatColor.DARK_GRAY + "(30 сек)",
                        ChatColor.GRAY + "• " + ChatColor.DARK_GREEN + "Тошнота I " + ChatColor.DARK_GRAY + "(15 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.STRENGTH, 600, 2, false, true, true),
                        new PotionEffect(PotionEffectType.NAUSEA, 300, 0, false, true, true)
                )
        ));

        drinks.put("mead", new DrinkDefinition(
                "mead",
                ChatColor.GOLD + "" + ChatColor.BOLD + "Медовуха",
                "Сладкий и согревающий напиток",
                Color.fromRGB(225, 210, 110),
                Material.HONEYCOMB,
                new String[]{
                        "CSC",
                        "SMS",
                        "   "
                },
                Map.of(
                        'C', new RecipeChoice.MaterialChoice(Material.HONEYCOMB),
                        'S', new RecipeChoice.MaterialChoice(Material.SUGAR),
                        'M', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.YELLOW + "Сопротивление II " + ChatColor.DARK_GRAY + "(40 сек)",
                        ChatColor.GRAY + "• " + ChatColor.LIGHT_PURPLE + "Регенерация I " + ChatColor.DARK_GRAY + "(10 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.RESISTANCE, 800, 1, false, true, true),
                        new PotionEffect(PotionEffectType.REGENERATION, 200, 0, false, true, true)
                )
        ));

        drinks.put("liqueur", new DrinkDefinition(
                "liqueur",
                ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Лесная настойка",
                "На пахучих лесных ягодах",
                Color.fromRGB(110, 120, 75),
                Material.SWEET_BERRIES,
                new String[]{
                        "SGS",
                        " P ",
                        "   "
                },
                Map.of(
                        'S', new RecipeChoice.MaterialChoice(Material.SWEET_BERRIES),
                        'G', new RecipeChoice.MaterialChoice(Material.GLOW_BERRIES),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.AQUA + "Скорость II " + ChatColor.DARK_GRAY + "(30 сек)",
                        ChatColor.GRAY + "• " + ChatColor.GREEN + "Прыгучесть II " + ChatColor.DARK_GRAY + "(30 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.SPEED, 600, 1, false, true, true),
                        new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 1, false, true, true)
                )
        ));

        drinks.put("beer", new DrinkDefinition(
                "beer",
                ChatColor.YELLOW + "" + ChatColor.BOLD + "Шахтерское пиво",
                "Добавляет сил под землей",
                Color.fromRGB(65, 70, 75),
                Material.COAL,
                new String[]{
                        "WCW",
                        "WPW",
                        "   "
                },
                Map.of(
                        'W', new RecipeChoice.MaterialChoice(Material.WHEAT),
                        'C', new RecipeChoice.MaterialChoice(Material.COAL),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.GOLD + "Спешка II " + ChatColor.DARK_GRAY + "(60 сек)",
                        ChatColor.GRAY + "• " + ChatColor.BLUE + "Ночное зрение " + ChatColor.DARK_GRAY + "(60 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.HASTE, 1200, 1, false, true, true),
                        new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0, false, true, true)
                )
        ));

        drinks.put("dragons_breath", new DrinkDefinition(
                "dragons_breath",
                ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Драконье дыхание",
                "Крайне опасный эксперимент",
                Color.fromRGB(145, 65, 155),
                Material.DRAGON_BREATH,
                new String[]{
                        "MDM",
                        " P ",
                        "   "
                },
                Map.of(
                        'M', new RecipeChoice.MaterialChoice(Material.MAGMA_CREAM),
                        'D', new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.GOLD + "Огнестойкость " + ChatColor.DARK_GRAY + "(60 сек)",
                        ChatColor.GRAY + "• " + ChatColor.YELLOW + "Поглощение III " + ChatColor.DARK_GRAY + "(30 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0, false, true, true),
                        new PotionEffect(PotionEffectType.ABSORPTION, 600, 2, false, true, true)
                )
        ));

        drinks.put("cranberry_punch", new DrinkDefinition(
                "cranberry_punch",
                ChatColor.RED + "" + ChatColor.BOLD + "Ягодный пунш",
                "Бодрящий коктейль для резких атак",
                Color.fromRGB(190, 45, 70),
                Material.APPLE,
                new String[]{
                        "RAR",
                        " P ",
                        "   "
                },
                Map.of(
                        'R', new RecipeChoice.MaterialChoice(Material.SWEET_BERRIES),
                        'A', new RecipeChoice.MaterialChoice(Material.APPLE),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.AQUA + "Скорость III " + ChatColor.DARK_GRAY + "(25 сек)",
                        ChatColor.GRAY + "• " + ChatColor.LIGHT_PURPLE + "Регенерация I " + ChatColor.DARK_GRAY + "(15 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.SPEED, 500, 2, false, true, true),
                        new PotionEffect(PotionEffectType.REGENERATION, 300, 0, false, true, true)
                )
        ));

        drinks.put("phantom_elixir", new DrinkDefinition(
                "phantom_elixir",
                ChatColor.AQUA + "" + ChatColor.BOLD + "Призрачный эликсир",
                "Скрывает шаги и смягчает падения",
                Color.fromRGB(115, 195, 215),
                Material.PHANTOM_MEMBRANE,
                new String[]{
                        "MPM",
                        " S ",
                        "   "
                },
                Map.of(
                        'M', new RecipeChoice.MaterialChoice(Material.PHANTOM_MEMBRANE),
                        'S', new RecipeChoice.MaterialChoice(Material.SUGAR),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.GRAY + "Невидимость " + ChatColor.DARK_GRAY + "(35 сек)",
                        ChatColor.GRAY + "• " + ChatColor.WHITE + "Плавное падение " + ChatColor.DARK_GRAY + "(35 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.INVISIBILITY, 700, 0, false, true, true),
                        new PotionEffect(PotionEffectType.SLOW_FALLING, 700, 0, false, true, true)
                )
        ));

        drinks.put("hell_grog", new DrinkDefinition(
                "hell_grog",
                ChatColor.DARK_RED + "" + ChatColor.BOLD + "Адский грог",
                "Обжигающий напиток глубин",
                Color.fromRGB(185, 70, 25),
                Material.BLAZE_POWDER,
                new String[]{
                        "BNB",
                        " P ",
                        "   "
                },
                Map.of(
                        'B', new RecipeChoice.MaterialChoice(Material.BLAZE_POWDER),
                        'N', new RecipeChoice.MaterialChoice(Material.NETHER_WART),
                        'P', new RecipeChoice.MaterialChoice(Material.POTION)
                ),
                List.of(
                        ChatColor.GRAY + "• " + ChatColor.RED + "Сила II " + ChatColor.DARK_GRAY + "(35 сек)",
                        ChatColor.GRAY + "• " + ChatColor.GOLD + "Огнестойкость " + ChatColor.DARK_GRAY + "(50 сек)",
                        ChatColor.GRAY + "• " + ChatColor.DARK_GREEN + "Тошнота I " + ChatColor.DARK_GRAY + "(10 сек)"
                ),
                List.of(
                        new PotionEffect(PotionEffectType.STRENGTH, 700, 1, false, true, true),
                        new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000, 0, false, true, true),
                        new PotionEffect(PotionEffectType.NAUSEA, 200, 0, false, true, true)
                )
        ));
    }

    private void registerAllRecipes() {
        for (DrinkDefinition def : drinks.values()) {
            NamespacedKey key = new NamespacedKey(this, def.id());
            ShapedRecipe recipe = new ShapedRecipe(key, createDrinkItem(def, true));
            recipe.shape(def.shape());
            for (Map.Entry<Character, RecipeChoice> entry : def.ingredients().entrySet()) {
                recipe.setIngredient(entry.getKey(), entry.getValue());
            }
            Bukkit.addRecipe(recipe);
            registeredRecipeKeys.add(key);
        }
    }

    private ItemStack createDrinkItem(DrinkDefinition def, boolean includeHint) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(def.displayName());
            meta.setColor(def.color());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.ITALIC + "" + ChatColor.GRAY + def.tagline());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Эффекты:");
            lore.addAll(def.effectDescriptions());
            if (includeHint) {
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "Рецепты: /alcohol");
            }

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES);
            meta.getPersistentDataContainer().set(alcoholKey, PersistentDataType.STRING, def.id());
            item.setItemMeta(meta);
        }
        return item;
    }

    private void openCatalog(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, CATALOG_TITLE);
        fillGlass(inv, Material.BLACK_STAINED_GLASS_PANE);

        int[] slots = {10, 11, 12, 13, 14, 15, 16, 22};
        int index = 0;

        for (DrinkDefinition def : drinks.values()) {
            if (index >= slots.length) break;
            ItemStack preview = createDrinkItem(def, false);
            ItemMeta meta = preview.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GREEN + "▶ Нажми, чтобы посмотреть рецепт");
                meta.setLore(lore);
                preview.setItemMeta(meta);
            }
            inv.setItem(slots[index++], preview);
        }

        player.openInventory(inv);
    }

    private void openRecipeView(Player player, DrinkDefinition def) {
        Inventory inv = Bukkit.createInventory(null, 27, RECIPE_TITLE_PREFIX + ChatColor.stripColor(def.displayName()));
        fillGlass(inv, Material.GRAY_STAINED_GLASS_PANE);

        int[] gridSlots = {1, 2, 3, 10, 11, 12, 19, 20, 21};
        String[] rows = def.shape();

        for (int r = 0; r < 3; r++) {
            String row = rows[r];
            for (int c = 0; c < 3; c++) {
                char symbol = row.charAt(c);
                int slot = gridSlots[r * 3 + c];
                if (symbol == ' ') {
                    inv.setItem(slot, new ItemStack(Material.AIR));
                    continue;
                }
                RecipeChoice choice = def.ingredients().get(symbol);
                if (choice instanceof RecipeChoice.MaterialChoice matChoice && !matChoice.getChoices().isEmpty()) {
                    Material mat = matChoice.getChoices().get(0);
                    ItemStack ingredient = new ItemStack(mat);
                    ItemMeta meta = ingredient.getItemMeta();
                    if (meta != null) {
                        meta.setLore(List.of(ChatColor.DARK_GRAY + "Ингредиент верстака"));
                        ingredient.setItemMeta(meta);
                    }
                    inv.setItem(slot, ingredient);
                }
            }
        }

        inv.setItem(15, createDrinkItem(def, false));

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад к напиткам");
            back.setItemMeta(backMeta);
        }
        inv.setItem(26, back);

        player.openInventory(inv);
    }

    private void fillGlass(Inventory inv, Material glassMaterial) {
        ItemStack pane = new ItemStack(glassMaterial);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            pane.setItemMeta(meta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        boolean isCatalog = title.equals(CATALOG_TITLE);
        boolean isRecipe = title.startsWith(RECIPE_TITLE_PREFIX);

        if (!isCatalog && !isRecipe) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        int slot = event.getRawSlot();
        if (isCatalog) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            ItemMeta meta = clicked.getItemMeta();
            if (meta == null) return;
            String drinkId = meta.getPersistentDataContainer().get(alcoholKey, PersistentDataType.STRING);
            if (drinkId != null && drinks.containsKey(drinkId)) {
                openRecipeView(player, drinks.get(drinkId));
            }
        } else if (isRecipe && slot == 26) {
            openCatalog(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (NamespacedKey key : registeredRecipeKeys) {
            player.discoverRecipe(key);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.hasItemMeta()) return;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) return;

        String drinkId = meta.getPersistentDataContainer().get(alcoholKey, PersistentDataType.STRING);
        if (drinkId == null) return;

        DrinkDefinition def = drinks.get(drinkId);
        if (def == null) return;

        Player player = event.getPlayer();
        for (PotionEffect effect : def.effects()) {
            player.addPotionEffect(effect);
        }
    }

    private record DrinkDefinition(
            String id,
            String displayName,
            String tagline,
            Color color,
            Material icon,
            String[] shape,
            Map<Character, RecipeChoice> ingredients,
            List<String> effectDescriptions,
            List<PotionEffect> effects
    ) {}
}

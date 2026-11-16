package com.chaossmp.chaosalcohol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class ChaosAlcohol extends JavaPlugin implements Listener {

    private NamespacedKey alcoholKey;

    @Override
    public void onEnable() {
        alcoholKey = new NamespacedKey(this, "drink_type");

        // Реєструємо слухач подій (вхід гравця та вживання напоїв)
        getServer().getPluginManager().registerEvents(this, this);

        // Реєструємо точні фіксовані рецепти
        registerRecipes();

        getLogger().info("ChaosAlcohol успішно активовано з точними рецептами та ефектами!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChaosAlcohol вимкнено.");
    }

    private void registerRecipes() {
        // 1. Самогонка (Пшениця, Цукор, Картопля)
        NamespacedKey samogonKey = new NamespacedKey(this, "samogon");
        ShapedRecipe samogonRecipe = new ShapedRecipe(samogonKey, createDrink("samogon", ChatColor.WHITE + "Самогонка", List.of(ChatColor.GRAY + "Міцний сільський напій")));
        samogonRecipe.shape("WSW", "CPC");
        samogonRecipe.setIngredient('W', Material.WHEAT);
        samogonRecipe.setIngredient('S', Material.SUGAR);
        samogonRecipe.setIngredient('P', Material.POTATO);
        Bukkit.addRecipe(samogonRecipe);

        // 2. Медовуха (Стільники, Цукор, Пляшка води)
        NamespacedKey meadKey = new NamespacedKey(this, "mead");
        ShapedRecipe meadRecipe = new ShapedRecipe(meadKey, createDrink("mead", ChatColor.GOLD + "Медовуха", List.of(ChatColor.GRAY + "Солодкий та зігріваючий напій")));
        meadRecipe.shape("CSC", "SMS");
        meadRecipe.setIngredient('C', Material.HONEYCOMB);
        meadRecipe.setIngredient('S', Material.SUGAR);
        meadRecipe.setIngredient('M', Material.POTION);
        Bukkit.addRecipe(meadRecipe);

        // 3. Лісова настойка (Солодкі ягоди, Світяться ягоди, Пляшка води)
        NamespacedKey liqueurKey = new NamespacedKey(this, "liqueur");
        ShapedRecipe liqueurRecipe = new ShapedRecipe(liqueurKey, createDrink("liqueur", ChatColor.LIGHT_PURPLE + "Лісова настойка", List.of(ChatColor.GRAY + "На пахучих лісових ягодах")));
        liqueurRecipe.shape("SGS", " P ");
        liqueurRecipe.setIngredient('S', Material.SWEET_BERRIES);
        liqueurRecipe.setIngredient('G', Material.GLOW_BERRIES);
        liqueurRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(liqueurRecipe);

        // 4. Шахтарське пиво (Пшениця, Вугілля, Пляшка води)
        NamespacedKey beerKey = new NamespacedKey(this, "beer");
        ShapedRecipe beerRecipe = new ShapedRecipe(beerKey, createDrink("beer", ChatColor.YELLOW + "Шахтарське пиво", List.of(ChatColor.GRAY + "Додає сил під землею")));
        beerRecipe.shape("WCW", "WPW");
        beerRecipe.setIngredient('W', Material.WHEAT);
        beerRecipe.setIngredient('C', Material.COAL);
        beerRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(beerRecipe);

        // 5. Драконяче дихання (Магмовий крем, Дихання дракона, Пляшка води)
        NamespacedKey dragonKey = new NamespacedKey(this, "dragons_breath");
        ShapedRecipe dragonRecipe = new ShapedRecipe(dragonKey, createDrink("dragons_breath", ChatColor.DARK_PURPLE + "Драконяче дихання", List.of(ChatColor.RED + "Вкрай небезпечний експеримент")));
        dragonRecipe.shape("MDM", " P ");
        dragonRecipe.setIngredient('M', Material.MAGMA_CREAM);
        dragonRecipe.setIngredient('D', Material.DRAGON_BREATH);
        dragonRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(dragonRecipe);
    }

    // Допоміжний метод для створення кастомної пляшки з унікальним тегом
    private ItemStack createDrink(String id, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(alcoholKey, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Автоматичне відкриття всіх рецептів гравцю одразу при вході на сервер
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.discoverRecipe(new NamespacedKey(this, "samogon"));
        player.discoverRecipe(new NamespacedKey(this, "mead"));
        player.discoverRecipe(new NamespacedKey(this, "liqueur"));
        player.discoverRecipe(new NamespacedKey(this, "beer"));
        player.discoverRecipe(new NamespacedKey(this, "dragons_breath"));
    }

    // Обробка ефектів під час вживання кастомних напоїв
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.hasItemMeta()) return;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        String drinkId = meta.getPersistentDataContainer().get(alcoholKey, PersistentDataType.STRING);
        if (drinkId == null) return; // Пропускаємо звичайні зенілки

        Player player = event.getPlayer();

        // Видаємо ефекти відповідно до обраного напою
        switch (drinkId) {
            case "samogon":
                // Сила III (ампліфікатор 2 = рівень 3) на 30 секунд (600 тіків)
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2, false, true, true));
                // Нудота на 15 секунд (300 тіків)
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 0, false, true, true));
                break;
            case "mead":
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 800, 1, false, true, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0, false, true, true));
                break;
            case "liqueur":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1, false, true, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 1, false, true, true));
                break;
            case "beer":
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 1200, 1, false, true, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0, false, true, true));
                break;
            case "dragons_breath":
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0, false, true, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 2, false, true, true));
                break;
        }
    }
}

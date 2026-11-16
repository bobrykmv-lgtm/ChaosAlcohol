package com.chaossmp.chaosalcohol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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

        // Регистрируем события (вход игрока и употребление напитков)
        getServer().getPluginManager().registerEvents(this, this);

        // Регистрируем точные рецепты верстака
        registerRecipes();

        getLogger().info("ChaosAlcohol успешно запущен с кастомными цветами и рецептами!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChaosAlcohol выключен.");
    }

    private void registerRecipes() {
        // 1. Самогонка (Светло-серая)
        NamespacedKey samogonKey = new NamespacedKey(this, "samogon");
        ShapedRecipe samogonRecipe = new ShapedRecipe(samogonKey, createDrink("samogon", ChatColor.WHITE + "Самогонка", List.of(ChatColor.GRAY + "Крепкий деревенский напиток"), Color.fromRGB(150, 160, 170)));
        samogonRecipe.shape("WSW", "CPC");
        samogonRecipe.setIngredient('W', Material.WHEAT);
        samogonRecipe.setIngredient('S', Material.SUGAR);
        samogonRecipe.setIngredient('P', Material.POTATO);
        Bukkit.addRecipe(samogonRecipe);

        // 2. Медовуха (Желтая / Медовая)
        NamespacedKey meadKey = new NamespacedKey(this, "mead");
        ShapedRecipe meadRecipe = new ShapedRecipe(meadKey, createDrink("mead", ChatColor.GOLD + "Медовуха", List.of(ChatColor.GRAY + "Сладкий и согревающий напиток"), Color.fromRGB(225, 210, 110)));
        meadRecipe.shape("CSC", "SMS");
        meadRecipe.setIngredient('C', Material.HONEYCOMB);
        meadRecipe.setIngredient('S', Material.SUGAR);
        meadRecipe.setIngredient('M', Material.POTION);
        Bukkit.addRecipe(meadRecipe);

        // 3. Лесная настойка (Зеленоватая)
        NamespacedKey liqueurKey = new NamespacedKey(this, "liqueur");
        ShapedRecipe liqueurRecipe = new ShapedRecipe(liqueurKey, createDrink("liqueur", ChatColor.LIGHT_PURPLE + "Лесная настойка", List.of(ChatColor.GRAY + "На пахучих лесных ягодах"), Color.fromRGB(110, 120, 75)));
        liqueurRecipe.shape("SGS", " P ");
        liqueurRecipe.setIngredient('S', Material.SWEET_BERRIES);
        liqueurRecipe.setIngredient('G', Material.GLOW_BERRIES);
        liqueurRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(liqueurRecipe);

        // 4. Шахтерское пиво (Темно-серая)
        NamespacedKey beerKey = new NamespacedKey(this, "beer");
        ShapedRecipe beerRecipe = new ShapedRecipe(beerKey, createDrink("beer", ChatColor.YELLOW + "Шахтерское пиво", List.of(ChatColor.GRAY + "Добавляет сил под землей"), Color.fromRGB(65, 70, 75)));
        beerRecipe.shape("WCW", "WPW");
        beerRecipe.setIngredient('W', Material.WHEAT);
        beerRecipe.setIngredient('C', Material.COAL);
        beerRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(beerRecipe);

        // 5. Драконье дыхание (Фиолетовая)
        NamespacedKey dragonKey = new NamespacedKey(this, "dragons_breath");
        ShapedRecipe dragonRecipe = new ShapedRecipe(dragonKey, createDrink("dragons_breath", ChatColor.DARK_PURPLE + "Драконье дыхание", List.of(ChatColor.RED + "Крайне опасный эксперимент"), Color.fromRGB(145, 65, 155)));
        dragonRecipe.shape("MDM", " P ");
        dragonRecipe.setIngredient('M', Material.MAGMA_CREAM);
        dragonRecipe.setIngredient('D', Material.DRAGON_BREATH);
        dragonRecipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(dragonRecipe);
    }

    // Метод создания кастомного напитка с заданным цветом жидкости
    private ItemStack createDrink(String id, String name, List<String> lore, Color color) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.setColor(color); // Устанавливаем точный цвет зелья
            meta.getPersistentDataContainer().set(alcoholKey, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Автоматическое открытие всех рецептов игроку при входе на сервер
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.discoverRecipe(new NamespacedKey(this, "samogon"));
        player.discoverRecipe(new NamespacedKey(this, "mead"));
        player.discoverRecipe(new NamespacedKey(this, "liqueur"));
        player.discoverRecipe(new NamespacedKey(this, "beer"));
        player.discoverRecipe(new NamespacedKey(this, "dragons_breath"));
    }

    // Выдача эффектов при употреблении кастомного напитка
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.hasItemMeta()) return;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        String drinkId = meta.getPersistentDataContainer().get(alcoholKey, PersistentDataType.STRING);
        if (drinkId == null) return;

        Player player = event.getPlayer();

        switch (drinkId) {
            case "samogon":
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2, false, true, true)); // Сила III
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 0, false, true, true));   // Тошнота
                break;
            case "mead":
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 800, 1, false, true, true)); // Сопротивление
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0, false, true, true)); // Регенерация
                break;
            case "liqueur":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1, false, true, true)); // Скорость
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, 1, false, true, true)); // Прыгучесть
                break;
            case "beer":
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 1200, 1, false, true, true)); // Спешка
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0, false, true, true)); // Ночное зрение
                break;
            case "dragons_breath":
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0, false, true, true)); // Огнестойкость
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 2, false, true, true)); // Поглощение
                break;
        }
    }
}

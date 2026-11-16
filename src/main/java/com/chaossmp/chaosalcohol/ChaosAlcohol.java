package com.chaossmp.chaosalcohol;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public final class ChaosAlcohol extends JavaPlugin implements Listener, CommandExecutor {

    private NamespacedKey itemDataKey;

    @Override
    public void onEnable() {
        itemDataKey = new NamespacedKey(this, "chaos_alcohol_type");

        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("alcohol") != null) {
            getCommand("alcohol").setExecutor(this);
        }

        registerAllRecipes();
        getLogger().info("Плагин ChaosAlcohol успешно активирован со всеми напитками!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChaosAlcohol выключен.");
    }

    // Универсальный метод создания кастомных алкогольных напитков
    private ItemStack createAlcoholItem(String type) {
        ItemStack bottle = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) bottle.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.getPersistentDataContainer().set(itemDataKey, PersistentDataType.STRING, type);

            switch (type) {
                case "samogon" -> {
                    meta.setBasePotionType(PotionType.WEAKNESS); // Серая мутная пляшка
                    meta.setDisplayName("§7§lСамогонка");
                    meta.setLore(List.of(
                            "§7Крепость: §c60°",
                            "§7Эффекты: §aСила III (30с)",
                            "§7Побочка: §cСильное головокружение",
                            "",
                            "§8Настоящий деревенский напиток!"
                    ));
                }
                case "mead" -> {
                    meta.setColor(Color.fromRGB(230, 190, 40)); // Золотистый
                    meta.setDisplayName("§e§lМедовуха");
                    meta.setLore(List.of(
                            "§7Крепость: §e15°",
                            "§7Эффекты: §aРегенерация I (20с), Спешка I (30с)",
                            "§7Побочка: §aПочти нет, очень мягкий напиток",
                            "",
                            "§8Сладкое питье для строителей и фермеров."
                    ));
                }
                case "liqueur" -> {
                    meta.setColor(Color.fromRGB(160, 30, 90)); // Ярко-красный / ягодный
                    meta.setDisplayName("§c§lЛесная Настойка");
                    meta.setLore(List.of(
                            "§7Крепость: §d35°",
                            "§7Эффекты: §aНочное зрение (45с), Прыгучесть II (30с)",
                            "§7Побочка: §cЛегкая тошнота",
                            "",
                            "§8Настояна на лесных и светящихся ягодах."
                    ));
                }
                case "beer" -> {
                    meta.setColor(Color.fromRGB(110, 70, 40)); // Темно-коричневый
                    meta.setDisplayName("§6§lШахтерское Пиво");
                    meta.setLore(List.of(
                            "§7Крепость: §620°",
                            "§7Эффекты: §aПоглощение (60с), Сопротивление I (30с)",
                            "§7Побочка: §cУсталость в первые 10 секунд",
                            "",
                            "§8Лучший выбор перед спуском в глубокую шахту."
                    ));
                }
                case "dragon_breath" -> {
                    meta.setColor(Color.fromRGB(240, 100, 10)); // Огненно-оранжевый
                    meta.setDisplayName("§4§lДраконье Дыхание");
                    meta.setLore(List.of(
                            "§7Крепость: §499°",
                            "§7Эффекты: §aОгнестойкость (90с), Скорость II (45с)",
                            "§7Побочка: §4Левитация и слепота",
                            "",
                            "§8Экстремальный эликсир для безумцев."
                    ));
                }
            }
            bottle.setItemMeta(meta);
        }
        return bottle;
    }

    // Регистрация крафтов для всех напитков
    private void registerAllRecipes() {
        // 1. Самогонка (Пляшка води + цукор + пшениця + картопля)
        ShapelessRecipe samogon = new ShapelessRecipe(new NamespacedKey(this, "craft_samogon"), createAlcoholItem("samogon"));
        samogon.addIngredient(Material.POTION);
        samogon.addIngredient(Material.SUGAR);
        samogon.addIngredient(Material.WHEAT);
        samogon.addIngredient(Material.POTATO);
        Bukkit.addRecipe(samogon);

        // 2. Медовуха (Пляшка води + 2x Медові стільники + цукор)
        ShapelessRecipe mead = new ShapelessRecipe(new NamespacedKey(this, "craft_mead"), createAlcoholItem("mead"));
        mead.addIngredient(Material.POTION);
        mead.addIngredient(Material.HONEYCOMB);
        mead.addIngredient(Material.HONEYCOMB);
        mead.addIngredient(Material.SUGAR);
        Bukkit.addRecipe(mead);

        // 3. Ягідна наливка (Пляшка води + 2x Солодкі ягоди + Світяться ягоди)
        ShapelessRecipe liqueur = new ShapelessRecipe(new NamespacedKey(this, "craft_liqueur"), createAlcoholItem("liqueur"));
        liqueur.addIngredient(Material.POTION);
        liqueur.addIngredient(Material.SWEET_BERRIES);
        liqueur.addIngredient(Material.SWEET_BERRIES);
        liqueur.addIngredient(Material.GLOW_BERRIES);
        Bukkit.addRecipe(liqueur);

        // 4. Шахтарське пиво (Пляшка води + 3x Пшениця + Вугілля)
        ShapelessRecipe beer = new ShapelessRecipe(new NamespacedKey(this, "craft_beer"), createAlcoholItem("beer"));
        beer.addIngredient(Material.POTION);
        beer.addIngredient(Material.WHEAT);
        beer.addIngredient(Material.WHEAT);
        beer.addIngredient(Material.WHEAT);
        beer.addIngredient(Material.COAL);
        Bukkit.addRecipe(beer);

        // 5. Драконівський подих (Готовий Самогон + Дихання дракона + Магма-крем)
        ShapelessRecipe dragon = new ShapelessRecipe(new NamespacedKey(this, "craft_dragon"), createAlcoholItem("dragon_breath"));
        dragon.addIngredient(Material.POTION);
        dragon.addIngredient(Material.DRAGON_BREATH);
        dragon.addIngredient(Material.MAGMA_CREAM);
        Bukkit.addRecipe(dragon);
    }

    // Адмін-команда для видачі: /alcohol <samogon/mead/liqueur/beer/dragon>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        if (!player.hasPermission("chaosalcohol.admin") && !player.isOp()) {
            player.sendMessage("§cУ вас нет прав!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§eИспользование: §7/alcohol <samogon | mead | liqueur | beer | dragon>");
            return true;
        }

        String type = args[0].toLowerCase();
        ItemStack item = createAlcoholItem(type);
        
        if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) {
            player.getInventory().addItem(item);
            player.sendMessage("§aВы получили напиток: " + item.getItemMeta().getDisplayName());
        } else {
            player.sendMessage("§cНеизвестный тип напитка! Доступно: samogon, mead, liqueur, beer, dragon");
        }
        return true;
    }

    // Обработка употребления напитков и их эффектов
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() != Material.POTION || !item.hasItemMeta()) return;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        String type = meta.getPersistentDataContainer().get(itemDataKey, PersistentDataType.STRING);
        if (type == null) return;

        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 0.7f);

        switch (type) {
            case "samogon" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 30, 2)); // Сила III
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 20, 0));   // Тошнота
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 0)); // Замедление
                player.sendMessage("§6*Ух, крепкая!* §7Голову повело, но в руках появилась дикая сила!");
            }
            case "mead" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 30, 0)); // Спешка
                player.sendMessage("§e*Сладко и тепло.* §7Тело расслабилось, работать станет легче!");
            }
            case "liqueur" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 45, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 30, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 0));
                player.sendMessage("§c*Ягодный вкус!* §7В темноте стало все видно, и будто тянет прыгать!");
            }
            case "beer" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 30, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 10, 0)); // Усталость в начале
                player.sendMessage("§6*Горьковато.* §7Броня крепчает, но сначала нужно немного разогреться.");
            }
            case "dragon_breath" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 90, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 45, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 3, 0)); // Подкидывает вверх
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0));
                player.sendMessage("§4*ПОЛНЫЙ ДЫМ!* §7Глаза выжигает, но теперь вам не страшен ни огонь, ни враги!");
            }
        }
    }
}

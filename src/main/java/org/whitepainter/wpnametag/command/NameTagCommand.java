package org.whitepainter.wpnametag.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.whitepainter.wpnametag.NameTagManager;
import org.whitepainter.wpnametag.gui.NameTagGUI;
import org.whitepainter.wpnametag.io.data.NameTag;
import org.whitepainter.wpnametag.io.data.PlayerData;
import org.whitepainter.wpnametag.io.instance.IOManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class NameTagCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;
        if(length == 0 || (length == 1 && args[0].equals("help"))){
            if(checkPermission(sender, "nametag.gui", false)) sender.sendMessage("/nametag gui "+ChatColor.GREEN+"打开称号背包");
            if(checkPermission(sender, "nametag.create", false)) sender.sendMessage("/nametag create [称号ID] "+ChatColor.GREEN+"将手中物品保存为称号");
            if(checkPermission(sender, "nametag.delete", false)) sender.sendMessage("/nametag delete <称号ID> "+ChatColor.GREEN+"删除一个称号");
            if(checkPermission(sender, "nametag.get", false)) sender.sendMessage("/nametag get <称号ID> "+ChatColor.GREEN+"获取称号物品");
            if(checkPermission(sender, "nametag.list", false)) sender.sendMessage("/nametag list "+ChatColor.GREEN+"显示所有称号");
            if(checkPermission(sender, "nametag.display", false)) sender.sendMessage("/nametag display <玩家> "+ChatColor.GREEN+"显示玩家拥有的所有称号");
            if(checkPermission(sender, "nametag.reset", false)) sender.sendMessage("/nametag reset <玩家> "+ChatColor.GREEN+"重置玩家称号");
            if(checkPermission(sender, "nametag.give", false)) sender.sendMessage("/nametag give <玩家> <称号ID> "+ChatColor.GREEN+"给予玩家称号");
            if(checkPermission(sender, "nametag.remove", false)) sender.sendMessage("/nametag remove <玩家> <称号ID> "+ChatColor.GREEN+"移除玩家称号");
            if(checkPermission(sender, "nametag.set", false)) sender.sendMessage("/nametag set <玩家> <称号ID> "+ChatColor.GREEN+"设置玩家已有称号");
            if(checkPermission(sender, "nametag.set-temporary", false)) sender.sendMessage("/nametag set-temporary <玩家> <称号ID> "+ChatColor.GREEN+"设置玩家临时称号");
            if(checkPermission(sender, "nametag.reset-temporary", false)) sender.sendMessage("/nametag reset-temporary <玩家> "+ChatColor.GREEN+"重置玩家临时称号");
            return true;
        }
        String name = args[0];
        if(name.equalsIgnoreCase("gui")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            if(sender instanceof Player player){
                new NameTagGUI(player).open(player);
            }else{
                sender.sendMessage(ChatColor.RED+"只有玩家可以使用这条命令");
            }
        }else if(name.equalsIgnoreCase("create")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            if(sender instanceof Player player){
                ItemStack itemStack = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
                ItemMeta itemMeta = itemStack.getItemMeta();
                if(itemMeta != null){
                    if(itemMeta.hasDisplayName()){
                        String key;
                        if(length >= 2){
                            key = args[1];
                        }else{
                            key = ChatColor.stripColor(itemMeta.getDisplayName());
                        }
                        if(NameTagManager.isIllegalLength(key)){
                            sender.sendMessage(ChatColor.RED + "创建称号失败，称号iD的长度过长");
                            return true;
                        }
                        if(NameTagManager.containsIllegalCharacters(key)){
                            sender.sendMessage(ChatColor.RED + "创建称号失败，称号iD中含有非法字符");
                            return true;
                        }
                        if(NameTagManager.createNameTag(key, itemStack)){
                            sender.sendMessage("成功创建称号'" + key + "'");
                        }else{
                            sender.sendMessage(ChatColor.RED + "创建称号失败，未知的错误");
                        }
                    }else{
                        sender.sendMessage(ChatColor.RED + "你需要先给物品设置一个名字");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED + "手中持有的为无效物品");
                }
            }else{
                sender.sendMessage(ChatColor.RED+"只有玩家可以使用这条命令");
            }
        }else if(name.equalsIgnoreCase("delete")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            if(length >= 2){
                String key = args[1];
                if(NameTagManager.containsSQLNameTag(key)){
                    NameTagManager.deleteNameTag(key);
                    sender.sendMessage("成功删除称号");
                    sender.sendMessage(ChatColor.GRAY+"*注: 此项改动不会影响已拥有此称号的玩家");
                }else{
                    sender.sendMessage(ChatColor.RED+"删除失败，SQL中不存在此称号，请尝试刷新称号");
                }
            }else{
                sender.sendMessage(ChatColor.RED+"参数不足");
            }
        }else if(name.equalsIgnoreCase("reload")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            NameTagManager.reload();
            sender.sendMessage("称号重载完毕");
        }else if(name.equalsIgnoreCase("get")){
            if(sender instanceof Player player){
                if(length >= 2){
                    String key = args[1];
                    NameTag nameTag = NameTagManager.getNameTag(key);
                    if(nameTag != null){
                        player.getInventory().addItem(nameTag.getItemStack());
                        sender.sendMessage("成功获取称号物品");
                    }else{
                        sender.sendMessage(ChatColor.RED+"获取称号物品失败，请确认是否存在此称号并在使用"+ChatColor.GOLD+"/nametag reload"+ChatColor.RED+"刷新后重试");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"参数不足");
                }
            }else{
                sender.sendMessage(ChatColor.RED+"手中未持有物品");
            }
        }else if(name.equalsIgnoreCase("list")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            Collection<NameTag> nameTags = NameTagManager.getNameTags();
            sender.sendMessage("所有可用的称号");
            for(NameTag nameTag : nameTags) sender.sendMessage("称号ID:"+nameTag.getKey()+" 称号显示名:"+nameTag.getFormat());
        }else if(name.equalsIgnoreCase("display")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            if(length >= 2){
                String playerName = args[1];
                Player player = Bukkit.getPlayerExact(playerName);
                if(player == null){
                    sender.sendMessage(ChatColor.RED+"该玩家处于离线状态");
                    return true;
                }
                sender.sendMessage("玩家'"+playerName+"'拥有的称号");
                PlayerData playerData = IOManager.getPlayerData(playerName);
                if(playerData == null){
                    sender.sendMessage(ChatColor.RED+"获取此玩家的数据失败");
                    return true;
                }
                Collection<String> nameTagKeys = playerData.getOwnedNameTagKey();
                for(String key : nameTagKeys){
                    NameTag nameTag = NameTagManager.getNameTag(key);
                    if(nameTag == null){
                        sender.sendMessage("称号ID:"+key+" 无效的称号");
                    }else{
                        sender.sendMessage("称号ID:"+key+" 称号显示名:"+nameTag.getFormat());
                    }
                }
            }else{
                sender.sendMessage(ChatColor.RED+"参数不足");
            }
        }else if(name.equalsIgnoreCase("reset")){
            if(!checkPermission(sender, "nametag."+name, true)) return true;
            if(length >= 2){
                String playerName = args[1];
                Player player = Bukkit.getPlayerExact(playerName);
                if(player == null){
                    sender.sendMessage(ChatColor.RED+"该玩家处于离线状态");
                    return true;
                }
                PlayerData playerData = IOManager.getPlayerData(playerName);
                if(playerData == null){
                    sender.sendMessage(ChatColor.RED+"获取此玩家的数据失败");
                    return true;
                }
                playerData.setUsedNameTag(null);
                sender.sendMessage("成功重置玩家称号");
            }else{
                sender.sendMessage(ChatColor.RED+"参数不足");
            }
        }else if(length >= 3){
            String playerName = args[1];
            String key = args[2];
            NameTag nameTag = NameTagManager.getNameTag(key);
            if(nameTag != null){
                Player player = Bukkit.getPlayerExact(playerName);
                if(player == null){
                    sender.sendMessage(ChatColor.RED+"该玩家处于离线状态");
                    return true;
                }
                PlayerData playerData = IOManager.getPlayerData(playerName);
                if(playerData == null){
                    sender.sendMessage(ChatColor.RED+"获取此玩家的数据失败");
                    return true;
                }
                if(name.equalsIgnoreCase("give")){
                    if(!checkPermission(sender, "nametag."+name, true)) return true;
                    if(playerData.giveNameTag(key)){
                        sender.sendMessage("成功给予玩家称号");
                    }else{
                        sender.sendMessage(ChatColor.RED+"给予玩家称号失败");
                    }
                }else if(name.equalsIgnoreCase("remove")){
                    if(!checkPermission(sender, "nametag."+name, true)) return true;
                    if(playerData.removeNameTag(key)){
                        sender.sendMessage("成功删除玩家称号");
                    }else{
                        sender.sendMessage(ChatColor.RED+"删除玩家称号失败");
                    }
                }else if(name.equalsIgnoreCase("set")){
                    if(!checkPermission(sender, "nametag."+name, true)) return true;
                    if(playerData.setUsedNameTag(key)){
                        sender.sendMessage("成功设置玩家称号");
                    }else{
                        sender.sendMessage(ChatColor.RED+"设置玩家称号失败");
                    }
                }else if(name.equalsIgnoreCase("set-temporary")){
                    if(!checkPermission(sender, "nametag."+name, true)) return true;
                    playerData.setTemporaryNameTag(key);
                    sender.sendMessage("成功设置玩家临时称号");
                }else if(name.equalsIgnoreCase("reset-temporary")){
                    if(!checkPermission(sender, "nametag."+name, true)) return true;
                    playerData.setTemporaryNameTag(null);
                    sender.sendMessage("成功重置玩家临时称号");
                }else{
                    sender.sendMessage(ChatColor.RED+"未知的参数");
                }
            }else{
                sender.sendMessage(ChatColor.RED+"无效的称号");
            }
        }else{
            sender.sendMessage(ChatColor.RED+"未知的命令，请用"+ChatColor.GOLD+"/nametag help"+ChatColor.RED+"查看帮助");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;
        List<String> list = new ArrayList<>();
        if(length == 1){
            a(sender, list, "gui");
            a(sender, list, "create");
            a(sender, list, "delete");
            a(sender, list, "reload");
            a(sender, list, "get");
            a(sender, list, "list");
            a(sender, list, "display");
            a(sender, list, "give");
            a(sender, list, "remove");
            a(sender, list, "set");
            a(sender, list, "reset");
            a(sender, list, "set-temporary");
            a(sender, list, "reset-temporary");
        }else if(length == 2){
            if(checkPermission(sender, args[0], false)){
                if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("get")){
                    Collection<NameTag> nameTags = NameTagManager.getNameTags();
                    for(NameTag nameTag : nameTags){
                        list.add(nameTag.getKey());
                    }
                }else{
                    for(Player player : Bukkit.getOnlinePlayers()){
                        list.add(player.getName());
                    }
                }
            }
        }else if(length == 3){
            if(checkPermission(sender, args[0], false)){
                Collection<NameTag> nameTags = NameTagManager.getNameTags();
                for(NameTag nameTag : nameTags){
                    list.add(nameTag.getKey());
                }
            }
        }
        return list;
    }

    private void a(CommandSender sender, List<String> list, String s){
        if(checkPermission(sender, "nametag.create", false)) list.add(s);
    }

    private boolean checkPermission(CommandSender sender, String permission, boolean sendMessage){
        if(sender.hasPermission(permission)) return true;
        if(sendMessage) sender.sendMessage(ChatColor.RED+"操作失败，未拥有权限"+ChatColor.GOLD+permission);
        return false;
    }
}

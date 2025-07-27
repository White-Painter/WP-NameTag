WP-NameTag 是一个专为高版本Minecraft Spigot服务端设计的轻量级称号插件，支持MySQL数据库，允许设置临时称号，允许管理员将物品设置为称号，建议与物品编辑插件配合使用。

# ✨ 核心功能

临时称号：允许为玩家设置临时称号（适用于小游戏等场景）
MySQL支持：可配置MYSQL数据库存储
临时称号系统：自动清除的临时称号
权限绑定：称号与权限节点关联
可视化GUI：直观的称号背包界面
PlaceholderAPI集成：丰富的变量支持

# 📦 安装指南

前置要求：

安装 ProtocolLib（必须）、PlaceholderAPI（可选）

1. 下载最新版本插件
2. 将前置插件与此插件放入服务器 plugins 文件夹内
3. 重启服务器生成配置文件
4. 编辑 plugins/WP-NameTag/config.yml 配置MySQL（可选）
5. 再次重启服务器使MySQL配置生效（可选）

# ⚙️ 基础使用

## 创建称号

1. 手持拥有自定义名称的物品
2. 将手中物品创建为插件：/nametag create
3. 物品名称将成为称号唯一标识符
4. 创建后即可给予玩家并在称号背包中显示

*注：称号默认关联权限：wpnametag.<称号标识符>，拥有此权限的玩家拥有此称号的使用权，但没有所有权

# 📜 命令大全

## 操作员命令

命令			|			描述			|			权限节点

/nametag reload	重置配置文件与称号	wpnametag.command.reload

/nametag create [id]	创建新称号	wpnametag.command.create

/nametag delete <id>	删除称号	wpnametag.command.delete

/nametag get <id>	获取称号物品	wpnametag.command.get

/nametag list	列出所有称号	wpnametag.command.list

/nametag display <玩家>	查看玩家称号	wpnametag.command.display

/nametag reset <玩家>	重置玩家称号	wpnametag.command.reset

/nametag give <玩家> <id>	给予玩家称号	wpnametag.command.give

/nametag remove <玩家> <id>	移除玩家称号	wpnametag.command.remove

/nametag set <玩家> <id>	设置玩家称号	wpnametag.command.set

/nametag set-temporary <玩家> <id>	设置临时称号	wpnametag.command.settemporary

/nametag reset-temporary <玩家>	清除临时称号	wpnametag.command.resettemporary

## 普通命令

命令			|			描述			|			权限节点

/nametag gui	打开称号背包	wpnametag.command.gui

# 🌐 PlaceholderAPI 变量

变量			|			描述			|			示例

%wpnametag_used%	当前使用称号ID	"VIP玩家"

%wpnametag_used_format%	当前称号显示名称	"VIP玩家"

%wpnametag_owned_amount%	拥有称号数量	"5"

%wpnametag_owned:<称号标识符>%	是否拥有特定称号	"true"

# ⚠️ 注意事项

1. 必须使用有自定义名称的物品创建称号
2. 临时称号优先级高于普通称号
3. 推荐配合物品编辑插件使用（如Lores）
4. MySQL配置需在首次启动后修改
5. 权限绑定功能可在配置文件中禁用

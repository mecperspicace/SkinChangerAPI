![SkinChangerAPI](https://user-images.githubusercontent.com/71231848/156544216-ba367464-3743-415c-a550-913e0ae948d9.png)

---
Lightweight and easy-to-use SkinChangerAPI for Bukkit plugins. 

## Features

- Support all the Bukkit version (1.7-1.18)
- Easy to use
- Support custom skin's textures
- No dependencies needed
- Less than 250 lines of code

## Usages

### Change player's skin

The function for change the player's skin is `SkinChangerAPI.change(Player player, Property textures)` :

```java
@EventHandler
public void OnJoin(PlayerJoinEvent event) {
    SkinChangerAPI.change(event.getPlayer(), new Property("textures", skin_value, skin_signature);
}
```

### Getting player's skin

The have many way to get the player's skin textures, for get the player's skin textures you can use 3 way : `getByUUID()`, `getByName()`, `getByObject()`

**By UUID :**
```java
@EventHandler
public void OnJoin(PlayerJoinEvent event) {
    SkinChangerAPI.change(event.getPlayer(), SkinChangerAPI.getByUUID(event.getPlayer.getUniqueID()));
}
```

**By Name:**
```java
@EventHandler
public void OnJoin(PlayerJoinEvent event) {
    SkinChangerAPI.change(event.getPlayer(), SkinChangerAPI.getByName(event.getPlayer.getName()));
}
```

**By Object:**
```java
@EventHandler
public void OnJoin(PlayerJoinEvent event) {
    SkinChangerAPI.change(event.getPlayer(), SkinChangerAPI.getByObject(event.getPlayer()));
}
```

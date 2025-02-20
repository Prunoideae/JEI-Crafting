This addon adds a way to define recipes that, when a player hovers the cursor over items shown in JEI item list or bookmarks and middle clicks them, the player will be given the items while consuming some defined ingredients.

Also supports JEI+EMI or TMRV+EMI, but in some very edge cases it will have behavioral problems when true cheating is enabled.

The addon is mostly designed for modpack-making, so it is not bundled with any recipe, and modpack authors can define their own JEI crafting recipes to make certain items easier to be accessed by the player.

![Usage Example](https://github.com/Prunoideae/JEI-Crafting/blob/master/examples/2.png?raw=true)

### Rationale

Some recipes in Minecraft are actually annoying, and especially for colored building blocks, or cosmetic blocks that are just building materials but have a recipe that requires you to go through 2 or 3 crafting operations to get. For example, if you want to build a house with 5 different colors of concrete, then you need to prepare those dyes and get 5 types of concrete sands beforehand, and if you accidentally run out of the material, you need to find a crafting table and make some more.

![Oh no…](https://github.com/Prunoideae/JEI-Crafting/blob/master/examples/3.png?raw=true)

So, as the mod proposed and provided functionality for, an easier way would just remove these recipes, and let players take the concrete of different colors out directly, while consuming some basic concrete materials. Or even more radical, just let players get the building materials for free!

The same also applies to _actual machines_. For example, you have propeller, brass hand, mixer… in Create, but each item only serves as the crafting ingredient in one or two machines. When you want to relocate the factory or just rebuild some part of it, your inventory is stuffed by the machines, cogwheels, belts and shafts. And this is solvable by the mod, where you can just reduce all the machines back to the raw ingredients, and turn them into other machines when you feel like to. Recipe staging and gating is still there, as you can define what kind of materials are needed to craft different machines.

### Usage

The recipes provided by this mod can be defined using datapacks:

```
{
    "type": "jei_crafting:jei_crafting",
    "output": {
        "id": "minecraft:smoker",
        "count": 1
    },
    "ingredients": [
        {
            "ingredient": {
                "tag": "minecraft:logs"
            },
            "count": 4
        },
        {
            "ingredient": {
                "tag": "c:cobblestones"
            },
            "count": 8
        }
    ],
    "uncraftsTo": [
        {
            "id": "minecraft:oak_wood",
            "count": 4
        },
        {
            "id": "minecraft:cobblestone",
            "count": 8
        }
    ]
}
```

So we defined a JEI crafting recipe that converts 4 logs and 8 cobblestones to a smoker. The default config in JEI crafting crafts 8 times at once, and it is adjustable in config/jei/\_crafting-client.toml.

The `uncraftsTo` can be omitted to disable the uncrafting of a recipe, and the `ingredients` can be omitted to make a recipe "free", consuming no items when the output is taken out by the player:

```
{
    "type": "jei_crafting:jei_crafting",
    "output": {
        "id": "minecraft:bedrock",
        "count": 1
    }
}
```

Free recipes are automatically uncraftable, as at that time it's not very different from putting the items into trashcan.

Finally, we will have something looks like this:

![Usage Example](https://github.com/Prunoideae/JEI-Crafting/blob/master/examples/1.gif?raw=true)

In v1.1.0, you can also configure the `craftsInTicks` field to set the crafting duration required in ticks for a craft to happen, like:

[Video](https://github.com/Prunoideae/JEI-Crafting/blob/master/examples/4.mp4?raw=true)

We got compat for KJS that allows you to dynamically modify the recipe output in v1.2.0. However, KubeJS recipe support is not very available at the time, as the recipe component does not allow empty list, where it is inevitable when defining free or not uncraftable items :/ But it's possible to define the recipe using `event.custom`, like:

```
ServerEvents.recipes(event => {

    event.custom({
        type: "jei_crafting:jei_crafting",
        output: Item.of("minecraft:bedrock"),
        craftInTicks: 10
    })

    event.custom({
        type: "jei_crafting:jei_crafting",
        output: Item.of('minecraft:copper_block'),
        ingredients: [{
            ingredient: Ingredient.of('minecraft:copper_ingot'),
            count: 9,
        }],
        uncraftsTo: [
            Item.of('minecraft:copper_ingot', 8)
        ],
        craftInTicks: 20
    }).id('kubejs:foobar')
})

// When these events `are event.cancel()`ed, the crafting will fail and there'll be some
// notification sound
JeiCraftingEvents.itemCrafting('kubejs:foobar', event=>{
    // When the item is crafted, modify the output to Sussy baka instead
    event.recipeOutput = Item.of("minecraft:copper_block[custom_name='\"Sussy baka\"']")
})

JeiCraftingEvents.itemUncrafting('kubejs:foobar', event=>{
    // Overrides the original uncrafting list to another one
    event.recipeOutput = [Item.of("minecraft:copper_block[custom_name='\"Sussy baka\"']")]
})
```